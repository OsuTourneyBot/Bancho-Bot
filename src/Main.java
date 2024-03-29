import java.util.HashMap;

import fileio.Credential_IO;
import fileio.FileIO;
import osu.OSUAPI;
import osu.bancho.BanchoBot;
import osu.tournamentData.Beatmap;
import osu.tournamentData.Mappool;
import osu.tournamentData.Ruleset;

public class Main {

	public static void main(String[] args) throws Exception {
		HashMap<String, String> credentials = Credential_IO.loadCredentials("Credentials.json");

		String IRCUsername = credentials.get("IRCUsername");
		String IRCPassword = credentials.get("IRCPassword");

		int APICredentials = Integer.parseInt(credentials.get("APIID"));
		String APIKey = credentials.get("APIKey");

		OSUAPI.setCredentials(APICredentials, APIKey);

		testBot(IRCUsername, IRCPassword);
	}

	public static void testBot(String IRCUsername, String IRCPassword) throws Exception {
		Mappool pool = FileIO.mappoolParser(FileIO.readFile("BotTestMP1.json"));
		Ruleset rule = FileIO.ruleParser(FileIO.readFile("RuleMP1.json"));

		BanchoBot bot = new BanchoBot(IRCUsername, IRCPassword);
		bot.connect();
		System.out.println(bot.isConnected());
//		RefBot ref = new RefBot(bot, "OTB: (" + rule.getTeamNames()[0] + ") vs (" + rule.getTeamNames()[1] + ")", rule,pool);
		bot.interactive();
//		ref.start();
	}

	public static void testBeatmapAPI() {
		Beatmap bm = new Beatmap(365060, -1, false);
		System.out.println(bm.getLink());
		System.out.println(bm.getTitle());
		System.out.println(bm.getFullTitle());
	}
}
