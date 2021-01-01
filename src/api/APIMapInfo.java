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
	private JSONObject Parent;
	
	URL beatmapLink;
	/**
	 * The constructor for the Get Map object.
	 * @param link Takes in the link of the beat map
	 * @param token The JSON object for the token.
	 * @throws IOException
	 */
	public APIMapInfo(String link, APIToken token) throws IOException {
	// Get API token
	JSONObject codes = token.pullData();
	AccessString = codes.getString("access_token");
	
	//make header of CAll into correct format.
	//set up URL
	beatmapLink = new URL(link);
	HttpURLConnection con = (HttpURLConnection) beatmapLink.openConnection();
	
	
	con.setRequestMethod("GET");
	con.setRequestProperty("Authorization", "Bearer "+ AccessString);
	con.setDoOutput(true);
	con.setRequestProperty("Accept", "application/json");
	
	
	int responseCode = con.getResponseCode();
	if (responseCode == HttpURLConnection.HTTP_OK) // successful connection
	{	
		//Reading Data and putting it back into a JSON.
		StringBuilder sb = new StringBuilder();  
		BufferedReader br = new BufferedReader(
	            new InputStreamReader(con.getInputStream(), "utf-8"));
	    String line = null;  
	    while ((line = br.readLine()) != null) {  
	        sb.append(line + "\n");  
		}
	    System.out.print(sb);
	    br.close();
	    
	    //JSON object that holds everything
	 	Parent = new JSONObject(sb.toString());}
	
	else { //Unsuccessful connection 
		
		//If the token is expired renew it
		if (token.isExpired()) {
			System.out.print("The Token has expired. Requesting a new one....");
			token.GenerateCode();
			
		}
		else {
			// Else its some other problem we cant fix it.
			System.out.print("The URL that you have inputed is invalid");
			
		}
	}
	   }
	
	public String GetTitle() {
		return Parent.getString("title");
	}
	
	public Object GetID() {
		return Parent.get("id");
	}
	
	public String GetLength() {
		return Parent.getString("total_length");
	}
	
	}

