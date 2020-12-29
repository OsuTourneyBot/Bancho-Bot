package bancho;

import irc.Channel;
import irc.handlers.IRCEventHandler;

public class LobbyHandler {

	private Channel channel;

	public LobbyHandler(Channel channel) {
		this.channel = channel;
		channel.message("!mp invite AssainPro");
		channel.flush();
	}

	public void close() {
		channel.message("!mp close");
		channel.flush();
	}

	public void addLobbyHandler(IRCEventHandler handler) {
		channel.addHandler(handler);
	}

	public boolean removeLobbyHanlder(IRCEventHandler handler) {
		return channel.removeHandler(handler);
	}
}
