import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Nate on 4/18/2017.
 */
class Hand extends JPanel {
    private String userName;
    private ArrayList<Card> cards;
    private JPanel panel;

    Hand(String user){
        cards = new ArrayList<Card>();
        userName = user;
        updateUI();
    }

    public String getUserName() {
        return userName;
    }

    void addCard(Card card){
        super.add(card);
        cards.add(card);
    }

    void removeCard(Card card){
        int index = 0;
        if((card.getValue() == Card.Value.valueOf("WILD")) || (card.getValue() == Card.Value.valueOf("WILDD4"))){
            for (Card cardIter:cards) {
                if(card.getValue().equals(cardIter.getValue())) {
                    super.remove(cards.remove(index));
                    break;
                }
                index++;
            }
        }else {
            for (Card cardIter : cards) {
                if (card.equals(cardIter)) {
                    super.remove(cards.remove(index));
                    break;
                }
                index++;
            }
        }
    }
}
