package irc;

import logger.Logger;

public class PingPongHandler implements IRCEventHandler {

	private Logger logger;

	public PingPongHandler() {
		logger = Logger.getLogger();
	}

	@Override
	public boolean match(String data) {
		return data.startsWith("PONG") || data.startsWith("PING");
	}

	@Override
	public boolean handle(String data, IRCClient client) {
		logger.println(this, data);
		if (data.startsWith("PONG")) {
			client.write(data.replace("PONG", "PING"));
			client.flush();
		}
		return true;
	}

}
