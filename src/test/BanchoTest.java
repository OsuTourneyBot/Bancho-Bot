package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import osu.bancho.BanchoBot;
import osu.lobby.LobbyHandler;

class BanchoTest {

	@Rule
	public Timeout globalTimeout = Timeout.seconds(1);

	private static final int PORT = 6667;
	private static final String SERVER_NAME = "!server";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String LOBBY_NAME = "test_lobby";
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
	void createLobbyTest() {
		connect();
		new Thread(() -> {
			client.makeLobby(LOBBY_NAME);
			LobbyHandler lobby = client.getLobby(LOBBY_NAME);
			assertNotNull(lobby);
			lobby.message("test message");
			lobby.flush();
		}).start();
		String req = server.getMessage();
		assertEquals("PRIVMSG BanchoBot :!mp make " + LOBBY_NAME, req);
		server.write(":BanchoBot" + SERVER_NAME + " PRIVMSG " + USERNAME
				+ " :Created the tournament match https://osu.ppy.sh/mp/0123456 " + LOBBY_NAME);
		String msg = server.getMessage();
		assertEquals("PRIVMSG #mp_0123456 test message", msg);
	}

}
