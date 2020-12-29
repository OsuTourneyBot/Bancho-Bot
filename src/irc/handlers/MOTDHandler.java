package irc.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IRCClient;

public class MOTDHandler implements IRCEventHandler {

	private Pattern pattern;

	public MOTDHandler() {
		pattern = Pattern.compile(".*:.+ (372|375|376) \\w+ :(.*)");
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
		if (data[0].equals("376")) {
			synchronized (client) {
				client.notify();
			}
		}
		return true;
	}

}
