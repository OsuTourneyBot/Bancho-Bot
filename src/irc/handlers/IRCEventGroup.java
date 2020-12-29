package irc.handlers;

import java.util.ArrayList;

import irc.IRCClient;

public abstract class IRCEventGroup implements IRCEventHandler {

	private ArrayList<IRCEventHandler> handlers;

	public IRCEventGroup(ArrayList<IRCEventHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public abstract String[] match(String[] data);

	@Override
	public boolean handle(String[] data, IRCClient client) {
		for (IRCEventHandler handler : handlers) {
			String[] parts = handler.match(data);
			if (parts != null) {
				return handler.handle(parts, client);
			}
		}
		return false;
	}

	public void addHandler(IRCEventHandler handler) {
		handlers.add(handler);
	}

	public void removeHandler(IRCEventHandler handler) {
		handlers.remove(handler);
	}

}
