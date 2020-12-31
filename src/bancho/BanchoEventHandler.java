package bancho;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class BanchoEventHandler implements IRCEventHandler {
	private LobbyHandler lobby;
	private BanchoEvent event;

	public BanchoEventHandler(LobbyHandler lobby) {
		this.lobby = lobby;
		event = null;
	}

	public void setEvent(BanchoEvent event) {
		this.event = event;
	}

	@Override
	public String[] match(String[] data) {
		System.out.println(data[0]);
		return event == null ? null : event.getMatch(data[0]);
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		synchronized (lobby) {
			lobby.notify();
		}
		return true;
	}

}