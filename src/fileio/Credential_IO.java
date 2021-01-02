package fileio;

import java.util.HashMap;
import org.json.JSONObject;

public class Credential_IO {
	/**
	 * This is used to read the credentials from a JSON file
	 * @param fileName
	 * @return
	 */
	public static HashMap<String, String> loadCredentials(String fileName)
	{
		// read the file
		String responseBody = FileIO.readFile(fileName);
		
		JSONObject cred = new JSONObject(responseBody);
		
		// stores the credentials
		HashMap<String, String> credential = new HashMap<String, String>();
		
		// getting credentials
		String IRCUser = cred.getString("IRCUsername");
		String IRCPass = cred.getString("IRCPassword");
		String APIID = cred.getString("APIID");
		String APIKey = cred.getString("APIKey");
		
		// putting it into the map
		credential.put("IRCUsername", IRCUser);
		credential.put("IRCPassword", IRCPass);
		credential.put("APIID", APIID);
		credential.put("APIKey", APIKey);
		
		return credential;
	}
}
