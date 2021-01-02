package refBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class RollHandler implements IRCEventHandler {

	private RefBot bot;
	private Pattern patternPlayerRoll;
	private Pattern banchoResponse;

	public RollHandler(RefBot bot) {
		this.bot = bot;
		patternPlayerRoll = Pattern.compile("(?i)!roll ?([0-9]*).*");
		banchoResponse = Pattern.compile("(.+) rolls ([0-9]{1,3}) point\\(s\\)");
	}

	@Override
	public String[] match(String[] data) {
		Matcher matchRollRequest = patternPlayerRoll.matcher(data[2]);
		Matcher matchResponse = banchoResponse.matcher(data[2]);
		if (matchRollRequest.matches()) {
			return new String[] { "RollRequest", data[0].split("!")[0], matchRollRequest.group(1) };
		} else if (matchResponse.matches()) {
			return new String[] { "RollResponse", matchResponse.group(1), matchResponse.group(2) };
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		if (data[0].equals("RollRequest")) {
			boolean isCaptain = false;
			for (int i = 0; i < bot.getPresentPlayers().length; i++) {
				if (bot.getPresentPlayers()[i][0].equalsIgnoreCase(data[1])) {
					isCaptain = true;
					break;
				}
			}
			if (!bot.hasRolled(data[1].toLowerCase()) && isCaptain && (data[2].equals("") || data[2].equals("100"))) {
				bot.setValidRoll(data[1].toLowerCase());
			}
			return true;
		} else if (data[0].equals("RollResponse") && bot.getValidRoll(data[1].toLowerCase())) {
			bot.setRoll(data[1].toLowerCase(), Integer.parseInt(data[2]));
			if (bot.allRolls()) {
				synchronized (bot) {
					bot.notify();
				}
			}
			return true;
		}
		return false;
	}

}
