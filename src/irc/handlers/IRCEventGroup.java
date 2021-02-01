package irc.handlers;

import java.util.ArrayList;

import irc.event.EventFireable;

public abstract class IRCEventGroup implements IRCEventHandler {

	private ArrayList<IRCEventHandler> handlers;

	public IRCEventGroup(ArrayList<IRCEventHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public abstract String[] match(String[] data);

	@Override
	public boolean handle(String[] data, EventFireable client) {
		for (IRCEventHandler handler : handlers) {
			String[] parts = handler.match(data);
			if (parts != null) {
				return handler.handle(parts, client);
			}
		}
		return false;
	}

	public void addHandler(IRCEventHandler... handlers) {
		for (IRCEventHandler handler : handlers) {
			this.handlers.add(handler);
		}
	}

	public boolean removeHandler(IRCEventHandler handler) {
		return handlers.remove(handler);
	}

	public void removeHandler(IRCEventHandler... handlers) {
		for (IRCEventHandler handler : handlers) {
			this.handlers.remove(handler);
		}
	}

}
