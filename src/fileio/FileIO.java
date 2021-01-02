package fileio;

import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import tournamentData.Beatmap;
import tournamentData.Mappool;
import tournamentData.Rule;
import tournamentData.RuleBuilder;

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

	/**
	 * Builds the rule from the response body from the json file
	 * 
	 * @param responseBody
	 * @return rule set
	 */
	public static Rule settingParser(String responseBody) {
		// new builder
		RuleBuilder builder;
		// getting the data as a JSONObject
		JSONObject setting = new JSONObject(responseBody);

		// setting players and other stuff required for the builder
		JSONArray players = setting.getJSONArray("players");

		String setName = setting.getString("setName");
		int firstTo = setting.getInt("firstTo");
		String[][] playersList = new String[players.length()][];
		int teamSize = setting.getInt("teamSize");
		int numBans = setting.getInt("numBans");
		int teamMode = setting.getInt("teamMode");
		int scoreMode = setting.getInt("scoreMode");

		// iterate through the players
		for (int i = 0; i < players.length(); i++) {
			// setting the size for the jagged array
			playersList[i] = new String[players.getJSONArray(i).length()];
			for (int x = 0; x < players.getJSONArray(i).length(); x++) {
				playersList[i][x] = players.getJSONArray(i).getString(x);
			}

		}

		// set to the constructor
		builder = new RuleBuilder(setName, firstTo, playersList, teamSize, numBans, teamMode, scoreMode);

		// check for optional stuff
		if (setting.has("pickTime")) {
			int pickTime = setting.getInt("pickTime");
			builder.pickTime(pickTime);
		}
		if (setting.has("banTime")) {
			int banTime = setting.getInt("banTime");
			builder.banTime(banTime);
		}
		if (setting.has("warmUp")) {
			boolean warmUp = setting.getBoolean("warmUp");
			builder.warmUp(warmUp);
		}

		// return rule set
		return new Rule(builder);
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

	/**
	 * this is used to parse the map pool
	 * 
	 * @param responseBody
	 * @return
	 */
	public static Mappool mapPoolParser(String responseBody) {
		// new builder and getting it as a JSON object
		Mappool builder = new Mappool();
		JSONObject map = new JSONObject(responseBody);

		// goes through the map names
		for (String name : map.keySet()) {
			Beatmap beatmap = mapParser(map.getJSONObject(name));
			// insert to mappool here
			builder.insertMap(name, beatmap);
		}

		// return map pool
		return builder;
	}

	// remove later if needed, just here to test if it works
	public static void main(String[] args) {
		Mappool map = mapPoolParser(readFile("Super Scuffed Tournament data.json"));
		System.out.println(map.getSong(1332737).getID());

	}

}
