import java.io.IOException;

import Bancho.BanchoBot;
import Bancho.LobbyHandler;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		BanchoBot bot = new BanchoBot("fungucide", "49aff0f5");
		bot.connect();
		bot.makeLobby("Fungucide's Game");
		Thread.sleep(20000);
		LobbyHandler lobby = bot.getLobby("Fungucide's Game");
		lobby.close();
	}

}
