import javax.swing.*;
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
    public ClientGUI(Client cli){
        client = cli;
        BuildChat();
    }

    private void BuildChat(){
        JPanel chatPanel = new JPanel();
        messages = new JTextArea(10,40);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        messages.setText("");
        messages.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messages);
        chatPanel.add(messageScrollPane);

        sendTxt = new JTextArea(4,20);
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
        chatPanel.add(sendScrollPane);
        add(chatPanel);

        JButton send = new JButton("Send");
        send.addActionListener((e) -> {
            client.sendMessage(send());
        });
        chatPanel.add(send);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((e) -> {

            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        chatPanel.add(cancel);

        setSize(new Dimension(500,300));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String send() {
        String message = sendTxt.getText();
        sendTxt.setText("");
        return message;
    }

    protected void newMessage(String user,String msg){
        messages.append(user + ": " + msg + "\r\n");
    }
}
