import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by Nate on 4/18/2017.
 */
public class Game extends JPanel {
    private HashMap<String,Component> components;
    private HashMap<String,String> locations;
    private boolean center;
    private boolean north;
    private boolean east;
    private boolean west;
    private boolean south;
    private String myUser;
    void add(Component comp, String user) {
        if(!south) {
            south = true;
            myUser = user;
            locations.put(user,BorderLayout.SOUTH);
            super.add(comp, BorderLayout.SOUTH);
        }else if(!north) {
            north = true;
            locations.put(user,BorderLayout.NORTH);
            super.add(comp, BorderLayout.NORTH);
        } else if(!east) {
            east = true;
            Hand hand = (Hand)comp;
            locations.put(user,BorderLayout.EAST);
            hand.setLayout(new BoxLayout(hand,BoxLayout.Y_AXIS));
            super.add(comp, BorderLayout.EAST);
        } else if(!west) {
            west = true;
            Hand hand = (Hand)comp;
            locations.put(user,BorderLayout.WEST);
            hand.setLayout(new BoxLayout(hand,BoxLayout.Y_AXIS));
            super.add(comp, BorderLayout.WEST);
        }
        components.put(user,comp);
    }
    private void add(Hand comp, boolean discard){
        super.add(comp,BorderLayout.CENTER);
        if(discard){
            components.put("discard",comp);
            components.put("draw",comp);
        }

    }
//    public void add(Component comp, Object constraints) {
//        super.add(comp,constraints);
//    }

    Game(){
        components = new HashMap<String,Component>();
        locations = new HashMap<String,String>();
        locations.put("draw",BorderLayout.CENTER);
        locations.put("discard",BorderLayout.CENTER);
        north = false;
        east = false;
        west = false;
    }
    Hand getHand(String userName){
        if (userName.equals(myUser)){
            return (Hand)(((JScrollPane)components.get(userName)).getViewport().getView());
        }else{
            return (Hand)components.get(userName);
        }
    }

    String getLocation(String user){
        return locations.get(user);
    }

    boolean hasComponent(String userName){
        return components.containsKey(userName);
    }

    void start(){
        Hand center = new Hand("server");
        this.add(center,true);
    }


}
