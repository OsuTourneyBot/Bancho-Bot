package bancho;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.handlers.IRCEventGroup;
import irc.handlers.IRCEventHandler;

public class PMHandler extends IRCEventGroup {

	private Pattern pattern;
	private String name;

	public PMHandler(ArrayList<IRCEventHandler> handlers, String name) {
		super(handlers);
		this.name = name;
		pattern = Pattern.compile(":(.+)!(.+) PRIVMSG (.+) :(.+)");
	}

	@Override
	public String[] match(String[] data) {
		System.out.println(data[0]);
		Matcher match = pattern.matcher(data[0]);
		if (match.matches() && match.group(3).equalsIgnoreCase(name)) {
			return new String[] { match.group(1), match.group(2), match.group(4) };
		} else {
			return null;
		}
	}

}
