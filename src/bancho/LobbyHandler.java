package bancho;

import java.util.ArrayList;
import java.util.HashMap;

import irc.Channel;
import irc.handlers.IRCEventHandler;
import tournamentData.Beatmap;
import tournamentData.Mod;

public class LobbyHandler {

	private Channel channel;
	private BanchoMessageHandlerGroup banchoHandler;
	private BanchoEventHandler eventHandler;
	private ScoreHandler scoreHandler;
	private Beatmap currentMap;

	private HashMap<String, Integer> playerScores;
	private HashMap<String, Integer> playerMods;

	private HashMap<String, Integer> playerSlots;

	public LobbyHandler(Channel channel) {
		this.channel = channel;

		this.playerScores = new HashMap<String, Integer>();
		this.playerMods = new HashMap<String, Integer>();

		this.playerSlots = new HashMap<String, Integer>();

		this.banchoHandler = new BanchoMessageHandlerGroup(new ArrayList<IRCEventHandler>());
		this.eventHandler = new BanchoEventHandler(this);
		this.scoreHandler = new ScoreHandler(this);

		this.banchoHandler.addHandler(eventHandler);
		this.banchoHandler.addHandler(scoreHandler);
		addLobbyHandler(banchoHandler);
	}

	public void message(String message) {
		channel.message(message);
	}

	public void flush() {
		channel.flush();
	}

	public void close() {
		message("!mp close");
		flush();
	}

	public Beatmap getCurrentMap() {
		return currentMap;
	}

	private void waitForEvent(BanchoEvent event) {
		eventHandler.setEvent(event);
		synchronized (this) {
			try {
				this.wait(); // pauses the function
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		eventHandler.setEvent(null);
	}

	public void addLobbyHandler(IRCEventHandler handler) {
		channel.addHandler(handler);
	}
	
	public void addLobbyHandler(IRCEventHandler handler,int idx) {
		channel.addHandler(handler,idx);
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
		waitForEvent(BanchoEvent.TIMER_FINISH);
	}

	public void waitToStart(int waitTime, int startDelay) {
		message("Starting map in " + waitTime + " seconds");
		message("!mp timer " + waitTime);
		flush();
		waitForEvent(BanchoEvent.READY_OR_TIMER);
		startGame(startDelay);
	}

	public void startGame(int startDelay) {
		playerScores.clear();
		playerMods.clear();
		message("!mp start " + startDelay);
		flush();
		waitForEvent(BanchoEvent.MAP_FINISH);
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
		waitForEvent(BanchoEvent.MAP_SELECTED);
	}

}
