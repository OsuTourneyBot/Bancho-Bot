package refBot;

import java.util.ArrayList;

import irc.handlers.IRCEventGroup;
import irc.handlers.IRCEventHandler;

public class BanchoMessageHandlerGroup extends IRCEventGroup {

	public BanchoMessageHandlerGroup(ArrayList<IRCEventHandler> handlers) {
		super(handlers);
	}

	@Override
	public String[] match(String[] data) {
		if (data[0].startsWith("BanchoBot!")) {
			return new String[] { data[2] };
		} else {
			return null;
		}
	}

}
