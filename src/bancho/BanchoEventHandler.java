package bancho;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class BanchoEventHandler implements IRCEventHandler {
	private LobbyHandler lobby;
	private BanchoEvent event;
	private BanchoEvent otherEvent = null;

	private BanchoEvent[] permanentHanlders;

	public BanchoEventHandler(LobbyHandler lobby) {
		this.lobby = lobby;
		this.event = null;
		this.permanentHanlders = new BanchoEvent[] { BanchoEvent.PLAYER_JOIN, BanchoEvent.PLAYER_MOVE,
				BanchoEvent.PLAYER_LEAVE };

	}

	public void setEvent(BanchoEvent event) {
		this.event = event;
	}

	@Override
	public String[] match(String[] data) {
		for (BanchoEvent permEvent : permanentHanlders) {
			String[] res = permEvent.getMatch(data[0]);
			if (res != null) {
				otherEvent = permEvent;
				return res;
			}
		}
		return event == null ? null : event.getMatch(data[0]);
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		if (otherEvent == BanchoEvent.PLAYER_JOIN || otherEvent == BanchoEvent.PLAYER_MOVE) {
			lobby.setPlayerSlot(data[0].toLowerCase(), Integer.parseInt(data[1]));
			otherEvent = null;
		} else if (otherEvent == BanchoEvent.PLAYER_LEAVE) {
			lobby.removePlayerSlot(data[0].toLowerCase());
			otherEvent = null;
		} else {
			synchronized (lobby) {
				lobby.notify();
			}
		}
		return true;
	}

}