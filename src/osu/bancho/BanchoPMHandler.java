package osu.bancho;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IRCClient;
import irc.event.Event;
import irc.event.EventFireable;
import irc.handlers.IRCEventHandler;
import osu.bancho.event.PMEvent;
import osu.lobby.MultiplayerLobby;

public class BanchoPMHandler implements IRCEventHandler {

	private Pattern createTournement;

	public BanchoPMHandler() {
		createTournement = Pattern.compile("Created the tournament match https://osu\\.ppy\\.sh/mp/([0-9]+) (.*)");
	}

	@Override
	public String[] match(String[] data) {
		if (data[0].equals("BanchoBot")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, EventFireable target) {
		Event event = PMEvent.LOBBY_CREATED.toEvent();
		Matcher match = createTournement.matcher(data[2]);
		if (match.matches()) {
			String title = match.group(2);
			String id = "mp_" + match.group(1);
			event.addData("title", title);
			event.addData("id", id);
		}
		target.fireEvent(event);
		return true;
	}

}
