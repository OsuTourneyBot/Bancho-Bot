package osu.refbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import logger.Logger;
import osu.bancho.BanchoBot;
import osu.lobby.MultiplayerLobby;
import osu.lobby.event.MultiplayerEvent;
import osu.tournamentData.Beatmap;
import osu.tournamentData.Mappool;
import osu.tournamentData.Ruleset;

public class RefBot extends Thread {

	private Logger logger;

	private MultiplayerLobby lobby;
	private Ruleset rule;
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
	private ArrayList<String> tiebreaker;

	public RefBot(BanchoBot bot, String matchTitle, Ruleset rule, Mappool mappool) {
		this.logger = Logger.getLogger();
		this.lobby = bot.makeLobby(matchTitle);
		this.rule = rule;
		this.playerTeam = new HashMap<String, Integer>();
		this.playerState = new HashMap<String, Integer>();
		this.points = new int[rule.getPlayers().length];
		this.commandHandler = new BotCommandHandler(this);
		this.rollHandler = new RollHandler(this);
		this.totalBans = 0;
		this.mappool = mappool;
		this.lobby.addHandler(commandHandler);
		this.presentPlayers = new String[rule.getPlayers().length][];
		this.validRoll = new boolean[presentPlayers.length];
		this.rolls = new int[presentPlayers.length];
		this.tiebreaker = new ArrayList<String>();

		this.remainingPicks = new HashSet<String>();
		for (String map : mappool.getShortMapNames().keySet()) {
			remainingPicks.add(map);
		}

		// Remove tiebreaker maps from the pool
		for (String map : rule.getTieBreaker()) {
			if (remainingPicks.contains(map)) {
				remainingPicks.remove(map);
				tiebreaker.add(map);
			}
		}

		// Record which player is on which team
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

	public MultiplayerLobby getLobby() {
		return lobby;
	}

	public void setPlayerState(String player, int state) {
		playerState.put(player, state);
	}

	public void setUp() {
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
		int totalPlayers = 0;
		int readyPlayers = 0;
		boolean flag = true;
		for (String player : lobby.getPlayerSlots().keySet()) {
			if (playerTeam.containsKey(player)) {
				teamSize[playerTeam.get(player)]++;
				totalPlayers++;
				if (playerState.containsKey(player) && playerState.get(player) == 1) {
					readyPlayers++;
				} else {
					flag = false;
				}
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
		} else {
			lobby.message("When all team members are here everyone type \"!ready\".");
		}
		lobby.message(readyPlayers + "/" + totalPlayers + " ready.");
		lobby.flush();
		return flag;
	}

	public String[][] getPresentPlayers() {
		return presentPlayers;
	}

	private void getRolls() {
		lobby.addHandler(rollHandler, 0);
		lobby.message("Captains, please roll \"!roll\".");
		lobby.flush();
		thisWait();
		lobby.removeHandler(rollHandler);
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
		lobby.message("FT-" + rule.getFirstTo() + " | " + rule.getNumBans() + " ban(s)");
		lobby.flush();
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
		boolean tiebreak = false;
		while (whoWon == -1) {
			// Let the players choose the map if it isn't a tiebreaker
			if (!tiebreak) {
				commandHandler.setBotCommand(BotCommand.PICK);
				lobby.message(getWhoIsPicking() + " pick your song using \"!pick <map>\".");
				lobby.message("Remaining songs: " + getRemainingPicks());
				lobby.flush();
				// Wait for the song to be chosen
				lobby.waitForEvent(MultiplayerEvent.MAP_CHANGED);
				commandHandler.setBotCommand(null);
				// Change who is picking
				whosPick = (whosPick + 1) % rule.getPlayers().length;
			} else {
				String tiebreakerID = tiebreaker.get(0);
				remainingPicks.add(tiebreakerID);
				pick(tiebreakerID);
			}
			// Start the match
			setPickedMap();
			currentPick = null;
			// Process the scores from last map and display the results
			whoWon = processScores();
			lobby.message(
					rule.getTeamNames()[0] + " | " + points[0] + "-" + points[1] + " | " + rule.getTeamNames()[1]);
			lobby.flush();
			// Check to see if we need to tiebreak
			if (tiebreaker != null && points[0] == rule.getFirstTo() - 1 && points[1] == rule.getFirstTo() - 1) {
				tiebreak = true;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		for (int i = 0; i < scores.length; i++) {
			lobby.message(rule.getTeamNames()[i] + " (Score: " + scores[i] + ")");
			if (scores[team] < scores[i]) {
				team = i;
			}
		}
		lobby.message(rule.getTeamNames()[team] + " has won the point.");
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
		lobby.closeLobby();
	}

	@Deprecated
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