package bancho;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum BanchoEvent {
	TIMER_FINISH("Countdown finished"), ALL_READY("All players are ready"),
	MAP_SELECTED("Changed beatmap to https://osu.ppy.sh/b/([0-9]*) .*"), MAP_FINISH("The match has finished!"),
	READY_OR_TIMER("(?:Countdown finished|All players are ready)");

	private final Pattern pattern;

	private BanchoEvent(String regex) {
		pattern = Pattern.compile(regex);
	}

	public String[] getMatch(String data) {
		Matcher match = pattern.matcher(data);
		if (match.matches()) {
			String[] res = new String[match.groupCount()];
			for (int i = 0; i < res.length; i++) {
				res[i] = match.group(i + 1);
			}
			return res;
		} else {
			return null;
		}
	}
}
