package bancho;

import irc.Channel;
import irc.handlers.IRCEventHandler;

public class LobbyHandler {

	private Channel channel;

	public LobbyHandler(Channel channel) {
		this.channel = channel;
		message("!mp invite AssainPro");
		flush();
	}

	public void message(String message) {
		channel.message(message);
	}

	public void flush() {
		channel.flush();
	}

	public void close() {
		message("!mp close");
		flush();
	}

	public void addLobbyHandler(IRCEventHandler handler) {
		channel.addHandler(handler);
	}

	public boolean removeLobbyHanlder(IRCEventHandler handler) {
		return channel.removeHandler(handler);
	}
}
