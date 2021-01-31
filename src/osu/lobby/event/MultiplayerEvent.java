package osu.lobby.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irc.event.Event;
import irc.event.EventFireable;
import irc.event.EventType;
import irc.handlers.IRCEventHandler;

public enum MultiplayerEvent implements EventType, IRCEventHandler {
	TIMER_FINISH("Countdown finished"), ALL_READY("All players are ready"),
	MAP_CHANGED("Changed beatmap to https://osu.ppy.sh/b/([0-9]*) .*", new String[] { "beatmapID" }),
	MAP_FINISH("The match has finished!"), READY_OR_TIMER("(?:Countdown finished|All players are ready)"),
	PLAYER_JOIN("(.+) joined in slot ([0-9]+)\\.", new String[] { "player", "slot" }),
	PLAYER_MOVE("(.+) moved to slot ([0-9]+)", new String[] { "player", "slot" }),
	PLAYER_LEAVE("(.+) left the game\\.", new String[] { "player" }),
	SCORE_REPORT("(.+) finished playing \\(Score: ([0-9]+), (PASSED|FAILED)\\)\\.",
			new String[] { "player", "score", "pass" }),
	PLAYER_SETTINGS("Slot ([0-9]+) (Ready|Not Ready) https://osu.ppy.sh/u/([0-9]+) (.+)\\w*\\[(.+)\\]",
			new String[] { "slot", "ready", "uid", "player", "mods" }),
	BANCHO_ROLL_RESPONSE("(.+) rolls ([0-9]{1,3}) point\\(s\\)", new String[] { "player", "amount" });

	private final Pattern pattern;
	private final String[] groupName;

	private MultiplayerEvent(String regex) {
		this(regex, new String[0]);
	}

	private MultiplayerEvent(String regex, String[] groupName) {
		pattern = Pattern.compile(regex);
		this.groupName = groupName;
	}

	public String[] match(String[] data) {
		if (!data[0].startsWith("BanchoBot!")) {
			return null;
		}
		Matcher match = pattern.matcher(data[2]);
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

	@Override
	public boolean handle(String[] data, EventFireable target) {
		Event event = toEvent();
		for (int i = 0; i < data.length; i++) {
			event.addData(groupName[i], data[i]);
		}
		target.fireEvent(event);
		return true;
	}
}
