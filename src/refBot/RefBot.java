package refBot;

import bancho.BanchoBot;
import bancho.LobbyHandler;
import tournamentData.Beatmap;
import tournamentData.Rule;

public class RefBot {

	private LobbyHandler lobby;
	private Rule rule;

	public RefBot(BanchoBot bot, String title, Rule rule) {
		this.lobby = bot.makeLobby(title);
		this.rule = rule;
		setUp();
		invitePlayers();
		lobby.message("!mp password 123456");
		lobby.setMap(new Beatmap(22538, "", 0, false));
		lobby.waitToStart(120, 10);
		lobby.message("Match Ended");
	}

	public void setUp() {
		lobby.message("!mp set " + rule.getTeamMode() + " " + rule.getScoreMode());
		lobby.flush();
	}

	public void invitePlayers() {
		for (String player : rule.getPlayers()) {
			lobby.message("!mp invite " + player);
		}
		lobby.flush();
	}

	public void close() {
		lobby.close();
	}

}