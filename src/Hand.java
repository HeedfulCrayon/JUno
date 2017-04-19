import javax.swing.*;

/**
 * Created by Nate on 4/18/2017.
 */
class Hand extends JPanel {
    private String userName;

    Hand(String user){
        userName = user;
    }

    public String getUserName() {
        return userName;
    }
}
