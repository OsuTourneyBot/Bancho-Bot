package osu.refbot.event;

import java.util.HashMap;

import irc.event.Event;
import irc.event.EventListener;
import irc.event.EventType;
import osu.refbot.RefBot;

public class ReadyEventListener implements EventListener {

	private RefBot bot;

	public ReadyEventListener(RefBot bot) {
		this.bot = bot;
	}

	@Override
	public void trigger(Event event) {
		HashMap<String, Object> data = event.getData();
		if (data.containsKey("player")) {
			bot.setPlayerState((String) data.get("player"), 1);
			if (bot.allReady()) {
				bot.getLobby().fireEvent(BotEvent.ALL_READY.toEvent());
			}
		}
	}

	@Override
	public EventType[] getEventType() {
		return new EventType[] { BotCommandEvent.READY };
	}

}
