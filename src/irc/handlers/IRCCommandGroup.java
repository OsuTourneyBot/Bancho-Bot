package irc.handlers;

import java.util.ArrayList;

public class IRCCommandGroup extends IRCEventGroup {

	public IRCCommandGroup() {
		super(handlerList());
	}

	private static ArrayList<IRCEventHandler> handlerList() {
		ArrayList<IRCEventHandler> handlers = new ArrayList<IRCEventHandler>();
		handlers.add(new PingPongHandler());
		handlers.add(new JoinChannelHandler());
		return handlers;
	}

	@Override
	public String[] match(String[] data) {
		String message = data[0];
		int idx = message.indexOf(' ');
		String command = message.substring(0, idx);
		if (command.matches("PING|PONG|JOIN")) {
			return new String[] { command, message.substring(idx + 1) };
		} else {
			return null;
		}
	}

}
