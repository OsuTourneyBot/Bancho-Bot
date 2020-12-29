package irc.handlers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCCommandGroup extends IRCEventGroup {

	private Pattern pattern;

	public IRCCommandGroup() {
		super(handlerList());
		pattern = Pattern.compile(":(.*)!(.+) (JOIN|PART) (.*)");
	}

	private static ArrayList<IRCEventHandler> handlerList() {
		ArrayList<IRCEventHandler> handlers = new ArrayList<IRCEventHandler>();
		handlers.add(new JoinChannelHandler());
		return handlers;
	}

	@Override
	public String[] match(String[] data) {
		Matcher match = pattern.matcher(data[0]);
		if (match.matches()) {
			return new String[] { match.group(1), match.group(2), match.group(3), match.group(4) };
		} else {
			return null;
		}
	}

}
