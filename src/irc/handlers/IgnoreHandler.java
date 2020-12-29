package irc.handlers;

import irc.IRCClient;

public class IgnoreHandler implements IRCEventHandler {

	@Override
	public String[] match(String[] data) {
		if (data[0].matches(":.* QUIT.*")) {
			return new String[] {};
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		return true;
	}

}
