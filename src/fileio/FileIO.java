package fileio;

import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import tournamentData.Beatmap;
import tournamentData.Mappool;
import tournamentData.Ruleset;
import tournamentData.RulesetBuilder;

public class FileIO {

	/**
	 * Reads the json file
	 * 
	 * @param fileName
	 * @return responsebody from file
	 */
	public static String readFile(String fileName) {
		FileInputStream fileInputStream = null;
		String data = "";
		StringBuffer stringBuffer = new StringBuffer("");

		try {
			fileInputStream = new FileInputStream(fileName);
			int i;

			// reads until there is no more data
			while ((i = fileInputStream.read()) != -1) {
				stringBuffer.append((char) i); // converts data to append
			}
			data = stringBuffer.toString(); // converts it to string
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close file
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// return response body
		return data;
	}

	public static Ruleset ruleParser(String body) {
		return ruleParser(new JSONObject(body));
	}

	/**
	 * Builds the rule from the response body from the json file
	 * 
	 * @param responseBody
	 * @return rule set
	 */
	public static Ruleset ruleParser(JSONObject body) {
		// new builder
		RulesetBuilder builder;

		// setting players and other stuff required for the builder
		JSONArray players = body.getJSONArray("players");

		int firstTo = body.getInt("firstTo");
		String[][] playersList = new String[players.length()][];
		int teamSize = body.getInt("teamSize");
		int numBans = body.getInt("numBans");
		int teamMode = body.getInt("teamMode");
		int scoreMode = body.getInt("scoreMode");

		// iterate through the players
		for (int i = 0; i < players.length(); i++) {
			// setting the size for the jagged array
			playersList[i] = new String[players.getJSONArray(i).length()];
			for (int x = 0; x < players.getJSONArray(i).length(); x++) {
				playersList[i][x] = players.getJSONArray(i).getString(x);
			}

		}

		// set to the constructor
		builder = new RulesetBuilder(firstTo, playersList, teamSize, numBans, teamMode, scoreMode);

		// check for optional stuff
		if (body.has("pickTime")) {
			int pickTime = body.getInt("pickTime");
			builder.pickTime(pickTime);
		}
		if (body.has("banTime")) {
			int banTime = body.getInt("banTime");
			builder.banTime(banTime);
		}
		if (body.has("warmUp")) {
			boolean warmUp = body.getBoolean("warmUp");
			builder.warmUp(warmUp);
		}
		if (body.has("tiebreaker")) {
			String[] tiebreaker = new String[body.getJSONArray("tiebreaker").length()];
			for (int i = 0; i < tiebreaker.length; i++) {
				tiebreaker[i] = body.getJSONArray("tiebreaker").getString(i);
			}
			builder.tieBreaker(tiebreaker);
		}

		// return rule set
		return new Ruleset(builder);
	}

	/**
	 * This is used to parse the beat maps
	 * 
	 * @param map
	 * @return
	 */
	public static Beatmap mapParser(JSONObject map) {
		// new builder
		Beatmap builder;

		// getting required data
		int ID = map.getInt("ID");
		int mod = map.getInt("mod");
		boolean freeMod = map.getBoolean("freeMod");

		// build and return builder
		builder = new Beatmap(ID, mod, freeMod);
		return builder;
	}

	public static Mappool mappoolParser(String map) {
		return mappoolParser(new JSONObject(map));
	}

	/**
	 * this is used to parse the map pool
	 * 
	 * @param responseBody
	 * @return
	 */
	public static Mappool mappoolParser(JSONObject body) {
		// new builder and getting it as a JSON object
		Mappool builder = new Mappool();

		// goes through the map names
		for (String name : body.keySet()) {
			Beatmap beatmap = mapParser(body.getJSONObject(name));
			// insert to mappool here
			builder.insertMap(name, beatmap);
		}

		// return map pool
		return builder;
	}

	// remove later if needed, just here to test if it works
	public static void main(String[] args) {
		Mappool map = mappoolParser(readFile("Super Scuffed Tournament data.json"));
		System.out.println(map.getSong(1332737).getID());

	}

}
