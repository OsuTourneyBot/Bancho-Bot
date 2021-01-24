package irc;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import irc.event.Event;
import irc.event.EventListener;
import irc.event.EventType;
import irc.event.IRCEvent;
import irc.event.WaitForEventListener;
import irc.handlers.IRCEventHandler;

public class IRCClient {

	private String adress;
	private int port;
	protected String username;
	private String password;
	private boolean connected = false;

	private HashMap<String, Channel> channels;
	private HashMap<EventType, ArrayList<EventListener>> eventListeners;

	private Socket socket;
	private RateLimitedPrintStream printStream;
	private MessageHandler messageHandler;

	public IRCClient(String adress, int port, String username, String password) {
		this.adress = adress;
		this.port = port;
		this.username = username;
		this.password = password;

		this.channels = new HashMap<String, Channel>();
		this.eventListeners = new HashMap<EventType, ArrayList<EventListener>>();
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

	public void addEventListener(EventListener listener) {
		addEventListener(listener, listener.getEventType());
	}

	public void addEventListener(EventListener listener, EventType type) {
		if (!eventListeners.containsKey(type)) {
			eventListeners.put(type, new ArrayList<EventListener>());
		}
		eventListeners.get(type).add(listener);
	}

	public void removeEventListener(EventListener listener) {
		removeEventListener(listener, listener.getEventType());
	}

	public void removeEventListener(EventListener listener, EventType type) {
		if (eventListeners.containsKey(type)) {
			eventListeners.get(type).remove(listener);
		}
	}

	public void fireEvent(Event event) {
		if (eventListeners.containsKey(event.getType())) {
			for (EventListener listener : eventListeners.get(event.getType())) {
				listener.trigger(event);
			}
		}
	}

	public Event waitForEvent(EventType eventType) {
		WaitForEventListener listener = new WaitForEventListener(eventType);
		addEventListener(listener);
		listener.listen();
		return listener.getEvent();
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
		flush();
		Event event = waitForEvent(IRCEvent.REGISTER);
		System.out.println(((String) event.getData().get("code")));
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
