import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;

/**
 * Created by Nate on 4/14/2017.
 */
public class ClientGUI extends JFrame{
    private Client client;

    private JTextArea messages;
    private JTextArea sendTxt;
    private JScrollPane messageScrollPane;
    public ClientGUI(Client cli){
        client = cli;
        createWindow(buildChat(),buildMenu());
    }

    private void createWindow(JPanel chat, JPanel menu) {
        setLayout(new GridLayout(1,2));
        setSize(new Dimension(1000,400));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        add(menu);
        add(chat);
    }

    private JPanel buildMenu() {
        JPanel menuPanel = new JPanel();
        JButton playGame = new JButton();

        return menuPanel;
    }

    private JPanel buildChat(){
        JPanel chatPanel = new JPanel();
        //chatPanel.setLayout(new GridLayout(2,1));
        //chatPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
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

    protected void newMessage(String user,String msg){
        messages.append(user + ": " + msg + "\r\n");
        JScrollBar bar = messageScrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }
}
