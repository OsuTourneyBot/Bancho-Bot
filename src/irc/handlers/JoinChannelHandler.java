package irc.handlers;

import irc.Channel;
import irc.IRCClient;
import logger.Logger;

public class JoinChannelHandler implements IRCEventHandler {

	private Logger logger;

	public JoinChannelHandler() {
		logger = Logger.getLogger();
	}

	@Override
	public String[] match(String[] data) {
		if (data[0].equals("JOIN")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		String channelName = data[1];
		client.addChannel(new Channel(client, channelName));
		logger.println(this, "JOINED: " + data[0]);
		return true;
	}

}
