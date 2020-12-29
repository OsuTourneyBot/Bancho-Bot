package irc.handlers;

import irc.IRCClient;

public interface IRCEventHandler {
	// return null if it does not find it
	public String[] match(String[] data);

	/**
	 * A function that takes in the data and responds accordingly.
	 * 
	 * @param data The data that needs to be processed
	 * @param client The IRCClient that received the event
	 * @return Whether the event should not be handled by anything else
	 */
	public boolean handle(String[] data ,IRCClient client);
}
