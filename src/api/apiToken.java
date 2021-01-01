package api;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class APIToken {

	
	private static JSONObject parent = new JSONObject();
	JSONObject obj = new JSONObject();
	private static JSONObject tokencodes;
	long initiatedTime;
	
	/**
	 * The constructor for trying to get an API Token.
	 * @param Clientid The client ID gotten from OSU API
	 * @param ClientSecret The Client Secret Gotten from OSU API
	 * @throws IOException
	 */
	public APIToken(int Clientid, String ClientSecret){
	// All of our info, creating 
	obj.put("client_id",Clientid);
	obj.put("client_secret",ClientSecret );
	obj.put("grant_type", "client_credentials");
	obj.put("scope", "public");
	
	parent.put("obj", obj.toString());
	}
	/**
	 * Returns the Tokens.
	 * @return JSON Object containing the token.
	 */
	public JSONObject pullData() {
		return tokencodes;
	}
	/**
	 * Checks if the current token is expired
	 * @return True if Expired, False if not expired.
	 */
	public boolean isExpired() {
		return((initiatedTime + (1000 * tokencodes.getInt("expires_in")) <= System.currentTimeMillis()));
			
	}
	/**
	 * Generates the Token and code and starts the timer for when the token will expire
	 * @throws IOException 
	 */
	public void GenerateCode() throws IOException {
		
		URL token = new URL("https://osu.ppy.sh/oauth/token");
		//Set up Connection
		HttpURLConnection con = (HttpURLConnection)token.openConnection();
		
		//Setting up the post request and setting the headers.
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");
		
		//Sending the JSON file.
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(obj.toString());
		wr.flush();
		
		//Checking if there is a successful connection
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) // successful connection
		{
			//read every line and put it into a JSON
		StringBuilder sb = new StringBuilder();  
		BufferedReader br = new BufferedReader(
	            new InputStreamReader(con.getInputStream(), "utf-8"));
	    String line = null;  
	    while ((line = br.readLine()) != null) {  
	        sb.append(line + "\n");  
	        
		}
	    br.close();
	    //Put string into JSON
	    initiatedTime  = System.currentTimeMillis();
	    tokencodes = new JSONObject(sb.toString());
	     }
		
		
	}


}
