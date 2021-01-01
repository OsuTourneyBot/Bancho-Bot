package api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class apiToken {

	String ID;
	int Length;
	String Mode;
	JSONObject parent = new JSONObject();
	JSONObject obj = new JSONObject();
	URL token;
	
	public apiToken() throws IOException {
	// All of our info, creating 
	obj.put("client_id",4381);
	obj.put("client_secret","nt9P2OeMY0PnHRubDvGg5T7VmHExK6b6QRYgiPpL" );
	obj.put("grant_type", "client_credentials");
	obj.put("scope", "public");
	parent.put("obj", obj.toString());
	
	//Setting up the URL
	token = new URL("https://osu.ppy.sh/oauth/token");
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
    parent = new JSONObject(sb.toString());
     }
	
	} 
	
	protected JSONObject pullData() {
		return parent;
	}

}
