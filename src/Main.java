import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import api.OSUAPI;
import bancho.BanchoBot;
import fileio.Credential_IO;
import fileio.FileIO;
import refBot.RefBot;
import tournamentData.Beatmap;
import tournamentData.Mappool;
import tournamentData.Rule;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		HashMap<String, String> credentials = Credential_IO.loadCredentials("Credentials.json");

		String IRCUsername = credentials.get("IRCUsername");
		String IRCPassword = credentials.get("IRCPassword");

		int APICredentials = Integer.parseInt(credentials.get("APIID"));
		String APIKey = credentials.get("APIKey");

		OSUAPI.setCredentials(APICredentials, APIKey);

		testBot(IRCUsername, IRCPassword);
	}

	public static void testBot(String IRCUsername, String IRCPassword) throws IOException, InterruptedException {
		Mappool pool = FileIO.mapPoolParser(FileIO.readFile("BotTestMP1.json"));
		Rule rule = FileIO.settingParser(FileIO.readFile("ExampleRuleset.json"));

		BanchoBot bot = new BanchoBot(IRCUsername, IRCPassword);
		bot.connect();
		RefBot ref = new RefBot(bot, "Fungucide's Game", rule, pool);
		Thread thread = new Thread() {
			public void run() {
				Scanner in = new Scanner(System.in);
				String line;
				while ((line = in.nextLine()) != null && !line.equals("CLOSE")) {
					bot.write(line);
					bot.flush();
				}
				ref.close();
			}
		};
		thread.start();
		ref.start();
	}

	public static void testBeatmapAPI() {
		Beatmap bm = new Beatmap(365060, -1, false);
		System.out.println(bm.getLink());
		System.out.println(bm.getTitle());
		System.out.println(bm.getFullTitle());
	}
}
