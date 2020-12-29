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
		if (message.startsWith("PING")) {
			int idx = message.indexOf(' ');
			return new String[] { "PING", message.substring(idx + 1) };
		} else if (message.startsWith("PONG")) {
			return new String[] { "PONG" };
		}
		return null;
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		if (data[0].equals("PING")) {
			logger.println(this, data[0] + " " + data[1]);
			client.write("PONG " + data[1]);
			client.flush();
		}else {
			logger.println(this, "PONGed");
		}
		return true;
	}

}
