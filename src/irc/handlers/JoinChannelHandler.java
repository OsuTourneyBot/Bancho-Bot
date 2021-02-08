package irc.handlers;

import irc.event.Event;
import irc.event.EventFireable;
import irc.event.IRCEvent;

public class JoinChannelHandler implements IRCEventHandler {

	@Override
	public String[] match(String[] data) {
		if (data[2].equals("JOIN")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, EventFireable target) {
		String channelName = data[3].substring(1);
		Event event = IRCEvent.JOIN.toEvent();
		event.addData("channelName", channelName);
		target.fireEvent(event);
		return true;
	}

}
