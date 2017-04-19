import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by Nate on 4/18/2017.
 */
public class Game extends JPanel {
    private HashMap<String,Component> components;
    private HashMap<String,String> locations;
    private boolean north;
    private boolean east;
    private boolean west;
    void add(Hand comp, String user) {
        if(!north) {
            north = true;
            locations.put(user,BorderLayout.NORTH);
            super.add(comp, BorderLayout.NORTH);
        } else if(!east) {
            east = true;
            locations.put(user,BorderLayout.EAST);
            comp.setLayout(new BoxLayout(comp,BoxLayout.Y_AXIS));
            super.add(comp, BorderLayout.EAST);
        } else if(!west) {
            west = true;
            locations.put(user,BorderLayout.WEST);
            comp.setLayout(new BoxLayout(comp,BoxLayout.Y_AXIS));
            super.add(comp, BorderLayout.WEST);
        }
        components.put(user,comp);
    }
    public void add(Component comp, Object constraints) {
        super.add(comp,constraints);
    }

    Game(){
        components = new HashMap<String,Component>();
        locations = new HashMap<String,String>();
        north = false;
        east = false;
        west = false;
    }
    Hand getHand(String userName){
        return (Hand)components.get(userName);
    }

    String getLocation(String user){
        return locations.get(user);
    }

    boolean hasComponent(String userName){
        return components.containsKey(userName);
    }


}
