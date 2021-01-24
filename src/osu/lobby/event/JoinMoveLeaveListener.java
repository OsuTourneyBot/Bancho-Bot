package osu.lobby.event;

import java.util.HashMap;

import irc.event.Event;
import irc.event.EventListener;
import irc.event.EventType;
import osu.lobby.LobbyHandler;

public class JoinMoveLeaveListener implements EventListener {

	private final LobbyHandler lobby;

	public JoinMoveLeaveListener(LobbyHandler lobby) {
		this.lobby = lobby;
	}

	@Override
	public void listen() {
	}

	@Override
	public void trigger(Event event) {
		MultiplayerEvent eventType = (MultiplayerEvent) event.getType();
		HashMap<String, Object> data = event.getData();
		if (eventType == MultiplayerEvent.PLAYER_JOIN || eventType == MultiplayerEvent.PLAYER_MOVE) {
			String name = (String) data.get("name");
			int slot = Integer.parseInt((String) data.get("slot"));
			lobby.setPlayerSlot(name, slot);
		} else if (eventType == MultiplayerEvent.PLAYER_LEAVE) {
			String name = (String) data.get("name");
			lobby.removePlayerSlot(name);
		} else {
			System.err.println("Event listener fired for unintended event: " + eventType.name());
		}
	}

	@Override
	public EventType getEventType() {
		return null;
	}

}
