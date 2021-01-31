package osu.lobby.event;

import java.util.HashMap;

import irc.event.Event;
import irc.event.EventListener;
import irc.event.EventType;
import osu.lobby.MultiplayerLobby;

public class ScoreReportListener implements EventListener {

	private MultiplayerLobby lobby;

	public ScoreReportListener(MultiplayerLobby lobby) {
		this.lobby = lobby;
	}

	@Override
	public void trigger(Event event) {
		HashMap<String, Object> data = event.getData();
		String player = (String) data.get("player");
		int score = Integer.parseInt((String) data.get("score"));
		lobby.setPlayerScore(player, score);
	}

	@Override
	public EventType[] getEventType() {
		return new EventType[] { MultiplayerEvent.SCORE_REPORT };
	}

}
