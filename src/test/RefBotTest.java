package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import fileio.FileIO;
import osu.bancho.BanchoBot;
import osu.refbot.RefBot;
import osu.tournamentData.Mappool;
import osu.tournamentData.Ruleset;

class RefBotTest {

	@Rule
	public Timeout globalTimeout = Timeout.seconds(1);

	private static final int PORT = 6667;
	private static final String SERVER_NAME = "!server";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String LOBBY_ID = "0123456";
	private static final String MATCH_NAME = "TEST";
	private SimpleIRCServer server;
	private BanchoBot client;

	@BeforeEach
	void init() throws Exception {
		server = new SimpleIRCServer(PORT);
		server.start();
		client = new BanchoBot("localhost", PORT, USERNAME, PASSWORD);
	}

	@AfterEach
	void teardown() {
		server.close();
		client.close();
	}

	private void connect() {
		new Thread(() -> {
			server.skipMessages(3);
			server.write(":" + SERVER_NAME + " 001 " + USERNAME + " :Welcome");
		}).start();
		try {
			client.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void runMatchTest() {
		connect();
		Mappool pool = FileIO.mappoolParser(FileIO.readFile("testData/UnitTestMappool.json"));
		Ruleset rule = FileIO.ruleParser(FileIO.readFile("testData/UnitTestRuleset.json"));

		new Thread(() -> {
			assertEquals("PRIVMSG BanchoBot :!mp make " + MATCH_NAME, server.getMessage());
			server.pmUser("BanchoBot", USERNAME,
					"Created the tournament match https://osu.ppy.sh/mp/" + LOBBY_ID + " " + MATCH_NAME);
			server.write(":" + SimpleIRCServer.SERVER_NAME + " JOIN #mp_" + LOBBY_ID);
		}).start();
		RefBot ref = new RefBot(client, MATCH_NAME, rule, pool);
		ref.start();
		server.skipMessages(4);
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user1 joined in slot 1.");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user2 joined in slot 2.");
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!ready");
		server.msgChannel("user2", "#mp_" + LOBBY_ID, "!ready");
		server.skipMessages(4);
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!roll 50");
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!roll");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user1 rolls 20 point(s)");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user1 rolls 70 point(s)");
		server.msgChannel("user2", "#mp_" + LOBBY_ID, "!roll 100");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user2 rolls 80 point(s)");
		server.skipMessages(5);
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!ban NM1");
		server.msgChannel("user2", "#mp_" + LOBBY_ID, "!ban NM2");
		server.skipMessages(3);
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!ban nm1");
		server.skipMessages(2);
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!ban NM1");
		server.skipMessages(3);
		server.msgChannel("user2", "#mp_" + LOBBY_ID, "!pick HD2");
		server.msgChannel("user1", "#mp_" + LOBBY_ID, "!pick HD1");
		server.skipMessages(3);
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID,
				"Changed beatmap to https://osu.ppy.sh/b/" + pool.getShortMapName("HD1").getID());
		server.skipMessages(2);
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "All players are ready");
		server.skipMessages(1);
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "The match has started!");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user1 finished playing (Score: 100000, PASSED).");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user2 finished playing (Score: 200000, PASSED).");
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "The match has finished!");
		server.skipMessages(6);
		
	}

}
