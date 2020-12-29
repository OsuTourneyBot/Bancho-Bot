package irc;

import irc.handlers.IRCEventGroup;

public class Channel {

	private IRCClient client;
	private String name;
	private IRCEventGroup eventGroup;

	public Channel(IRCClient client, String name) {
		this.client = client;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void message(String data) {
		client.write("PRIVMSG " + name + " " + data);
	}
}
