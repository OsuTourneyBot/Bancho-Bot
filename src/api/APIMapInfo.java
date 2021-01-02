package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class APIMapInfo {
	String ID;
	int Length;
	String Mode;
	private String AccessString;
	private String[] Parent;
	
	URL beatmapLink;
	/**
	 * The constructor for the Get Map object.
	 * @param link Takes in the link of the beat map
	 * @param token The JSON object for the token.
	 * @throws IOException
	 */
	public APIMapInfo(String link, APIToken token) throws IOException {
	// Get API token
	
	if  (token.isExpired()){
		token.GenerateCode();
		
	}
	else {
		String AccessString = token.pullData();
		String Bearer = "Bearer " + AccessString;
		String body = API.GET(link, "Authorization","Accept", Bearer , "application/json");
		Parent = body.split(",");
	}	
	
	}
	
	public String GetTitle() {
		return Parent[43].split(":")[1];
	}
	
	public Object GetID() {
		return Parent[1].split(":")[1];
	}
	
	public String GetLength() {
		return Parent[3].split(":")[1];
	}
	public String GetMode() {
		return Parent[2].split(":")[1];
	}
	
}

