package irc;

import java.util.ArrayList;

import irc.handlers.ChannelHandler;
import irc.handlers.IRCEventHandler;

public class Channel {

	private IRCClient client;
	private String name;
	private ChannelHandler channelHandler;

	public Channel(IRCClient client, String name) {
		this.client = client;
		this.name = name;
		channelHandler = new ChannelHandler(this, new ArrayList<IRCEventHandler>());
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

	public void addHandler(IRCEventHandler handler) {
		channelHandler.addHandler(handler);
	}

	public boolean removeHandler(IRCEventHandler handler) {
		return channelHandler.removeHandler(handler);
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}
}
