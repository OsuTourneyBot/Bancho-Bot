package bancho;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;
import logger.Logger;

public class ScoreHandler implements IRCEventHandler {

	private Pattern pattern;
	private LobbyHandler lobby;
	private Logger logger;

	public ScoreHandler(LobbyHandler lobby) {
		this.lobby = lobby;
		this.logger = Logger.getLogger();
		pattern = Pattern.compile("(.+) finished playing \\(Score: ([0-9]+), (?:PASSED|FAILED)\\)\\.");
	}

	@Override
	public String[] match(String[] data) {
		Matcher match = pattern.matcher(data[0]);
		if (match.matches()) {
			return new String[] { match.group(1), match.group(2) };
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		logger.println(this, "[" + data[0] + "] Score:" + data[1]);
		lobby.setPlayerScore(data[0], Integer.parseInt(data[1]));
		return true;
	}

}
