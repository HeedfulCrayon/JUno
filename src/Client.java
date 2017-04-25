import junoServer.Protocol;
import junoServer.Receivable;
import org.json.JSONArray;
import org.json.JSONObject;

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
    String userName;
    private Protocol handler;
    private ClientGUI gui;
    public static void main(String [] args){
        Client client = new Client();
    }

    private Client() {
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
            //new Thread(new Writer()).start();
        }catch(IOException e){
            e.printStackTrace();
            handleError("There was an error connecting to the server","Connection Error");
            //JOptionPane.showMessageDialog(gui, "There was an error connecting to the server","Connection Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // TODO: 4/24/2017 Handle unknown messages with error message or log
    @Override
    public void giveMessage(JSONObject message) {
        if(message.has("type")){
            switch (message.getString("type")){
                case "chat":
                    gui.newMessage(message.getString("fromUser"), message.getString("message"));
                    break;
                case "whois":
                    message.remove("type");
                    gui.updateWhoIs(message);
                    break;
                case "application":
                    if (message.has("message")) {
                        JSONObject msg = message.getJSONObject("message");
                        if (msg.has("action")) {
                            switch (msg.getString("action")) {
                                case "cardDealt":
                                    handleCardDealt(msg);
                                    break;
                                case "startCard":
                                    handleStartCard(msg);
                                    if(msg.has("players")){
                                        JSONArray jsnPlayers = msg.getJSONArray("players");
                                        for (Object oPlayer: jsnPlayers){
                                            if(oPlayer instanceof JSONObject){
                                                JSONObject jsnPlayer = (JSONObject)oPlayer;
                                                String user = jsnPlayer.getString("username");
                                                int numOfCards = jsnPlayer.getInt("cards");
                                                for (int i = 0; i < numOfCards; i++){
                                                    handleCardDealt(user);
                                                }
                                            }
                                        }
                                        if(msg.getString("turn").equals(userName)){
                                            gui.turnNotify(msg.getString("user"));
                                        }
                                    }
                                    break;
                                case "playCard":
                                    handlePlayCard(msg);
                                    break;
                                case "turn":
                                    gui.turnNotify(msg.getString("user"));
                                    break;
                                case "callUno":
                                    gui.unoNotify(msg.getString("user"));
                                    break;
                                case "win":
                                    gui.winNotify(msg.getString("username"));
                                    break;
                            }
                        } else if (msg.has("type") && (msg.getString("type").equals("reset"))){
                            gui.resetGameGUI();
                        }
                    }
                    break;
                case "error":
                    handleError(message);
                    break;
                default:
                    System.out.println(message);
            }
        } else if (message.has("action")){
                if (message.getString("action").equals("dealCard")) {
                    addMyCard(message);
                }
        }
    }

    @Override
    public void setUsername(String user) {
        if(userNotSet){
            userName = user;
            userNotSet = false;
        }
    }

    void sendApplicationMsg(String action){
        JSONObject appWrap = new JSONObject();
        appWrap.put("type","application");
        JSONObject msg = new JSONObject();
        msg.put("action",action);
        msg.put("module","juno");
        appWrap.put("message",msg);
        sendMessage(appWrap);
    }

    void sendMessage(String msg){
        JSONObject message = new JSONObject();
        if (msg.equals("whois")){
            message.put("type","whois");
        }else {

            message.put("type", "chat");
            message.put("message", msg);
            String regex = "(?<=^|(?<=[^a-zA-Z0-9-_\\\\.]))@([A-Za-z][A-Za-z0-9_]+)";
            Matcher whisperMatcher = Pattern.compile(regex).matcher(msg);
            if (whisperMatcher.find()) {
                String symUser = whisperMatcher.group(0);
                StringBuilder sb = new StringBuilder(symUser);
                String user = (sb.deleteCharAt(0)).toString();
                message.put("username", user);
            }
            gui.newMessage("\t" + userName, msg);
//            System.out.println(message);
        }
        handler.sendMessage(message);
    }

    void sendMessage(JSONObject msg){
        if(msg.has("action")){
            JSONObject appWrap = new JSONObject();
            appWrap.put("type","application");
            appWrap.put("message",msg);
            System.out.println("Sent: " + appWrap);
            handler.sendMessage(appWrap);
        }else {
            System.out.println("Sent: " + msg);
            handler.sendMessage(msg);
        }
    }

    private void addMyCard(JSONObject message) {
        JSONObject jsnCard = new JSONObject(message.getString("card"));
        Card.Color color = Card.Color.valueOf(jsnCard.getString("color").toUpperCase());
        Card.Value val = Card.Value.valueOf(jsnCard.getString("value").toUpperCase());
        Card card = new Card(color,val);
        gui.addMyCard(card);
    }

    private void handleCardDealt(JSONObject msg) {
        String user = msg.getString("user");
        if(!user.equals(userName)) {
            gui.placeCard(user);
        }
    }

    private void handleCardDealt(String msg) {
        if(!msg.equals(userName)) {
            gui.placeCard(msg);
        }
    }

    private void handlePlayCard(JSONObject msg) {
        //{"type":"application","message":{"action":"handlePlayCard","user":"Ethan","card":"{\"color\":\"WILD\",\"value\":\"WILDD4\"}"}}
        String user = msg.getString("user");
        if(user.equals(userName)) {
            JSONObject jsnCard = new JSONObject(msg.getString("card"));
            Card card = new Card(Card.Color.valueOf(jsnCard.getString("color")), Card.Value.valueOf(jsnCard.getString("value")));
            gui.removeCard(card, user);
        }else{
            gui.removeCard(user);
        }
        JSONObject jsnCard = new JSONObject(msg.getString("card"));
        Card card = new Card(Card.Color.valueOf(jsnCard.getString("color")), Card.Value.valueOf(jsnCard.getString("value")));
        gui.updateDiscard(card);
    }

    private void handleStartCard(JSONObject msg){
        JSONObject startCard = new JSONObject(msg.getString("card"));
        Card discard = new Card(
                Card.Color.valueOf(startCard.getString("color")),
                Card.Value.valueOf(startCard.getString("value")));
        gui.createDiscard(discard);
    }

    private void handleError(JSONObject message){
        String strMessage = message.getString("message");
        gui.displayError(strMessage,"Error");
    }

    private void handleError(String message, String title){
        gui.displayError(message,title);
    }

//    private class Writer implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                while(true){
//                    sendMessage("whois");
//                    Thread.sleep(10000);
//                }
//            }catch(InterruptedException e){
//                e.printStackTrace();
//            }
//
//        }
//    }
}
