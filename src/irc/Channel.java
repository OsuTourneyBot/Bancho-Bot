package irc;

public class Channel {

	private IRCClient client;
	private String name;

	public Channel(IRCClient client, String name) {
		this.client = client;
		this.name = name;
	}

	public void message(String data) {
		client.write("PRIVMSG " + name + " " + data);
	}
}
