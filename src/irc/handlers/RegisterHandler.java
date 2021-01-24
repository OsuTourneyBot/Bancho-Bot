package irc.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IRCClient;
import irc.event.Event;
import irc.event.EventType;
import irc.event.IRCEvent;

public class RegisterHandler implements IRCEventHandler {

	private Pattern successPattern;
	private Pattern failPattern;

	public RegisterHandler() {
		successPattern = Pattern.compile(".*:.+ (001) (\\w+) :.*");
		failPattern = Pattern.compile(".*:.+ (464|465).*");
	}

	@Override
	public String[] match(String[] data) {
		Matcher matchSuccess = successPattern.matcher(data[0]);
		Matcher matchFail = failPattern.matcher(data[0]);
		if (matchSuccess.matches()) {
			return new String[] { matchSuccess.group(1), matchSuccess.group(2) };
		} else if (matchFail.matches()) {
			return new String[] {matchFail.group(1)};
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		Event event = IRCEvent.REGISTER.toEvent();
		event.addData("code", data[0]);
		if (data[0].equals("001")) {
			event.addData("nick", data[1]);
		}
		client.fireEvent(event);
		return true;
	}

}
