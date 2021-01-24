package osu.bancho.event;

import java.util.HashMap;

import irc.event.Event;
import irc.event.WaitForEventListener;
import osu.lobby.LobbyHandler;

public class MatchCreatedListener extends WaitForEventListener {

	private String title;
	private String id;
	private LobbyHandler lobbyHandler;

	public MatchCreatedListener(String name) {
		super(PMEvent.LOBBY_CREATED);
		this.title = name;
	}

	@Override
	public void trigger(Event event) {
		HashMap<String, Object> data = event.getData();
		// Make sure we're getting the id for the right room
		if (data.containsKey("name") && ((String) data.get("title")).equals(title)) {
			// Make sure everything was successfully captured
			if (data.containsKey("id") && data.containsKey("lobbyHandler")) {
				id = (String) data.get("id");
				lobbyHandler = (LobbyHandler) data.get("lobbyHandler");
			}
			super.trigger(event);
		}
	}

	public String getId() {
		return id;
	}

	public LobbyHandler getLobbyHandler() {
		return lobbyHandler;
	}
}
