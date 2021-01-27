package osu.bancho.event;

import java.util.HashMap;

import irc.event.Event;
import irc.event.WaitForEventListener;
import osu.bancho.BanchoBot;
import osu.lobby.LobbyHandler;

public class MatchCreatedListener extends WaitForEventListener {

	private BanchoBot client;

	private String title;
	private String id;

	public MatchCreatedListener(BanchoBot client, String name) {
		super(PMEvent.LOBBY_CREATED);
		this.client = client;
		this.title = name;
	}

	@Override
	public void trigger(Event event) {
		HashMap<String, Object> data = event.getData();
		// Make sure we're getting the id for the right room

		if (data.containsKey("title") && ((String) data.get("title")).equals(title)) {
			// Make sure everything was successfully captured
			if (data.containsKey("id")) {
				id = (String) data.get("id");
				client.setLobby(title, new LobbyHandler(client, id));

			}
			super.trigger(event);
		}
	}

	public String getId() {
		return id;
	}
}
