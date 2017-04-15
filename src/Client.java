import junoServer.Protocol;
import junoServer.Receivable;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by Nate on 4/10/2017.
 */
public class Client implements Receivable{
    private boolean userNotSet;
    private String userName;
    private Protocol handler;
    private ClientGUI gui;
    public static void main(String [] args){
        Client client = new Client();
    }

    public Client() {
        try {
            handler = new junoServer.Protocol(this);
        }catch(IOException e){
            e.printStackTrace();
        }

        gui = new ClientGUI(this);
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void giveMessage(JSONObject message) {
        if(message.getString("type").equals("acknowledge")){
            System.out.println("Logged in");
        }else if(message.getString("type").equals("chat")){
            gui.newMessage(message.getString("fromUser"),message.getString("message"));
        }
        System.out.println(message);
    }

    @Override
    public void setUsername(String user) {
        if(userNotSet){
            userName = user;
            userNotSet = false;
        }
    }

    protected void sendMessage(String msg){
        JSONObject chatMessage = new JSONObject();
        chatMessage.put("type","chat");
        chatMessage.put("message",msg);
        handler.sendMessage(chatMessage);
        gui.newMessage("Nate",msg);
        System.out.println(chatMessage);
    }

    protected void sendMessage(String user, String msg){
        JSONObject chatMessage = new JSONObject();
        chatMessage.put("type","chat");
        chatMessage.put("message",msg);
        chatMessage.put("username",user);
        handler.sendMessage(chatMessage);
        gui.newMessage("Nate",msg);
        System.out.println(chatMessage);
    }
}
