package irc.handlers;

import irc.IRCClient;
import logger.Logger;

public class PingPongHandler implements IRCEventHandler {

	private Logger logger;

	public PingPongHandler() {
		logger = Logger.getLogger();
	}

	@Override
	public String[] match(String[] data) {
		String message = data[0];
		if (message.startsWith("PONG")) {
			System.out.println(message);
			int idx = message.indexOf(' ');
			return new String[] { message.substring(0, idx), message.substring(idx + 1) };
		} else if (message.startsWith("PING")) {
			System.out.println(message);
			return new String[] {};
		}
		return null;
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		System.out.println(data);
		if (data[0].equals("PONG")) {
			logger.println(this, data[0] + " " + data[1]);
			client.write("PING " + data[1]);
			client.flush();
		}
		return true;
	}

}
