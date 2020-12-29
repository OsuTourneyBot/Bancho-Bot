package refBot;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class BanchoTimerHandler implements IRCEventHandler {
	private RefBot ref;

	BanchoTimerHandler(RefBot ref) {
		this.ref = ref;
	}

	@Override
	public String[] match(String[] data) {
		if (data[0].toLowerCase().startsWith("countdown finished")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		synchronized (ref) {
			ref.notify();
		}
		return true;
	}

}
