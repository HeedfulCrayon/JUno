import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Nate on 4/14/2017.
 */
public class ClientGUI extends JFrame{
    private Client client;

    private JTextArea messages;
    private JTextArea sendTxt;
    private JScrollPane messageScrollPane;
    private JPanel users;
    private JPanel modules;
    List<JLabel> labels;
    ClientGUI(Client cli){
        client = cli;
        createWindow(buildChat(),buildMenu(),buildWhois());
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

    private void createWindow(JPanel chat, JPanel menu, JPanel whoIs) {
        setLayout(new GridLayout(1,3));
        setSize(new Dimension(1500,400));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        add(menu);
        add(chat);
        add(whoIs);
        sendTxt.grabFocus();
    }

    private JPanel buildMenu() {
        JPanel menuPanel = new JPanel();
        JButton playGame = new JButton("Play Game");
        playGame.addActionListener((e -> {
            JSONObject playMsg = new JSONObject();
            playMsg.put("type","application");
            playMsg.put("message","startgame");
            client.sendMessage(playMsg);
        }));
        menuPanel.add(playGame);
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

    private String send() {
        String message = sendTxt.getText();
        sendTxt.setText("");
        return message;
    }

    void newMessage(String user,String msg){
        messages.append(user + ": " + msg + "\r\n");
        JScrollBar bar = messageScrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    void updateWhoIs(JSONObject jsnWhois){
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
            users.add(new JLabel("Modules: " + user.get("modules").toString()));
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
            modules.add(new JLabel(module.get("moduleName").toString()));
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
}
