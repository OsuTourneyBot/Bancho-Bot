package refBot;

import java.util.HashMap;
import java.util.HashSet;

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
	private HashSet<String> remainingPicks;
	private Mappool mappool;
	private String[][] presentPlayers;

	public RefBot(BanchoBot bot, String title, Rule rule, Mappool mappool) {
		this.logger = Logger.getLogger();
		this.lobby = bot.makeLobby(title);
		this.rule = rule;
		this.playerTeam = new HashMap<String, Integer>();
		this.playerState = new HashMap<String, Integer>();
		this.points = new int[rule.getPlayers().length];
		this.commandHandler = new BotCommandHandler(this);
		this.totalBans = 0;
		this.mappool = mappool;
		this.lobby.addLobbyHandler(commandHandler);
		this.presentPlayers = new String[rule.getPlayers().length][];

		this.remainingPicks = new HashSet<String>();
		for (String map : mappool.getShortMapNames().keySet()) {
			remainingPicks.add(map);
		}

		for (int i = 0; i < rule.getPlayers().length; i++) {
			for (String player : rule.getPlayers()[i]) {
				playerTeam.put(player, i);
			}
		}
	}

	@Override
	public void run() {
		setUp();
		invitePlayers();
		waitForReadyup();
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

	private void waitForReadyup() {
		commandHandler.setBotCommand(BotCommand.READY);
		commandHandler.setWaitingToStart(true);
		thisWait();
		commandHandler.setBotCommand(null);
		commandHandler.setWaitingToStart(false);
	}

	public boolean allReady() {
		System.out.println("Check Ready");
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

	private void startMatch() {
		banPhase();
		pickPhase();
	}

	private void banPhase() {
		commandHandler.setBotCommand(BotCommand.BAN);
		while (totalBans < rule.getNumBans() * rule.getPlayers().length) {
			lobby.message(getWhoIsBanning() + " Pick your ban.");
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
			lobby.message(getWhoIsPicking() + " pick your song");
			lobby.flush();
			// Wait for the song to be chosen
			thisWait();
			commandHandler.setBotCommand(null);
			// Start the match
			setPickedMap();
			// Process the scores from last map
			whoWon = processScores();
			// Change who is picking
			whosPick = (whosPick + 1) % rule.getPlayers().length;
		}
		lobby.message("Team " + whoWon + " has won");
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
					+ mappool.getShortMapName(map).getTitle());
			synchronized (this) {
				this.notify();
			}
		} else {
			lobby.message("Failed to ban <" + map + ">");
			lobby.message("Either map doesn't exist or has been picked already");
		}
		lobby.message("Remaining songs: " + getRemainingPicks());
		lobby.flush();
	}

	void pick(String map) {
		if (remainingPicks.contains(map)) {
			lobby.message("Picked [" + mappool.getShortMapName(map).getLink() + " " + map + "]: "
					+ mappool.getShortMapName(map).getTitle());
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
		lobby.message("Score " + 0 + " - " + scores[0]);
		for (int i = 1; i < scores.length; i++) {
			lobby.message("Score " + i + " - " + scores[i]);
			if (scores[team] < scores[i]) {
				team = i;
			}
		}
		lobby.message("Team " + team + " has won the point.");
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
			int team = playerTeam.get(player);
			scores[team] += lobby.getPlayerScores().get(player);
			playerCount[team]++;
		}
		for (int i = 0; i < playerCount.length; i++) {
			if (playerCount[i] > rule.getTeamSize()) {
				logger.println(this, "Team " + i + " played with too many players");
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