package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import irc.IRCClient;
import irc.event.IRCEvent;

class IRCTest {

	@Rule
	public Timeout globalTimeout = Timeout.seconds(1);

	private static final int PORT = 6667;
	private static final String SERVER_NAME="!server";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String CHANNEL_NAME="test_channel";
	private SimpleIRCServer server;
	private IRCClient client;

	@BeforeEach
	void init() throws Exception {
		server = new SimpleIRCServer(PORT);
		server.start();
		client = new IRCClient("localhost", PORT, USERNAME, PASSWORD);
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
	void connectSuccessTest() throws InterruptedException {
		new Thread(() -> {
			try {
				client.connect();
				assertTrue("Connected", client.isConnected());
			} catch (Exception e) {
				e.printStackTrace();
				fail("Failed to register");
			}
		}).start();
		// Make sure we get 3 messages
		int res = 0;
		for (int i = 0; res >= 0 && i < 3; i++) {
			String message = server.getMessage();
			switch (message) {
			case "PASS " + PASSWORD:
				res |= 1;
				break;
			case "NICK " + USERNAME:
				res |= 2;
				break;
			case "USER " + USERNAME:
				res |= 4;
				break;
			default:
				res = -1;
				break;
			}
		}
		assertEquals(7, res, "PASS, NICK and USER messages");
		server.write(":server 001 " + USERNAME + " :Welcome");
	}

	@Test
	void connectFailTest() {
		new Thread(() -> {
			try {
				client.connect();
			} catch (Exception e) {
				assertEquals("Register Failed", e.getMessage());
				assertTrue("Not Connected", !client.isConnected());
			}
		}).start();
		server.skipMessages(3);
		server.write(":test 464");
	}

	@Test
	void pingPongTest() {
		connect();
		server.write("PING test ping");
		assertEquals("PONG test ping", server.getMessage());
	}
	
	@Test
	void joinChannelTest() {
		connect();
		server.write(":"+SERVER_NAME+" JOIN #"+CHANNEL_NAME);
		client.waitForEvent(IRCEvent.JOIN);
		assertNotNull(client.getChannel(CHANNEL_NAME));
	}
}
