package refBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum BotCommand {
	READY("!ready"), BAN("!(?:ban|b) (.+)"), PICK("!(?:pick|p) (.+)");

	private final Pattern pattern;

	private BotCommand(String regex) {
		pattern = Pattern.compile("(?i)" + regex);
	}

	public String[] getMatch(String[] data) {
		Matcher match = pattern.matcher(data[2]);
		if (match.matches()) {
			String[] res = new String[match.groupCount() + 1];
			res[0] = data[0].toLowerCase().substring(0, data[0].indexOf('!'));
			for (int i = 1; i < res.length; i++) {
				res[i] = match.group(i);
			}
			return res;
		} else {
			return null;
		}
	}
}
