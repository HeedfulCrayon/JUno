import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Created by Nate on 4/14/2017.
 */
class ClientGUI extends JFrame{
    private static final long serialVersionUID = 3403737665582698443L;
    private Client client;

    private JTextArea messages;
    private JTextArea sendTxt;
    private JScrollPane messageScrollPane;
    private JPanel users;
    private JPanel modules;
    private JPanel menuPanel;
    private Game gamePanel;
    private Hand myHand;
    private Card discard;
    List<JLabel> labels;
    ClientGUI(Client cli){
        client = cli;
        createWindow(buildChat(), buildGame(),buildWhois());
    }
// Creating Window and components
    private void createWindow(JPanel chat, JPanel menu, JPanel whoIs) {
        setLayout(new GridLayout(1,3));
        setSize(new Dimension(1600,600));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        add(chat);
        add(whoIs);
        add(menu);
        setTitle("Game Server");
        sendTxt.grabFocus();
    }

    private JPanel buildGame() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel,BoxLayout.Y_AXIS));
        menuPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        addMenuButtons();
        gamePanel = new Game();
        gamePanel.setLayout(new BorderLayout(0,0));
        gamePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        myHand = new Hand(client.userName);
        JScrollPane scrollHand = new JScrollPane(myHand);
        scrollHand.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollHand.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        gamePanel.add(scrollHand,client.userName);
        menuPanel.add(gamePanel);
        return menuPanel;
    }

    private JPanel buildChat(){
        JPanel chatPanel = new JPanel();
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messages = new JTextArea(13,40);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        messages.setText("");
        messages.setEditable(false);
        messageScrollPane = new JScrollPane(messages);
        messagePanel.add(messageScrollPane);

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.Y_AXIS));
        sendTxt = new JTextArea(5,20);
        sendTxt.setLineWrap(true);
        sendTxt.setWrapStyleWord(true);
        sendTxt.setEditable(true);
        sendTxt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int codeKey = e.getKeyCode();
                int modifierKey = e.getModifiers();
                if (codeKey == KeyEvent.VK_ENTER && modifierKey == KeyEvent.CTRL_MASK){
                    client.sendMessage(send());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        JScrollPane sendScrollPane = new JScrollPane(sendTxt);
        sendPanel.add(sendScrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        JButton send = new JButton("Send");
        send.addActionListener((e) -> {
            client.sendMessage(send());
        });
        buttonPanel.add(send);

        buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((e) -> {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        buttonPanel.add(cancel);
        buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
        sendPanel.add(buttonPanel);
        JSplitPane textBoxes = new JSplitPane(JSplitPane.VERTICAL_SPLIT,messagePanel,sendPanel);
        chatPanel.add(textBoxes);
        return chatPanel;
    }

    private JPanel buildWhois() {
        JPanel whoIsPanel = new JPanel();
        whoIsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        whoIsPanel.setLayout(new GridLayout(1,2));
        modules = new JPanel();
        whoIsPanel.add(modules);
        users = new JPanel();
        whoIsPanel.add(users);
        return whoIsPanel;
    }

    private void addMenuButtons(){
        JToolBar toolbar = new JToolBar();
        toolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
        JButton joinGame = new JButton("Join Game");
        JButton playGame = new JButton("Play Game");
        JButton callUno = new JButton("Call Uno");
        JButton quitGame = new JButton("Quit Game");
        JButton resetGame = new JButton("Reset Game");
        joinGame.addActionListener((e -> {
            joinGame.setVisible(false);
            playGame.setVisible(true);
            quitGame.setVisible(true);
            menuPanel.updateUI();
            joinGame();
        }));
        toolbar.add(joinGame);
        playGame.addActionListener((e -> {
            playGame.setVisible(false);
            callUno.setVisible(true);
            quitGame.setVisible(true);
//            gamePanel.start();
            menuPanel.updateUI();
            playGame();
        }));
        playGame.setVisible(false);
        toolbar.add(playGame);
        callUno.addActionListener((e -> {
            callUno();
        }));
        callUno.setVisible(false);
        toolbar.add(callUno);
        quitGame.addActionListener((e -> {
            quitGame.setVisible(false);
            callUno.setVisible(false);
            joinGame.setVisible(true);
            menuPanel.updateUI();
            quitGame();
        }));
        quitGame.setVisible(false);
        toolbar.add(quitGame);
        resetGame.addActionListener((e -> {
            quitGame();
            resetGame();
        }));
        toolbar.add(resetGame);
        toolbar.setFloatable(false);
        toolbar.setBorderPainted(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(toolbar);
        menuPanel.updateUI();
    }
// Server Update UI

    void addMyCard(Card card){
        JSONObject playCard = new JSONObject();
        JSONObject newCard = new JSONObject();
        playCard.put("action","playCard");
        card.addActionListener((e -> {
            if (card.getColor() == Card.Color.WILD){
                String[] colors = { "GREEN", "BLUE", "YELLOW", "RED" };
                String color = (String) JOptionPane.showInputDialog(null,"Select a color","Color Selection",JOptionPane.QUESTION_MESSAGE,null,colors,colors[0]);
                newCard.put("color",color);
            }else {
                newCard.put("color", card.getColor());
            }
            newCard.put("value",card.getValue());
            playCard.put("card",newCard);
            playCard.put("module","juno");
            client.sendMessage(playCard);
        }));
        myHand.addCard(card);
        myHand.updateUI();
    }

    void createDiscard(Card card){
        if(!gamePanel.hasComponent("server")){
            gamePanel.start();
        }
        Card draw = new Card(BorderLayout.CENTER);
        draw.addActionListener((e -> {
            JSONObject dealCard = new JSONObject();
            JSONObject dealAction = new JSONObject();
            dealCard.put("type","application");
            dealAction.put("action","dealCard");
            dealAction.put("module","juno");
            dealCard.put("message",dealAction);
            client.sendMessage(dealCard);
        }));
        gamePanel.getHand("draw").addCard(draw);
        discard = card;
        gamePanel.getHand("discard").addCard(card);
        gamePanel.updateUI();
    }

    void updateDiscard(Card card){
        gamePanel.getHand("discard").removeCard(discard);
        discard = card;
        gamePanel.getHand("discard").addCard(card);
        gamePanel.updateUI();
    }

//    void placeCard(JSONObject msg){
//        if(msg.getString("action").equals("dealCard")){
//            JSONObject jsnCard = new JSONObject(msg.getString("card"));
//            Card.Color c = Card.Color.valueOf(jsnCard.getString("color").toUpperCase());
//            Card.Value val = Card.Value.valueOf(jsnCard.getString("value").toUpperCase());
//            Card card = new Card(c,val);
//            card.addActionListener((e -> {
//                JSONObject playCard = new JSONObject();
//                JSONObject newCard = new JSONObject();
//                playCard.put("action","playCard");
//                newCard.put("color",c);
//                newCard.put("value",val);
//                playCard.put("card",newCard);
//                playCard.put("module","juno");
//                client.sendMessage(playCard);
//                //myCardPanel.remove(card);
//                //myCardPanel.updateUI();
//            }));
//            myHand.add(card);
//            myHand.updateUI();
//        }else if(msg.getString("action").equals("cardDealt")){
//            String user = msg.getString("user");
//            if(!user.equals(client.userName)) {
//                if (gamePanel.hasComponent(user)) {
//                    Hand hand = gamePanel.getHand(user);
//                    Card card = new Card(gamePanel.getLocation(user));
//                    card.setBorder(BorderFactory.createEmptyBorder());
//                    hand.add(card);
//                    hand.updateUI();
//                } else {
//                    Hand hand = new Hand(user);
//                    gamePanel.add(hand, user);
//                    Card card = new Card(gamePanel.getLocation(user));
//                    card.setBorder(BorderFactory.createEmptyBorder());
//                    hand.add(card);
//                    hand.updateUI();
//                }
//            }
//        } else if(msg.getString("action").equals("startCard")){
//            JSONObject startCard = new JSONObject(msg.getString("card"));
//            Card discard = new Card(
//                    Card.Color.valueOf(startCard.getString("color")),
//                    Card.Value.valueOf(startCard.getString("value")));
//            gamePanel.getHand("server").add(discard);
//            //System.out.println(Card.Color.valueOf(startCard.getString("color")) + " " + Card.Value.valueOf(startCard.getString("value")));
//            //discardPile.add(discard);
//            //gamePanel.add(discardPile,true);
//            gamePanel.updateUI();
//        }
//    }

    void placeCard(String user){
        if(gamePanel.hasComponent(user)) {
            gamePanel.getHand(user).addCard(new Card(gamePanel.getLocation(user)));
            gamePanel.updateUI();
        }else{
            Hand hand = new Hand(user);
            gamePanel.add(hand, user);
            Card card = new Card(gamePanel.getLocation(user));
            hand.add(card);
            hand.updateUI();
        }
    }

    void newMessage(String user,String msg){
        messages.append(user + ": " + msg + "\r\n");
        JScrollBar bar = messageScrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    void updateWhoIs(JSONObject jsnWhois){
        client.gameUserCount = 0;
        JSONArray jsnUsers = jsnWhois.getJSONObject("message").getJSONArray("users");
        System.out.println(jsnWhois);
        List<JSONObject> userList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsnUsers.length(); i++){
            userList.add(jsnUsers.getJSONObject(i));
        }
        users.removeAll();
        users.setLayout(new BoxLayout(users,BoxLayout.Y_AXIS));
        users.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JLabel userTitle = new JLabel("Users Online");
        userTitle.setFont(new Font(userTitle.getFont().getName(),Font.BOLD,14));
        users.add(userTitle);
        users.add(Box.createHorizontalGlue());
        for (JSONObject user:userList) {
            users.add(Box.createRigidArea(new Dimension(10,5)));
            users.add(new JLabel(user.get("username").toString()));
            JSONArray jsnUserModules = user.getJSONArray("modules");
            JLabel userModules = new JLabel("Games: ");
            for (int i = 0; i < jsnUserModules.length(); i++){
                userModules.setText(userModules.getText() + jsnUserModules.getString(i) + "  ");
                if (jsnUserModules.getString(i).equals("juno")){
                    client.gameUserCount++;
                }
            }
            users.add(userModules);
        }
        users.updateUI();

        JSONArray jsnModules = jsnWhois.getJSONObject("message").getJSONArray("modules");
        List<JSONObject> moduleList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsnModules.length(); i++){
            moduleList.add(jsnModules.getJSONObject(i));
        }
        modules.removeAll();
        modules.setLayout(new BoxLayout(modules,BoxLayout.Y_AXIS));
        modules.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JLabel gamesTitle = new JLabel("Games");
        gamesTitle.setFont(new Font(gamesTitle.getFont().getName(),Font.BOLD,14));
        modules.add(gamesTitle);
        modules.add(Box.createHorizontalGlue());
        for (JSONObject module:moduleList) {
            modules.add(Box.createRigidArea(new Dimension(10,5)));
            String strGameName = module.get("moduleName").toString();
            modules.add(new JLabel(strGameName));
            JLabel status = new JLabel();
            if(module.getBoolean("started")){
                if (strGameName.equals("juno") && client.gameUserCount >= 2){
                    client.gameStarted = true;
                }
                status.setText("started");
                status.setForeground(Color.GREEN);
            }else{
                status.setText("not started");
                status.setForeground(Color.RED);
            }
            modules.add(status);
        }
        modules.updateUI();
    }

//    void removeCard(JSONObject msg) {
//        //{"type":"application","message":{"action":"playCard","user":"Ethan","card":"{\"color\":\"WILD\",\"value\":\"WILDD4\"}"}}
//        String user = msg.getString("user");
//        JSONObject jsnCard = new JSONObject(msg.getString("card"));
//        Card card = new Card(Card.Color.valueOf(jsnCard.getString("color")),Card.Value.valueOf(jsnCard.getString("value")));
//        gamePanel.getHand(user).removeCard(card);
//        gamePanel.updateUI();
//    }
    void removeCard(Card card, String user){
        gamePanel.getHand(user).removeCard(card);
    }

    void removeCard(String user){
        Card card = new Card(gamePanel.getLocation(user));
        gamePanel.getHand(user).removeCard(card);
    }
// Client Upodate UI
    void resetGameGUI() {
        remove(menuPanel);
        menuPanel = buildGame();
        add(menuPanel);
        menuPanel.updateUI();
    }

    void displayError(JSONObject message) {
        JOptionPane.showMessageDialog(null,message.getString("message"),"Error",JOptionPane.ERROR_MESSAGE);
    }

// Button functions
    private String send() {
        String message = sendTxt.getText();
        sendTxt.setText("");
        return message;
    }

    private void joinGame() {
        JSONObject joinMsg = new JSONObject();
        joinMsg.put("type", "application");
        JSONObject game = new JSONObject();
        game.put("action", "joinGame");
        game.put("module", "juno");
        joinMsg.put("message", game);
        client.sendMessage(joinMsg);
    }

    private void playGame(){
        JSONObject playMsg = new JSONObject();
        playMsg.put("type", "application");
        JSONObject game = new JSONObject();
        game.put("action", "startGame");
        game.put("module", "juno");
        playMsg.put("message", game);
        client.sendMessage(playMsg);
    }

    private void quitGame() {
        JSONObject playMsg = new JSONObject();
        playMsg.put("type", "application");
        JSONObject game = new JSONObject();
        if (client.gameStarted) {
            game.put("action", "quitGame");
        }else{
            game.put("action", "quitGame");
        }
        game.put("module", "juno");
        playMsg.put("message", game);
        client.sendMessage(playMsg);
        resetGameGUI();
    }

    private void resetGame() {
        JSONObject playMsg = new JSONObject();
        playMsg.put("type", "application");
        JSONObject game = new JSONObject();
        game.put("action", "reset");
        game.put("module", "juno");
        playMsg.put("message", game);
        client.sendMessage(playMsg);
        myHand.removeAll();
    }

    private void callUno() {

    }
}
