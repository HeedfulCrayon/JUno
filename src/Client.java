import junoServer.Protocol;
import junoServer.Receivable;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        userNotSet = true;
        try {
            handler = new junoServer.Protocol(this);
            gui = new ClientGUI(this);
            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(gui, "There was an error connecting to the server","Connection Error",JOptionPane.ERROR_MESSAGE);
        }
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
        String regex = "(?<=^|(?<=[^a-zA-Z0-9-_\\\\.]))@([A-Za-z][A-Za-z0-9_]+)";
        Matcher matcher = Pattern.compile(regex).matcher(msg);
        if (matcher.find()) {
            String symUser = matcher.group(0);
            StringBuilder sb = new StringBuilder(symUser);
            String user = (sb.deleteCharAt(0)).toString();
            chatMessage.put("username",user);
        }
        handler.sendMessage(chatMessage);
        gui.newMessage("\t" + userName,msg);
        System.out.println(chatMessage);
    }

    protected void sendMessage(JSONObject msg){
        handler.sendMessage(msg);
    }
}
