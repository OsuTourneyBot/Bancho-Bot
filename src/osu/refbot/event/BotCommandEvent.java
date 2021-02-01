package osu.refbot.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.event.Event;
import irc.event.EventFireable;
import irc.event.EventType;
import irc.handlers.IRCEventHandler;

public enum BotCommandEvent implements EventType, IRCEventHandler {
	READY("!ready"), BAN("!(?:ban|b) (.+)", new String[] { "ban" }), PICK("!(?:pick|p) (.+)", new String[] { "pick" }),
	PLAYER_ROLL("(?i)!roll ?([0-9]*).*", new String[] { "amount" });

	private final Pattern pattern;
	private final String[] groupName;

	private BotCommandEvent(String regex) {
		this(regex, new String[0]);
	}

	private BotCommandEvent(String regex, String[] groupName) {
		pattern = Pattern.compile("(?i)" + regex);
		this.groupName = groupName;
	}

	public String[] match(String[] data) {
		Matcher match = pattern.matcher(data[2]);
		if (match.matches()) {
			String[] res = new String[match.groupCount() + 1];
			res[0] = data[0].toLowerCase().substring(0, data[0].indexOf('!'));
			for (int i = 1; i < res.length; i++) {
				res[i] = match.group(i);
			}
			return res;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, EventFireable target) {
		Event event = toEvent();
		event.addData("player", data[0]);
		for (int i = 1; i < data.length; i++) {
			event.addData(groupName[i-1], data[i]);
		}
		target.fireEvent(event);
		return true;
	}
}
