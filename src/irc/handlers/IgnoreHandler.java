package irc.handlers;

import irc.event.EventFireable;

public class IgnoreHandler implements IRCEventHandler {

	private String[] patterns = new String[] { ":.* QUIT :.*", ".*:.+ (372|375|376) \\w+ :(.*)" };

	@Override
	public String[] match(String[] data) {
		for (String pattern : patterns) {
			if (data[0].matches(pattern)) {
				return new String[] {};
			}
		}
		return null;
	}

	@Override
	public boolean handle(String[] data, EventFireable client) {
		return true;
	}

}
