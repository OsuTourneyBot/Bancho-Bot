package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import osu.bancho.BanchoBot;
import osu.lobby.MultiplayerLobby;
import osu.lobby.event.MultiplayerEvent;
import osu.tournamentData.Beatmap;

class BanchoTest {

	@Rule
	public Timeout globalTimeout = Timeout.seconds(1);

	private static final int PORT = 6667;

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String LOBBY_NAME = "test_lobby";
	private static final String LOBBY_ID = "0123456";
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
			server.write(":" + SimpleIRCServer.SERVER_NAME + " 001 " + USERNAME + " :Welcome");
		}).start();
		try {
			client.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void createLobbyTest() {
		connect();
		new Thread(() -> {
			client.makeLobby(LOBBY_NAME);
			MultiplayerLobby lobby = client.getLobby(LOBBY_NAME);
			assertNotNull(lobby);
			lobby.message("test message");
			lobby.flush();
		}).start();
		String req = server.getMessage();
		assertEquals("PRIVMSG BanchoBot :!mp make " + LOBBY_NAME, req);
		server.pmUser("BanchoBot", USERNAME,
				"Created the tournament match https://osu.ppy.sh/mp/" + LOBBY_ID + " " + LOBBY_NAME);
		String msg = server.getMessage();
		assertEquals("PRIVMSG #mp_" + LOBBY_ID + " test message", msg);
	}

	@Test
	void setMapTest() {
		connect();
		new Thread(() -> {
			client.makeLobby(LOBBY_NAME);
			MultiplayerLobby lobby = client.getLobby(LOBBY_NAME);
			assertNotNull(lobby);
			// Random map no mod
			Beatmap b = new Beatmap(987654, 0, false);
			lobby.setMap(b);
			assertEquals(b, lobby.getCurrentMap());
			// Random freemod
			b = new Beatmap(876543, -1, true);
			lobby.setMap(b);
			assertEquals(b, lobby.getCurrentMap());
			// Random multi mod
			b = new Beatmap(765432, 9, false);
			lobby.setMap(b);
			assertEquals(b, lobby.getCurrentMap());
		}).start();
		String req = server.getMessage();
		assertEquals("PRIVMSG BanchoBot :!mp make " + LOBBY_NAME, req);
		server.pmUser("BanchoBot", USERNAME,
				"Created the tournament match https://osu.ppy.sh/mp/" + LOBBY_ID + " " + LOBBY_NAME);
		server.write(":" + SimpleIRCServer.SERVER_NAME + " JOIN #mp_" + LOBBY_ID);
		String prefix = "PRIVMSG #mp_" + LOBBY_ID + " ";
		String[] expected = new String[] { "!mp mods None", "!mp map 987654", "987654 Random_Map1", "!mp mods Freemod",
				"!mp map 876543", "876543 Random_Map2", "!mp mods NF HD", "!mp map 765432", "765432 Random_Map3" };
		for (int i = 0; i < expected.length; i++) {
			if (i % 3 == 2) {
				server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID,
						"Changed beatmap to https://osu.ppy.sh/b/" + expected[i]);
			} else {
				assertEquals(prefix + expected[i], server.getMessage());
			}
		}

	}

	@Test
	void playerJoinLobbyTest() {
		connect();
		new Thread(() -> {
			client.makeLobby(LOBBY_NAME);
			client.write("LOBBY_CREATED CONFIRMED");
			client.flush();
		}).start();
		String req = server.getMessage();
		assertEquals("PRIVMSG BanchoBot :!mp make " + LOBBY_NAME, req);
		server.pmUser("BanchoBot", USERNAME,
				"Created the tournament match https://osu.ppy.sh/mp/" + LOBBY_ID + " " + LOBBY_NAME);
		server.write(":" + SimpleIRCServer.SERVER_NAME + " JOIN #mp_" + LOBBY_ID);
		server.skipMessages(1);
		server.msgChannel("BanchoBot", "#mp_" + LOBBY_ID, "user1 joined in slot 2.");
		MultiplayerLobby lobby = client.getLobby(LOBBY_NAME);
		assertNotNull(lobby);
		lobby.waitForEvent(MultiplayerEvent.PLAYER_JOIN);
		assertEquals(2, lobby.getPlayerSlot("user1"));
	}

}
