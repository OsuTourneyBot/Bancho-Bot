package fileio;

import java.nio.file.*;
import org.json.*;

import tournamentData.Rule;
import tournamentData.RuleBuilder;

public class FileIOSettings {
	public void readFile(String fileName) {
		try {
			// call method to convert json to a string
			String json = readFileAsString(fileName);

			// call parser
			parse(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readFileAsString(String fileName) throws Exception {
		return new String(Files.readAllBytes(Paths.get(fileName)));
	}

	// Time to parse data
	public static Rule parse(String responseBody) {
		RuleBuilder builder;

		// the entire response data
		JSONArray data = new JSONArray(responseBody);
		
		// using default values
//		int teamSize = 0;
//		int numBans = 0; 
//		int teamMode = 2;
//		int scoreMode = 3;
//		int banTime = 120;
//		boolean warmUp = true;
//		int maxWarmUpLength = 300; 
//		boolean tieBreaker = false;
//		String setName = null;
//		int firstTo = 0;
//		String[][] players = null;
		
		// switch later (either initialize to 0/null or default values)
		int teamSize = 0;
		int numBans = 0 ; 
		int teamMode = 0;
		int scoreMode = 0;
		int banTime = 0;
		boolean warmUp = false;
		int maxWarmUpLength = 0 ; 
		boolean tieBreaker = false ;
		String setName = null ;
		int firstTo = 0 ;
		String[][] players = null;
		
		// going through all the data
		for (int i = 0; i < data.length(); i++) {
			// holds the individual data object
			JSONObject dataObj = data.getJSONObject(i);

			// extract the ID and stuff
			
			setName = dataObj.getString("setName");
			firstTo = dataObj.getInt("firstTo");
			
			// prob need to change (try one or the other to see if any of them work)
			players = (String[][]) dataObj.get("players");
			
			// doesn't work btw 
//			JSONArray ja = dataObj.getJSONArray("players");
//			for (int x = 0; x < ja.length(); x++)
//			{
//				JSONObject jo = ja.getJSONObject(x);
//				//players = 
//			}

			teamSize = dataObj.getInt("teamSize");
			numBans = dataObj.getInt("numBans");
			teamMode = dataObj.getInt("teamMode");
			scoreMode = dataObj.getInt("scoreMode");
			banTime = dataObj.getInt("banTime");
			warmUp = dataObj.getBoolean("warmUp");
			maxWarmUpLength = dataObj.getInt("maxWarmUpLength"); // assuming the warmUp is not warmup (note the lower cased 'u')
			tieBreaker = dataObj.getBoolean("tieBreaker");
			
		}
		
		builder = new RuleBuilder(setName, firstTo, players, teamSize, numBans, teamMode, scoreMode);
		builder.banTime(banTime);
		builder.warmUp(warmUp);
		builder.maxWarmupLength(maxWarmUpLength);
		builder.tieBreaker(tieBreaker);
		
		// modify to add data instead of printing the stuff
		return new Rule(builder);
	}
}
