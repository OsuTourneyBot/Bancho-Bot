package irc.handlers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.Channel;

public class ChannelHandler extends IRCEventGroup {

	private Channel channel;
	private Pattern pattern;

	public ChannelHandler(Channel channel, ArrayList<IRCEventHandler> handlers) {
		super(handlers);
		this.channel = channel;
		this.pattern = Pattern.compile(":(.*!.+) PRIVMSG #" + channel.getName() + " :(.+)");
	}

	@Override
	public String[] match(String[] data) {
		String message = data[0];
		Matcher match = pattern.matcher(message);
		if (match.matches()) {
			return new String[] { match.group(1), channel.getName(), match.group(2) };
		} else {
			return null;
		}
	}
}
