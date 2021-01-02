package fileio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tournamentData.Rule;
import tournamentData.RuleBuilder;

public class FileIOSettings {
	public static void readFile(String fileName) {
//		try {
//			// call method to convert json to a string
//			String json = readFileAsString(fileName);
//			
//			
//
////			// call parser
////			parse(json);
//		} catch (Exception e) {
//			System.out.println("cannot find file");
//			e.printStackTrace();
//		}
		try
		{
//			JSONParser parser = new JSONParser();
//			JSONArray a = (JSONArray) parser.parse(new FileReader("ExampleRuleSet.json"));
//
//			for (Object o : a) {
////				JSONObject person = (JSONObject) o;
////
////				String name = (String) person.get("setName");
////				System.out.println(name);
//
////				JSONArray cars = (JSONArray) person.get("players");
////
////				for (Object c : cars) {
////					System.out.println(c + "");
////				}
//			}
	        JSONParser parser = new JSONParser();
	        //Use JSONObject for simple JSON and JSONArray for array of JSON.
	        JSONObject data = (JSONObject) parser.parse(
	              new FileReader(fileName));//path to the JSON file.

	        System.out.println("lll");
//	        String json = data.toString();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
	}

//	public static String readFileAsString(String fileName) throws Exception {
//		return new String(Files.readAllBytes(Paths.get(fileName)));
//	}

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
		int numBans = 0;
		int teamMode = 0;
		int scoreMode = 0;
		int banTime = 0;
		boolean warmUp = false;
		int maxWarmUpLength = 0;
		boolean tieBreaker = false;
		String setName = null;
		int firstTo = 0;
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
			maxWarmUpLength = dataObj.getInt("maxWarmUpLength"); // assuming the warmUp is not warmup (note the lower
																	// cased 'u')
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
	
	public static Rule parser(String responseBody) {
		RuleBuilder builder;
		
		
		JSONObject setting = new JSONObject(responseBody);
		
		String setName = (String)setting.get("setName");
		
		JSONArray players = setting.getJSONArray("players");
		
		String[][] name = new String[players.length()][];
		for (int i = 0; i < players.length(); i++)
		{
			name[i] = new String[players.getJSONArray(i).length()];
			for (int x = 0; x < players.getJSONArray(i).length(); x++)
			{
				name[i][x] = players.getJSONArray(i).getString(x);
			}
			
		}
		
		for (int i = 0; i < name.length ; i++)
		{
			
			for (int x = 0; x < name[i].length; x++)
			{
				System.out.print(name[i][x] + ", ");
			}
			System.out.println();
		}
		
		System.out.println(setName);
		
		
//		JSONArray albums = new JSONArray(responseBody);
//		String[][] pl;
//		for (int i = 0; i < albums.length(); i++) {
//			JSONObject album = albums.getJSONObject(i);
//
//			// extract the ID and stuff
//			int id = album.getInt("firstTo"); // no idea if key: is needed
//			int userId = album.getInt("teamSize");
//			String title = album.getString("setName");
////			String[] players = (String[]) album.get("players");
//			JSONArray players = (JSONArray) album.get("players");
//			
//			System.out.println(id + " " + title + " " + userId);
//			for (int x = 0; x < players.length(); x++)
//			{
//				JSONObject p = players.getJSONObject(x);
//				System.out.println(p);
//				
//			}
//		}

		// modify to add data instead of printing the stuff
		return null;
	}

	/**
	 * Storing data in this order
	 * setName
	 * firstTo
	 * players
	 * teamSize
	 * numBans
	 * teamMode
	 * scoreMode
	 * pickTime
	 * banTime
	 * warmup
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		// TODO Auto-generated method stub
//		readFile("ExampleRuleset.json");
		FileInputStream fileInputStream = null;
		String data="";
		StringBuffer stringBuffer = new StringBuffer("");
		
		try{
		    fileInputStream=new FileInputStream("ExampleRuleset.json");
		    int i;
		    
		    while((i=fileInputStream.read())!=-1)
		    {
		        stringBuffer.append((char)i);
		    }
		    data = stringBuffer.toString();
		    System.out.println(data);
		}
		catch(Exception e){
		        e.printStackTrace();
		}
		finally{
		    if(fileInputStream!=null){  
		        fileInputStream.close();
		    }
		}
		
		parser(data);
//		
//		JSONParser jp = new JSONParser();
//		
//		// parsing data
//		JSONObject jo = (JSONObject) jp.parse(new FileReader("ExampleRuleset.json"));
//		
//		String value = (String) jo.get("setName");
//		System.out.println(value);
		
//		System.out.println("lol");
		
//		// parsing file "JSONExample.json" 
//        Object obj = new JSONParser().parse(new FileReader("ExampleRuleset.json")); 
//        
//        // typecasting obj to JSONObject 
//        JSONObject jo = (JSONObject) obj;
//        
////        String setName = (String) jo.getString("setName");
//        
//        String firstName = (String) jo.get("players");
////        String lastName = (String) jo.get(""); 
//          
//        System.out.println(firstName); 
       //System.out.println(lastName); 
        
		//String setName = (String)jo.getString("setName");
//		System.out.println(setName);
		
		
	}
}
