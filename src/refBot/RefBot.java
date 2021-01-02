package refBot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import bancho.BanchoBot;
import bancho.LobbyHandler;
import logger.Logger;
import tournamentData.Beatmap;
import tournamentData.Mappool;
import tournamentData.Rule;

public class RefBot extends Thread {

	private Logger logger;

	private LobbyHandler lobby;
	private Rule rule;
	private HashMap<String, Integer> playerTeam;
	private HashMap<String, Integer> playerState;
	private int[] points;
	private int whosPick;
	private int whosBan;
	private int totalBans;
	private Beatmap currentPick;

	private BotCommandHandler commandHandler;
	private RollHandler rollHandler;
	private HashSet<String> remainingPicks;
	private Mappool mappool;
	private String[][] presentPlayers;
	private boolean[] validRoll;
	private int[] rolls;

	public RefBot(BanchoBot bot, String title, Rule rule, Mappool mappool) {
		this.logger = Logger.getLogger();
		this.lobby = bot.makeLobby(title);
		this.rule = rule;
		this.playerTeam = new HashMap<String, Integer>();
		this.playerState = new HashMap<String, Integer>();
		this.points = new int[rule.getPlayers().length];
		this.commandHandler = new BotCommandHandler(this);
		this.rollHandler = new RollHandler(this);
		this.totalBans = 0;
		this.mappool = mappool;
		this.lobby.addLobbyHandler(commandHandler);
		this.presentPlayers = new String[rule.getPlayers().length][];
		this.validRoll = new boolean[presentPlayers.length];
		this.rolls = new int[presentPlayers.length];

		this.remainingPicks = new HashSet<String>();
		for (String map : mappool.getShortMapNames().keySet()) {
			remainingPicks.add(map);
		}

		for (int i = 0; i < rule.getPlayers().length; i++) {
			rolls[i] = -1;
			for (String player : rule.getPlayers()[i]) {
				this.playerTeam.put(player, i);
			}
		}
	}

	@Override
	public void run() {
		setUp();
		invitePlayers();
		waitForReadyUp();
		getRolls();
		startMatch();
	}

	public LobbyHandler getLobby() {
		return lobby;
	}

	public void setPlayerState(String player, int state) {
		playerState.put(player, state);
	}

	public void setUp() {
		lobby.message("!mp password 123456");
		lobby.message("!mp set " + rule.getTeamMode() + " " + rule.getScoreMode());
		lobby.flush();
	}

	public void invitePlayers() {
		for (String[] team : rule.getPlayers()) {
			for (String player : team) {
				lobby.message("!mp invite " + player.toLowerCase().replaceAll(" ", "_"));
			}
		}
		lobby.flush();
	}

	private void waitForReadyUp() {
		commandHandler.setBotCommand(BotCommand.READY);
		commandHandler.setWaitingToStart(true);
		lobby.message("When all team members are here everyone type \"!ready\".");
		lobby.flush();
		thisWait();
		commandHandler.setBotCommand(null);
		commandHandler.setWaitingToStart(false);
	}

	public boolean allReady() {
		int[] teamSize = new int[rule.getPlayers().length];
		boolean flag = true;
		for (String player : lobby.getPlayerSlots().keySet()) {
			if (playerTeam.containsKey(player)) {
				teamSize[playerTeam.get(player)]++;
			}
			if (!playerState.containsKey(player) || playerState.get(player) != 1) {
				flag = false;
				break;
			}
		}
		for (int i : teamSize) {
			if (i < rule.getTeamSize()) {
				flag = false;
			}
		}
		if (flag) {
			for (int i = 0; i < presentPlayers.length; i++) {
				presentPlayers[i] = new String[teamSize[i]];
			}
			int[] teamIdx = new int[presentPlayers.length];
			for (String player : lobby.getPlayerSlots().keySet()) {
				if (!playerTeam.containsKey(player))
					continue;
				int team = playerTeam.get(player);
				presentPlayers[team][teamIdx[team]++] = player;
			}
		}
		return flag;
	}

	public String[][] getPresentPlayers() {
		return presentPlayers;
	}

	private void getRolls() {
		lobby.addLobbyHandler(rollHandler, 0);
		lobby.message("Captains, please roll \"!roll\".");
		lobby.flush();
		thisWait();
		lobby.removeLobbyHanlder(rollHandler);
		if (rolls[0] > rolls[1]) {
			whosBan = 0;
			whosPick = 1;
		} else {
			whosBan = 1;
			whosPick = 0;
		}
		lobby.message(presentPlayers[whosBan][0] + " is banning first.");
		lobby.message(presentPlayers[whosPick][0] + " is picking first.");
		lobby.flush();
	}

	public void setValidRoll(String player) {
		validRoll[playerTeam.get(player)] = true;
	}

	public boolean getValidRoll(String player) {
		return validRoll[playerTeam.get(player)];
	}

	public void setRoll(String player, int roll) {
		rolls[playerTeam.get(player)] = roll;
	}

	public boolean hasRolled(String player) {
		return rolls[playerTeam.get(player)] != -1;
	}

	public boolean allRolls() {
		boolean allRolled = true;
		for (int i : rolls) {
			if (i == -1) {
				allRolled = false;
				break;
			}
		}
		return allRolled;
	}

	private void startMatch() {
		banPhase();
		pickPhase();
	}

	private void banPhase() {
		commandHandler.setBotCommand(BotCommand.BAN);
		while (totalBans < rule.getNumBans() * rule.getPlayers().length) {
			lobby.message(getWhoIsBanning() + " Pick your ban using \"!ban <map>\".");
			lobby.message("Remaining songs: " + getRemainingPicks());
			lobby.flush();
			thisWait();
			// Change who is banning
			whosBan = (whosBan + 1) % rule.getPlayers().length;
		}

		commandHandler.setBotCommand(null);
	}

	private void pickPhase() {
		int whoWon = -1;
		while (whoWon == -1) {
			commandHandler.setBotCommand(BotCommand.PICK);
			lobby.message(getWhoIsPicking() + " pick your song using \"!pick <map>\".");
			lobby.message("Remaining songs: " + getRemainingPicks());
			lobby.flush();
			// Wait for the song to be chosen
			thisWait();
			commandHandler.setBotCommand(null);
			// Start the match
			setPickedMap();
			// Process the scores from last map
			whoWon = processScores();
			// Change who is picking
			lobby.message(
					rule.getTeamNames()[0] + " | " + points[0] + "-" + points[1] + " | " + rule.getTeamNames()[1]);
			whosPick = (whosPick + 1) % rule.getPlayers().length;
		}
		lobby.message("Team " + rule.getTeamNames()[whoWon] + " has won");
		lobby.flush();
	}

	public String getWhoIsBanning() {
		return presentPlayers[whosBan][0];
	}

	public String getWhoIsPicking() {
		return presentPlayers[whosPick][0];
	}

	void ban(String map) {
		if (remainingPicks.contains(map)) {
			remainingPicks.remove(map);
			totalBans++;
			lobby.message("Ban [" + mappool.getShortMapName(map).getLink() + " " + map + "]: "
					+ mappool.getShortMapName(map).getFullTitle());
			synchronized (this) {
				this.notify();
			}
		} else {
			lobby.message("Failed to ban <" + map + ">");
			lobby.message("Either map doesn't exist or has been picked already");
		}
		lobby.flush();
	}

	void pick(String map) {
		if (remainingPicks.contains(map)) {
			lobby.message("Picked [" + mappool.getShortMapName(map).getLink() + " " + map + "]: "
					+ mappool.getShortMapName(map).getFullTitle());
			lobby.flush();

			remainingPicks.remove(map);
			currentPick = mappool.getShortMapName(map);
			synchronized (this) {
				this.notify();
			}
		} else {
			lobby.message("Failed to pick <" + map + ">");
			lobby.message("Either map doesn't exist or has been picked already");
			lobby.flush();
		}
	}

	private void setPickedMap() {
		lobby.setMap(currentPick);
		lobby.waitToStart(rule.getReadyTime(), 10);
	}

	private String getRemainingPicks() {
		String res = "";
		for (String map : remainingPicks) {
			res += ", " + map;
		}
		return res.substring(Math.min(2, res.length()));
	}

	private int processScores() {
		int team = 0;
		int[] scores = getScores();
		lobby.message("Score: " + rule.getTeamNames()[0] + " - " + scores[0]);
		for (int i = 1; i < scores.length; i++) {
			lobby.message("Score: " + rule.getTeamNames()[i] + " - " + scores[i]);
			if (scores[team] < scores[i]) {
				team = i;
			}
		}
		lobby.message("Team " + rule.getTeamNames()[team] + " has won the point.");
		lobby.flush();
		points[team]++;
		if (points[team] >= rule.getFirstTo()) {
			return team;
		} else {
			return -1;
		}
	}

	private int[] getScores() {
		int[] scores = new int[rule.getPlayers().length];
		int[] playerCount = new int[scores.length];
		for (String player : lobby.getPlayerScores().keySet()) {
			int team = playerTeam.get(player.toLowerCase());
			scores[team] += lobby.getPlayerScores().get(player);
			playerCount[team]++;
		}
		for (int i = 0; i < playerCount.length; i++) {
			if (playerCount[i] > rule.getTeamSize()) {
				logger.println(this, "Team: " + rule.getTeamNames()[i] + " played with too many players");
			}
		}
		return scores;
	}

	public void close() {
		lobby.close();
	}

	private void thisWait() {
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}