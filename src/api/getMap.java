package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONObject;

public class getMap {
	String ID;
	int Length;
	String Mode;
	private String AccessString;
	private JSONObject Parent;
	
	URL beatmapLink;
	public getMap(String link) throws IOException {
	// Get API token
	apiToken token = new apiToken();
	JSONObject codes = token.pullData();
	AccessString = codes.getString("access_token");
	
	//make header of CAll into correct format.
	//set up URL
	beatmapLink = new URL("https://osu.ppy.sh/api/v2/beatmaps/218189");
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
	
	
	public static void main(String[] args) {
	try {
		getMap x = new getMap("x");
		System.out.print(x.GetID());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
