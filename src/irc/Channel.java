package irc;

import java.util.ArrayList;

import irc.event.EventFireable;
import irc.handlers.ChannelHandler;
import irc.handlers.IRCEventHandler;

public class Channel extends EventFireable {

	private IRCClient client;

	private String name;
	private ChannelHandler channelHandler;

	public Channel(IRCClient client, String name) {
		super();
		this.client = client;
		this.name = name;
		channelHandler = new ChannelHandler(this, new ArrayList<IRCEventHandler>());
		client.addEventHandler(channelHandler);
	}

	public void close() {
		client.removeEventHandler(channelHandler);
	}

	public IRCClient getClient() {
		return client;
	}

	public String getName() {
		return name;
	}

	public void message(String data) {
		client.write("PRIVMSG #" + name + " " + data);
	}

	public void flush() {
		client.flush();
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}
}
