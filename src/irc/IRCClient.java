package irc;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import irc.event.Event;
import irc.event.EventFireable;
import irc.event.IRCEvent;
import irc.event.JoinEventListener;
import irc.event.PingEventListener;
import irc.event.WaitForEventListener;
import irc.handlers.IRCEventHandler;

public class IRCClient extends EventFireable {

	private String adress;
	private int port;
	protected String username;
	private String password;
	private boolean connected = false;

	protected HashMap<String, Channel> channels;

	private Socket socket;
	private RateLimitedPrintStream printStream;
	protected MessageHandler messageHandler;

	public IRCClient(String adress, int port, String username, String password) {
		super();
		this.adress = adress;
		this.port = port;
		this.username = username;
		this.password = password;

		this.channels = new HashMap<String, Channel>();

		addEventListener(new JoinEventListener(this));
		addEventListener(new PingEventListener(this));
	}

	public boolean isConnected() {
		return connected;
	}

	private Channel createChannel(String name) {
		return new Channel(this, name);
	}

	public boolean addChannel(String name) {
		if (channels.containsKey(name)) {
			return false;
		}
		Channel channel = createChannel(name);
		channels.put(name, channel);
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

	public void removeEventHandler(IRCEventHandler handler) {
		messageHandler.removeHanlder(handler);
	}

	public void connect() throws Exception {
		if (connected) {
			throw new IOException("IRC Client already connected");
		}
		socket = new Socket(adress, port);
		printStream = new RateLimitedPrintStream(socket.getOutputStream());
		messageHandler = new MessageHandler(this, socket.getInputStream());

		listen();
		register();
	}

	private void listen() {
		messageHandler.start();
	}

	private void register() throws Exception {
		write("PASS " + password);
		write("NICK " + username);
		write("USER " + username);
		WaitForEventListener listener = createWaitForEvent(IRCEvent.REGISTER);
		flush();
		listener.listen();
		removeEventListener(listener);
		Event event = listener.getEvent();
		if (((String) event.getData().get("code")).equals("001")) {
			this.connected = true;
		} else {
			throw new Exception("Register Failed");
		}
	}

	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
