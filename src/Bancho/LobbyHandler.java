package Bancho;

import irc.Channel;

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
}
