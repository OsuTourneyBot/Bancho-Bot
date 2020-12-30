package refBot;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanchoEventHandler implements IRCEventHandler {
	private RefBot ref;
	private Pattern maplink = Pattern.compile("Changed beatmap to https://osu.ppy.sh/b/[0-9]* .*");
	
	BanchoEventHandler(RefBot ref) {
		this.ref = ref;
	}
	
	@Override
	public String[] match(String[] data) {
		
		
		if (data[0].toLowerCase().contains("finished playing")){
			return data;
		}
		else if (data[0].toLowerCase().contains("all players are ready")) {
			return data;
		}
		
		else if (data[0].toLowerCase().startsWith("countdown finished")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		synchronized (ref) {
			ref.notify();
		}
		return true;
	}

}
