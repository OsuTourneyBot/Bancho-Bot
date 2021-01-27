package osu.lobby;

import java.util.ArrayList;
import java.util.HashMap;

import irc.Channel;
import irc.IRCClient;
import irc.event.Event;
import irc.event.EventType;
import irc.event.WaitForEventListener;
import irc.handlers.IRCEventHandler;
import osu.lobby.event.JoinMoveLeaveListener;
import osu.lobby.event.MultiplayerEvent;
import osu.tournamentData.Beatmap;
import osu.tournamentData.Mod;

public class LobbyHandler extends Channel {

	private LobbyBanchoHandlerGroup banchoHandler;
	private ScoreHandler scoreHandler;
	private JoinMoveLeaveListener listener;
	private Beatmap currentMap;

	private HashMap<String, Integer> playerScores;
	private HashMap<String, Integer> playerMods;

	private HashMap<String, Integer> playerSlots;

	public LobbyHandler(IRCClient bancho, String name) {
		super(bancho, name);

		this.playerScores = new HashMap<String, Integer>();
		this.playerMods = new HashMap<String, Integer>();

		this.playerSlots = new HashMap<String, Integer>();

		this.banchoHandler = new LobbyBanchoHandlerGroup(new ArrayList<IRCEventHandler>());
		// TODO: do something about this so it can be easily removed later
		for (MultiplayerEvent event : MultiplayerEvent.values()) {
			this.banchoHandler.addHandler(event);
		}
		this.scoreHandler = new ScoreHandler(this);
		this.banchoHandler.addHandler(scoreHandler);
		addHandler(banchoHandler);

		listener = new JoinMoveLeaveListener(this);
		addEventListener(listener, MultiplayerEvent.PLAYER_JOIN);
		addEventListener(listener, MultiplayerEvent.PLAYER_MOVE);
		addEventListener(listener, MultiplayerEvent.PLAYER_LEAVE);
	}

	@Override
	public void close() {
		super.close();
		removeEventListener(listener, MultiplayerEvent.PLAYER_JOIN);
		removeEventListener(listener, MultiplayerEvent.PLAYER_MOVE);
		removeEventListener(listener, MultiplayerEvent.PLAYER_LEAVE);
	}

	public void closeLobby() {
		message("!mp close");
		flush();
		close();
	}

	public Beatmap getCurrentMap() {
		return currentMap;
	}

	public void setPlayerScore(String player, int score) {
		playerScores.put(player, score);
	}

	public void setPlayerMods(String player, int mods) {
		playerMods.put(player, mods);
	}

	public HashMap<String, Integer> getPlayerScores() {
		return playerScores;
	}

	public HashMap<String, Integer> getPlayerMods() {
		return playerMods;
	}

	public void setPlayerSlot(String player, int slot) {
		playerSlots.put(player, slot);
	}

	public void hasPlayerSlot(String player) {
		playerSlots.containsKey(player);
	}

	public int getPlayerSlot(String player) {
		return playerSlots.get(player);
	}

	public HashMap<String, Integer> getPlayerSlots() {
		return playerSlots;
	}

	public int removePlayerSlot(String player) {
		return playerSlots.remove(player);
	}

	public void timer(int sec) {
		message("!mp timer " + sec);
		flushWaitForEvent(MultiplayerEvent.TIMER_FINISH);
	}

	public void waitToStart(int waitTime, int startDelay) {
		message("Starting match in " + waitTime + " seconds");
		message("!mp timer " + waitTime);
		flushWaitForEvent(MultiplayerEvent.READY_OR_TIMER);
		startGame(startDelay);
	}

	public void startGame(int startDelay) {
		playerScores.clear();
		playerMods.clear();
		message("!mp start " + startDelay);
		flushWaitForEvent(MultiplayerEvent.MAP_FINISH);
	}

	public boolean setMap(Beatmap m) {
		if (m.isFreeMod()) {
			message("!mp mods Freemod");
		} else if (m.getMod() == 0) {
			message("!mp mods None");
		} else {
			message("!mp mods" + Mod.getNames(m.getMod()));
		}
		message("!mp map " + m.getID());
		HashMap<String, Object> data = flushWaitForEvent(MultiplayerEvent.MAP_SELECTED).getData();
		if (data.containsKey("beatmapID")) {
			int id = Integer.parseInt((String) data.get("beatmapID"));
			System.out.println(id+" "+m.getID());
			if (id == m.getID()) {
				currentMap = m;
			}
			return id == m.getID();
		}
		return false;
	}

	private Event flushWaitForEvent(EventType eventType) {
		WaitForEventListener listener = createWaitForEvent(eventType);
		flush();
		listener.listen();
		removeEventListener(listener);
		return listener.getEvent();
	}
}
