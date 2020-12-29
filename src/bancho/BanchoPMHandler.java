package bancho;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.IRCClient;
import irc.handlers.IRCEventHandler;

public class BanchoPMHandler implements IRCEventHandler {

	private Pattern createTournement;

	public BanchoPMHandler() {
		createTournement = Pattern.compile("Created the tournament match https://osu\\.ppy\\.sh/mp/([0-9]+) (.*)");
	}

	@Override
	public String[] match(String[] data) {
		if (data[0].equals("BanchoBot")) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public boolean handle(String[] data, IRCClient client) {
		Matcher match = createTournement.matcher(data[2]);
		if (match.matches()) {
			String name = match.group(2);
			String id = "mp_"+match.group(1);
			if (client instanceof BanchoBot) {
				BanchoBot bot = (BanchoBot) client;
				if (bot.hasLobby(name)) {
					bot.setLobby(name, new LobbyHandler(client.getChannel(id)));
					synchronized (bot) {
						bot.notify();
					}
				}
				System.out.println(name+" -> "+id);
			}
		}
		return true;
	}

}
