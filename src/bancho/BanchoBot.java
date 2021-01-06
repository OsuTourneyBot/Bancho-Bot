package bancho;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class BanchoBot extends IRCClient {

	private ArrayList<IRCEventHandler> pmHandlers;
	private HashMap<String, LobbyHandler> lobbies;

	public BanchoBot(String username, String password) throws IOException {
		super("irc.ppy.sh", 6667, username, password);
		lobbies = new HashMap<String, LobbyHandler>();
	}

	@Override
	public IRCClient getSelf() {
		return this;
	}

	@Override
	public void connect() throws IOException, InterruptedException {
		super.connect();
		pmHandlers = new ArrayList<IRCEventHandler>();
		pmHandlers.add(new BanchoPMHandler());
		addEventHandler(new PMHandler(pmHandlers, username));
	}

	public void pmBanchoBot(String message) {
		write("PRIVMSG BanchoBot :" + message);
	}

	public LobbyHandler makeLobby(String title) {
		lobbies.put(title, null);
		pmBanchoBot("!mp make " + title);
		flush();
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return lobbies.get(title);
	}

	public void setLobby(String name, LobbyHandler handler) {
		lobbies.put(name, handler);
	}

	public boolean hasLobby(String name) {
		return lobbies.containsKey(name);
	}

	public LobbyHandler getLobby(String name) {
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
					lobbies.get(name).close();
				}
			}
		};
		thread.start();
	}
}
