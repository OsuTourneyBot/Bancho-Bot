package refBot;

import java.util.ArrayList;

import bancho.LobbyHandler;
import irc.IRCClient;
import irc.handlers.IRCEventHandler;
import tournamentData.Map;

public class RefBot {

	private LobbyHandler lobby;
	private BanchoMessageHandlerGroup banchoHandler;

	public RefBot(LobbyHandler lobby) {
		this.lobby = lobby;
		this.banchoHandler = new BanchoMessageHandlerGroup(new ArrayList<IRCEventHandler>());
		lobby.addLobbyHandler(banchoHandler);
	}

	/*
	 * Make a new function called timer takes in int (secs) as args waits for either
	 * 2 things (a for when time runs out or b for notify for "this" object) [look
	 * at bancho bot line 41]
	 */
	public void timer(int sec) {
		// start the timer
		lobby.message("!mp timer " + sec);
		lobby.flush();
		BanchoEventHandler handler = new BanchoEventHandler(this);
		banchoHandler.addHandler(handler);

		synchronized (this) {			try {
				this.wait(); // pauses the function
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		lobby.removeLobbyHanlder(handler);

	}
	public void startGame() {
		lobby.message("!mp start 10");
		lobby.flush();
		BanchoEventHandler handler = new BanchoEventHandler(this);
		banchoHandler.addHandler(handler);

		synchronized (this) {			try {
				this.wait(); // pauses the function
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lobby.removeLobbyHanlder(handler);
		
	}
	public void setMap(Map m) {
		String code = Integer.toString(m.getID());
		lobby.message(("!mp map "+ code));
		lobby.flush();
		timer(120);
		startGame();
		
		
	}
}

// Example class don't will remove later
class MatchMessage implements IRCEventHandler {

	@Override
	public String[] match(String[] data) {
		// Matches if the message is hello
		if (data[0].equals("Hello")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		// Do something with the information
		return false;
	}

}
