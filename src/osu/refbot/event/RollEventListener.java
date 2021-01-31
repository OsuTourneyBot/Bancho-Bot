package osu.refbot.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import irc.event.Event;
import irc.event.EventListener;
import irc.event.EventType;
import osu.lobby.event.MultiplayerEvent;
import osu.refbot.RefBot;

public class RollEventListener implements EventListener {

	private RefBot bot;
	private Queue<Integer>[] rollRequests;
	private int[] rollResults;

	public RollEventListener(RefBot bot) {
		this.bot = bot;
		rollRequests = new Queue[bot.getPresentPlayers().length];
		rollResults = new int[bot.getPresentPlayers().length];
		for (int i = 0; i < rollRequests.length; i++) {
			rollRequests[i] = new LinkedList<Integer>();
			rollResults[i] = -1;
		}
	}

	@Override
	public void trigger(Event event) {
		HashMap<String, Object> data = event.getData();
		String player = (String) data.get("player");
		int team = bot.getPlayerTeam(player);
		int amount = Integer.parseInt((String) data.get("amount"));
		EventType type = event.getType();
		if (type == BotCommandEvent.PLAYER_ROLL) {
			if (team != -1 && bot.getPresentPlayers()[team][0].equalsIgnoreCase(player)) {
				rollRequests[team].add(amount);
			}
		} else if (type == MultiplayerEvent.BANCHO_ROLL_RESPONSE) {
			if (team != -1 && rollResults[team] == -1 && bot.getPresentPlayers()[team][0].equalsIgnoreCase(player)
					&& rollRequests[team].poll() == 100) {
				rollResults[team] = amount;
				boolean flag = true;
				for (int i : rollResults) {
					if (i == -1) {
						flag = false;
						break;
					}
				}
				if (flag) {
					bot.getLobby().fireEvent(BotEvent.ROLLS_DONE.toEvent());
				}
			}
		}
	}

	@Override
	public EventType[] getEventType() {
		return new EventType[] { BotCommandEvent.PLAYER_ROLL, MultiplayerEvent.BANCHO_ROLL_RESPONSE };
	}

	public int[] getRolls() {
		return rollResults;
	}

}
