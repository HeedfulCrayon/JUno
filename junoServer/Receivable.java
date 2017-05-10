package junoServer;

import org.json.JSONObject;

public interface Receivable {
	
	public void giveMessage(JSONObject message);
	public void setUsername(String user);
}
