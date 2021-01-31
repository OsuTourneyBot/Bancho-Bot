package osu.refbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import irc.event.Event;
import logger.Logger;
import osu.bancho.BanchoBot;
import osu.lobby.MultiplayerLobby;
import osu.refbot.event.BotCommandEvent;
import osu.refbot.event.BotEvent;
import osu.refbot.event.ReadyEventListener;
import osu.refbot.event.RollEventListener;
import osu.tournamentData.Beatmap;
import osu.tournamentData.Mappool;
import osu.tournamentData.Ruleset;

public class RefBot extends Thread {

	private Logger logger;

	private MultiplayerLobby lobby;
	private Ruleset rule;
	private HashMap<String, Integer> playerTeam;
	private HashMap<String, Integer> playerState;
	private boolean started;
	private int[] points;
	private int whosPick;
	private int whosBan;
	private int totalBans;
	private Beatmap currentPick;

	private HashSet<String> remainingPicks;
	private Mappool mappool;
	private String[][] presentPlayers;
	private ArrayList<String> tiebreaker;

	public RefBot(BanchoBot bot, String matchTitle, Ruleset rule, Mappool mappool) {
		this.logger = Logger.getLogger();
		this.lobby = bot.makeLobby(matchTitle);
		this.rule = rule;
		this.playerTeam = new HashMap<String, Integer>();
		this.playerState = new HashMap<String, Integer>();
		this.started = false;
		this.points = new int[rule.getPlayers().length];
		this.totalBans = 0;
		this.mappool = mappool;
		this.presentPlayers = new String[rule.getPlayers().length][];
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

	public int getPlayerTeam(String player) {
		return playerTeam.containsKey(player) ? playerTeam.get(player) : -1;
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
		ReadyEventListener listener = new ReadyEventListener(this);
		lobby.addEventListener(listener);
		lobby.message("When all team members are here everyone type \"!ready\".");
		lobby.flushWaitForEvent(BotEvent.ALL_READY);
		lobby.removeEventListener(listener);
		started = true;
	}

	public boolean allReady() {
		if (started) {
			return false;
		}
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
		lobby.message("Captains, please roll \"!roll\".");
		RollEventListener listener = new RollEventListener(this);
		lobby.addEventListener(listener);
		lobby.flushWaitForEvent(BotEvent.ROLLS_DONE);
		lobby.removeEventListener(listener);
		int[] rolls = listener.getRolls();
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

	private void startMatch() {
		lobby.message("FT-" + rule.getFirstTo() + " | " + rule.getNumBans() + " ban(s)");
		lobby.flush();
		banPhase();
		pickPhase();
	}

	private void banPhase() {
		while (totalBans < rule.getNumBans() * rule.getPlayers().length) {
			lobby.message(getWhoIsBanning() + " Pick your ban using \"!ban <map>\".");
			lobby.message("Remaining songs: " + getRemainingPicks());
			String player;
			String ban;
			do {
				Event event = lobby.flushWaitForEvent(BotCommandEvent.BAN);
				HashMap<String, Object> data = event.getData();
				player = (String) data.get("player");
				ban = (String) data.get("ban");
			} while (!ban(player, ban));
			// Change who is banning
			whosBan = (whosBan + 1) % rule.getPlayers().length;
		}
	}

	private void pickPhase() {
		int whoWon = -1;
		boolean tiebreak = false;
		while (whoWon == -1) {
			// Let the players choose the map if it isn't a tiebreaker
			if (!tiebreak) {
				lobby.message(getWhoIsPicking() + " pick your song using \"!pick <map>\".");
				lobby.message("Remaining songs: " + getRemainingPicks());
				// Wait for the song to be chosen
				String player;
				String pick;
				do {
					Event event = lobby.flushWaitForEvent(BotCommandEvent.BAN);
					HashMap<String, Object> data = event.getData();
					player = (String) data.get("player");
					pick = (String) data.get("pick");
				} while (!pick(player, pick));
				// Change who is picking
				whosPick = (whosPick + 1) % rule.getPlayers().length;
			} else {
				Collections.shuffle(tiebreaker);
				String tiebreakerID = tiebreaker.get(0);
				remainingPicks.add(tiebreakerID);
				pick(getWhoIsPicking(), tiebreakerID);
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

	public boolean ban(String player, String map) {
		if (!getWhoIsBanning().equalsIgnoreCase(player)) {
			return false;
		} else if (remainingPicks.contains(map)) {
			remainingPicks.remove(map);
			totalBans++;
			lobby.message("Ban [" + mappool.getShortMapName(map).getLink() + " " + map + "]: "
					+ mappool.getShortMapName(map).getFullTitle());
			lobby.flush();
			return true;
		} else {
			lobby.message("Failed to ban <" + map + ">");
			lobby.message("Either map doesn't exist or has been picked already");
			lobby.flush();
			return false;
		}
	}

	public boolean pick(String player, String map) {
		if (!getWhoIsPicking().equalsIgnoreCase(player)) {
			return false;
		} else if (remainingPicks.contains(map)) {
			remainingPicks.remove(map);
			currentPick = mappool.getShortMapName(map);
			lobby.message("Picked [" + mappool.getShortMapName(map).getLink() + " " + map + "]: "
					+ mappool.getShortMapName(map).getFullTitle());
			lobby.flush();
			return true;
		} else {
			lobby.message("Failed to pick <" + map + ">");
			lobby.message("Either map doesn't exist or has been picked already");
			lobby.flush();
			return false;
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

}