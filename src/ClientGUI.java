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

class ClientGUI extends JFrame{
    private static final long serialVersionUID = -2929524721374854993L;
    private Client client;
    private String currentTurn;
    private JTextArea messages;
    private JTextArea sendTxt;
    private JScrollPane messageScrollPane;
    private JPanel users;
    private JPanel modules;
    private JPanel menuPanel;
    private Game gamePanel;
    private Hand myHand;
    private Card discard;
    private JButton playGame;
    private JButton callUno;
    private JButton quitGame;

    ClientGUI(Client cli){
        client = cli;
        createWindow(buildChat(), buildMenu(),buildWhois(),buildGame());
    }

    private void createWindow(JPanel chat, JPanel menu, JPanel whoIs, Game game) {
        setLayout(new BorderLayout());
        setSize(new Dimension(1800,600));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        add(chat,BorderLayout.WEST);
        add(whoIs,BorderLayout.CENTER);
        add(menu,BorderLayout.NORTH);
        add(game,BorderLayout.EAST);
        setTitle("Game Server");
        sendTxt.grabFocus();
    }

    private JPanel buildMenu(){
        menuPanel = new JPanel();
        addMenuButtons();
        return menuPanel;
    }

    private Game buildGame() {
        gamePanel = new Game();
        gamePanel.setPreferredSize(new Dimension(595,595));
        gamePanel.setLayout(new BorderLayout(0,0));
        gamePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        myHand = new Hand(client.userName);
        JScrollPane scrollHand = new JScrollPane(myHand);
        myHand.setVisible(false);
        scrollHand.setBorder(null);
        scrollHand.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollHand.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        gamePanel.add(scrollHand,client.userName);
        return gamePanel;
    }

    private JPanel buildChat(){
        JPanel chatPanel = new JPanel();
        chatPanel.setPreferredSize(new Dimension(595,595));
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
        send.addActionListener((e) -> client.sendMessage(send()));
        buttonPanel.add(send);

        buttonPanel.add(Box.createRigidArea(new Dimension(10,0)));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((e) -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
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
        whoIsPanel.setPreferredSize(new Dimension(595,595));
        return whoIsPanel;
    }

    private void addMenuButtons(){
        playGame = new JButton("Play Game");
        callUno = new JButton("Call Uno");
        quitGame = new JButton("Quit Game");
        JButton resetGame = new JButton("Reset Game");
        playGame.addActionListener((e -> {
            playGame.setVisible(false);
            menuPanel.updateUI();
            client.sendApplicationMsg("startGame");
        }));
        menuPanel.add(playGame);
        callUno.addActionListener((e -> callUno()));
        callUno.setVisible(false);
        menuPanel.add(callUno);
        quitGame.addActionListener((e -> {
            quitGame.setVisible(false);
            menuPanel.updateUI();
            client.sendApplicationMsg("quit");
            resetGameGUI();
        }));
        quitGame.setVisible(false);
        menuPanel.add(quitGame);
        resetGame.addActionListener((e -> {
            client.sendApplicationMsg("reset");
            myHand.removeAll();
        }));
        menuPanel.add(resetGame);
        menuPanel.updateUI();
    }

    private String send() {
        String message = sendTxt.getText();
        sendTxt.setText("");
        return message;
    }

    private void callUno() {
        client.sendApplicationMsg("callUno");
    }

    void addMyCard(Card card){
        if(!myHand.isVisible()){
            myHand.setVisible(true);
            callUno.setVisible(true);
            quitGame.setVisible(true);

        }
        JSONObject jsnCardWrapper = new JSONObject();
        JSONObject jsnCard = new JSONObject();
        jsnCardWrapper.put("action","playCard");
        card.addActionListener((e -> {
            if (card.getColor() == Card.Color.WILD){
                String[] colors = { "GREEN", "BLUE", "YELLOW", "RED" };
                Card.Color.values();
                String color = (String) JOptionPane.showInputDialog(null,"Select a color","Color Selection",JOptionPane.QUESTION_MESSAGE,null,colors,colors[0]);
                if(!(color == null)) {
                    jsnCard.put("color", color);
                    jsnCard.put("value", card.getValue());
                    jsnCardWrapper.put("card", jsnCard);
                    jsnCardWrapper.put("module", "juno");
                    client.sendMessage(jsnCardWrapper);
                }
            }else {
                jsnCard.put("color", card.getColor());
                jsnCard.put("value",card.getValue());
                jsnCardWrapper.put("card",jsnCard);
                jsnCardWrapper.put("module","juno");
                client.sendMessage(jsnCardWrapper);
            }
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
    // TODO: 4/24/2017 Move JSON to Client
    void updateWhoIs(JSONObject jsnWhois){
        JSONArray jsnUsers = jsnWhois.getJSONObject("message").getJSONArray("users");
        System.out.println(jsnWhois);
        List<JSONObject> userList = new ArrayList<>();
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
            }
            users.add(userModules);
        }
        users.updateUI();

        JSONArray jsnModules = jsnWhois.getJSONObject("message").getJSONArray("modules");
        List<JSONObject> moduleList = new ArrayList<>();
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

    void removeCard(Card card, String user){
        gamePanel.getHand(user).removeCard(card);
    }

    void removeCard(String user){
        Card card = new Card(gamePanel.getLocation(user));
        gamePanel.getHand(user).removeCard(card);
    }

    void resetGameGUI() {
        remove(gamePanel);
        gamePanel = buildGame();
        add(gamePanel,BorderLayout.EAST);
        playGame.setVisible(true);
        callUno.setVisible(false);
        quitGame.setVisible(false);
        gamePanel.updateUI();
    }

    void displayError(String message, String title) {
        JOptionPane.showMessageDialog(null,message,title,JOptionPane.ERROR_MESSAGE);
    }

    void turnNotify(String user) {
        if (!(currentTurn == null)){
            gamePanel.getHand(currentTurn).resetBackground();
        }
        gamePanel.getHand(user).setBackground(new Color(148,238,160));
        currentTurn = user;
        gamePanel.updateUI();
    }

    void winNotify(String user) {
        JOptionPane.showMessageDialog(null,user + " wins!","Winner winner chicken dinner!",JOptionPane.INFORMATION_MESSAGE);
    }

    void unoNotify(String user) {
        if(gamePanel.getHand(user).getCardCount() == 1){
            gamePanel.getHand(user).setBackground(new Color(255,153,153));
        }
    }
}
