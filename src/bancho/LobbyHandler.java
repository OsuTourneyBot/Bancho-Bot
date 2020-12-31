package bancho;

import java.util.ArrayList;

import irc.Channel;
import irc.handlers.IRCEventHandler;
import tournamentData.Beatmap;
import tournamentData.Mod;

public class LobbyHandler {

	private Channel channel;
	private BanchoMessageHandlerGroup banchoHandler;
	private BanchoEventHandler eventHandler;
	private Beatmap currentMap;
	
	

	public LobbyHandler(Channel channel) {
		this.channel = channel;

		this.banchoHandler = new BanchoMessageHandlerGroup(new ArrayList<IRCEventHandler>());
		this.eventHandler = new BanchoEventHandler(this);

		this.banchoHandler.addHandler(eventHandler);
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

	public boolean removeLobbyHanlder(IRCEventHandler handler) {
		return channel.removeHandler(handler);
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
