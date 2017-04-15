import junoServer.Protocol;
import junoServer.Receivable;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Nate on 4/10/2017.
 */
public class Client implements Receivable{
    public static void main(String [] args){
        Client client = new Client();
    }

    public Client() {
        try {
            Protocol protocol = new junoServer.Protocol("Nate",this);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void giveMessage(JSONObject message) {

    }
}
