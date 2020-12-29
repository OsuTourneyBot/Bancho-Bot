package irc;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import irc.handlers.IRCEventHandler;

public class IRCClient {

	private String adress;
	private int port;
	protected String username;
	private String password;
	private boolean connected = false;

	private HashMap<String, Channel> channels;

	private Socket socket;
	private RateLimitedPrintStream printStream;
	private MessageHandler messageHandler;

	public IRCClient(String adress, int port, String username, String password) {
		this.adress = adress;
		this.port = port;
		this.username = username;
		this.password = password;

		this.channels = new HashMap<String, Channel>();
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean addChannel(Channel channel) {
		if (channels.containsKey(channel.getName())) {
			return false;
		}
		channels.put(channel.getName(), channel);
		messageHandler.addHandler(channel.getChannelHandler());
		return true;
	}

	public boolean removeChannel(Channel channel) {
		messageHandler.removeHanlder(channel.getChannelHandler());
		return channels.remove(channel.getName(), channel);
	}

	public Channel getChannel(String name) {
		return channels.get(name);
	}

	public void write(String message) {
		printStream.write(message);
	}

	public void flush() {
		printStream.flush();
	}

	public void addEventHandler(IRCEventHandler handler) {
		messageHandler.addHandler(handler);
	}

	public IRCClient getSelf() {
		return this;
	}

	public void connect() throws IOException, InterruptedException {
		if (connected) {
			throw new IOException("IRC Client already connected");
		}
		socket = new Socket(adress, port);
		printStream = new RateLimitedPrintStream(socket.getOutputStream());
		messageHandler = new MessageHandler(getSelf(), socket.getInputStream());

		listen();
		register();
		synchronized (this) {
			this.wait();
		}
	}

	private void listen() {
		messageHandler.start();
	}

	public void register() {
		write("PASS " + password);
		write("NICK " + username);
		write("USER " + username);
		flush();
	}
}
