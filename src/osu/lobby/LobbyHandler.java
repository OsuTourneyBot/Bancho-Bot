package osu.lobby;

import java.util.ArrayList;
import java.util.HashMap;

import irc.Channel;
import irc.handlers.IRCEventHandler;
import osu.lobby.event.JoinMoveLeaveListener;
import osu.lobby.event.MultiplayerEvent;
import osu.tournamentData.Beatmap;
import osu.tournamentData.Mod;

public class LobbyHandler {

	private Channel channel;
	private LobbyBanchoHandlerGroup banchoHandler;
	private ScoreHandler scoreHandler;
	private JoinMoveLeaveListener listener;
	private Beatmap currentMap;

	private HashMap<String, Integer> playerScores;
	private HashMap<String, Integer> playerMods;

	private HashMap<String, Integer> playerSlots;

	public LobbyHandler(Channel channel) {
		this.channel = channel;

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
		addLobbyHandler(banchoHandler);

		listener = new JoinMoveLeaveListener(this);
		this.channel.getClient().addEventListener(listener, MultiplayerEvent.PLAYER_JOIN);
		this.channel.getClient().addEventListener(listener, MultiplayerEvent.PLAYER_MOVE);
		this.channel.getClient().addEventListener(listener, MultiplayerEvent.PLAYER_LEAVE);
	}

	public void close() {
		this.channel.getClient().removeEventListener(listener, MultiplayerEvent.PLAYER_JOIN);
		this.channel.getClient().removeEventListener(listener, MultiplayerEvent.PLAYER_MOVE);
		this.channel.getClient().removeEventListener(listener, MultiplayerEvent.PLAYER_LEAVE);
	}

	public Channel getChannel() {
		return channel;
	}

	public void message(String message) {
		channel.message(message);
	}

	public void flush() {
		channel.flush();
	}

	public void closeLobby() {
		message("!mp close");
		flush();
	}

	public Beatmap getCurrentMap() {
		return currentMap;
	}

	public void addLobbyHandler(IRCEventHandler handler) {
		channel.addHandler(handler);
	}

	public void addLobbyHandler(IRCEventHandler handler, int idx) {
		channel.addHandler(handler, idx);
	}

	public boolean removeLobbyHanlder(IRCEventHandler handler) {
		return channel.removeHandler(handler);
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
		flush();
		channel.getClient().waitForEvent(MultiplayerEvent.TIMER_FINISH);
	}

	public void waitToStart(int waitTime, int startDelay) {
		message("Starting match in " + waitTime + " seconds");
		message("!mp timer " + waitTime);
		flush();
		channel.getClient().waitForEvent(MultiplayerEvent.READY_OR_TIMER);
		startGame(startDelay);
	}

	public void startGame(int startDelay) {
		playerScores.clear();
		playerMods.clear();
		message("!mp start " + startDelay);
		flush();
		channel.getClient().waitForEvent(MultiplayerEvent.MAP_FINISH);
	}

	public void setMap(Beatmap m) {
		currentMap = m;
		if (m.isFreeMod()) {
			message("!mp mods Freemod");
		} else if (m.getMod() == 0) {
			message("!mp mods None");
		} else {
			message("!mp mods" + Mod.getNames(m.getMod()));
		}
		message("!mp map " + m.getID());
		flush();
		channel.getClient().waitForEvent(MultiplayerEvent.MAP_SELECTED);
	}
}
