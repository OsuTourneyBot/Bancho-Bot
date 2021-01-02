package fileio;

import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import tournamentData.Rule;
import tournamentData.RuleBuilder;

public class FileIOSettings {
	
	/**
	 * Reads the json file
	 * @param fileName
	 * @return responsebody from file
	 */
	public static String readFile(String fileName) {
		FileInputStream fileInputStream = null;
		String data="";
		StringBuffer stringBuffer = new StringBuffer("");
		
		try{
		    fileInputStream=new FileInputStream(fileName);
		    int i;
		    
		    while((i=fileInputStream.read())!=-1)
		    {
		        stringBuffer.append((char)i);
		    }
		    data = stringBuffer.toString();
		}
		catch(Exception e){
		        e.printStackTrace();
		}
		finally{
		    if(fileInputStream!=null){  
		        try {
					fileInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		
		return data;
	}
	
	/**
	 * Builds the rule from the response body from the json file
	 * @param responseBody
	 * @return rule set
	 */
	public static Rule parser(String responseBody) {
		RuleBuilder builder;
		JSONObject setting = new JSONObject(responseBody);
		JSONArray players = setting.getJSONArray("players");

		String setName = setting.getString("setName");
		int firstTo = setting.getInt("firstTo");
		String[][] playersList = new String[players.length()][];
		int teamSize = setting.getInt("teamSize");
		int numBans = setting.getInt("numBans");
		int teamMode = setting.getInt("teamMode");
		int scoreMode = setting.getInt("scoreMode");
		
		// iterate through the players
		for (int i = 0; i < players.length(); i++)
		{
			playersList[i] = new String[players.getJSONArray(i).length()];
			for (int x = 0; x < players.getJSONArray(i).length(); x++)
			{
				playersList[i][x] = players.getJSONArray(i).getString(x);
			}
			
		}
		
		// set to the constructor
		builder = new RuleBuilder(setName, firstTo, playersList, teamSize, numBans, teamMode, scoreMode);

		// check for optional stuff
		if (setting.has("pickTime"))
		{
			int pickTime = setting.getInt("pickTime");
			builder.pickTime(pickTime);
		}
		if (setting.has("banTime"))
		{
			int banTime = setting.getInt("banTime");			
			builder.banTime(banTime);
		}
		if (setting.has("warmUp"))
		{
			boolean warmUp = setting.getBoolean("warmUp");			
			builder.warmUp(warmUp);
		}
		
		return new Rule(builder);
	}
}
