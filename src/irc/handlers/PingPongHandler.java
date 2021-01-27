package irc.handlers;

import irc.event.Event;
import irc.event.EventFireable;
import irc.event.IRCEvent;

public class PingPongHandler implements IRCEventHandler {

	@Override
	public String[] match(String[] data) {
		String message = data[0];
		if (message.startsWith("PING")) {
			int idx = message.indexOf(' ');
			return new String[] { "PING", message.substring(idx + 1) };
		} else if (message.startsWith("PONG")) {
			return new String[] { "PONG" };
		}
		return null;
	}

	@Override
	public boolean handle(String[] data, EventFireable target) {
		Event event = IRCEvent.valueOf(data[0]).toEvent();
		event.addData("data", data[1]);
		target.fireEvent(event);
		return true;
	}

}
