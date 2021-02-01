package osu.bancho;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;
import osu.bancho.event.MatchCreatedListener;
import osu.lobby.MultiplayerLobby;

public class BanchoBot extends IRCClient {

	private ArrayList<IRCEventHandler> pmHandlers;
	private HashMap<String, MultiplayerLobby> lobbies;

	public BanchoBot(String username, String password) throws IOException {
		this("irc.ppy.sh", 6667, username, password);
	}

	public BanchoBot(String address, int port, String username, String password) throws IOException {
		super(address, port, username, password);
		lobbies = new HashMap<String, MultiplayerLobby>();
	}

	@Override
	public void connect() throws Exception {
		super.connect();
		pmHandlers = new ArrayList<IRCEventHandler>();
		pmHandlers.add(new BanchoPMHandler());
		addEventHandler(new PMHandler(pmHandlers, username));
	}

	public void pmBanchoBot(String message) {
		write("PRIVMSG BanchoBot :" + message);
	}

	public MultiplayerLobby makeLobby(String title) {
		MatchCreatedListener listener = new MatchCreatedListener(this, title);
		addEventListener(listener);
		pmBanchoBot("!mp make " + title);
		flush();
		listener.listen();
		removeEventListener(listener);
		if (listener.getId() != null) {
			return getLobby(title);
		} else {
			return null;
		}
	}

	public void setLobby(String name, MultiplayerLobby handler) {
		lobbies.put(name, handler);
		if (!channels.containsKey(handler.getName())) {
			channels.put(handler.getName(), handler);
		}
	}

	public boolean hasLobby(String name) {
		return lobbies.containsKey(name);
	}

	public MultiplayerLobby getLobby(String name) {
		return lobbies.get(name);
	}

	public void interactive() {
		Thread thread = new Thread() {
			public void run() {
				Scanner in = new Scanner(System.in);
				String line;
				while ((line = in.nextLine()) != null && !line.equals("CLOSE")) {
					write(line);
					flush();
				}
				for (String name : lobbies.keySet()) {
					lobbies.get(name).closeLobby();
				}
				in.close();
			}
		};
		thread.start();
	}
}
