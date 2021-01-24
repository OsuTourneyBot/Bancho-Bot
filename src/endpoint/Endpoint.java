package endpoint;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;

import api.OSUAPI;
import bancho.BanchoBot;
import fileio.Credential_IO;
import fileio.FileIO;
import refBot.RefBot;
import spark.Request;
import spark.Response;
import spark.Spark;
import tournamentData.Mappool;
import tournamentData.Ruleset;

public class Endpoint {

	private BanchoBot bot;
	private HashMap<Integer, RefBot> refBots;

	public Endpoint(String username, String password) {
		try {
			bot = new BanchoBot(username, password);
			bot.connect();

			refBots = new HashMap<Integer, RefBot>();
			Spark.post("/create-match", (request, response) -> {
				return startMatch(request, response);
			});

			Spark.get("/test", (request, response) -> {
				response.body("test");
				return response.body();
			});
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	private String startMatch(Request request, Response response) {
		JSONObject body = parseJSON(request, response);
		if (body != null) {
			try {
				Mappool mappool = null;
				Ruleset rule = null;
				String title = null;
				// Check to make sure there is a mappool present
				if (body.has("mappool")) {
					mappool = FileIO.mappoolParser(body.getJSONObject("mappool"));
				}
				// Check to make sure ther is a ruleset present
				if (body.has("ruleset")) {
					rule = FileIO.ruleParser(body.getJSONObject("ruleset"));
				}
				// Make sure a title has been chosen
				if (body.has("title")) {
					title = body.getString("title");
				} else if (body.has("acronym")) {
					title = body.getString("acronym") + ": (" + rule.getTeamNames()[0] + ") vs ("
							+ rule.getTeamNames()[1] + ")";
				}

				if (mappool != null && rule != null && title != null) {
					RefBot refBot = new RefBot(bot, title, rule, mappool);
					int hash = refBot.hashCode();
					while (refBots.containsKey(hash)) {
						hash++;
					}
					refBots.put(hash, refBot);
					response.status(200);
					response.type("application/json;charset=UTF-8");
					response.body("{id:" + hash + "}");
					refBot.start();
				} else {
					badRequest((mappool == null ? "Missing mappool in JSON\n" : "")
							+ (rule == null ? "Missing rule in JSON\n" : "")
							+ (title == null ? "Missing title in JSON\n" : ""), response);
				}
			} catch (Exception e) {
				badRequest("Error when parsing\n" + e.getMessage(), response);
				return response.body();
			}
		}
		return response.body();
	}

	private JSONObject parseJSON(Request request, Response response) {
		if (!request.contentType().contains("application/json")) {
			String responseText = "Not expecting content-type " + request.contentType()
					+ "\nExpecting application/json";
			badRequest(responseText, response);
		}
		JSONObject body;
		try {
			body = new JSONObject(request.body());
		} catch (Exception e) {
			body = null;
			badRequest("Bad JSON object\n" + e.getMessage(), response);
		}
		return body;
	}

	private void badRequest(String message, Response response) {
		response.status(400);
		response.type("text/plain;charset=UTF-8");
		response.body(message);
	}

	public BanchoBot getBanchoBot() {
		return bot;
	}

	public static void main(String[] args) {
		HashMap<String, String> credentials = Credential_IO.loadCredentials("Credentials.json");

		String IRCUsername = credentials.get("IRCUsername");
		String IRCPassword = credentials.get("IRCPassword");

		int APICredentials = Integer.parseInt(credentials.get("APIID"));
		String APIKey = credentials.get("APIKey");

		OSUAPI.setCredentials(APICredentials, APIKey);

		Endpoint endpoint = new Endpoint(IRCUsername, IRCPassword);
		System.out.println("Listening to Port: " + Spark.port());
		endpoint.getBanchoBot().interactive();
	}
}
