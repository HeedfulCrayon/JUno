import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nate on 4/18/2017.
 */
class Hand extends JPanel {
    private String userName;
    private ArrayList<Card> cards;
    private Color defBackground;

    Hand(String user){
        cards = new ArrayList<Card>();
        userName = user;
        if (!userName.equals("server")) {
            defBackground = this.getBackground();
            TitledBorder titleUser = BorderFactory.createTitledBorder(userName);
            titleUser.setTitleJustification(TitledBorder.CENTER);
            setBorder(titleUser);
        }
        updateUI();
    }

    void resetBackground(){
        this.setBackground(defBackground);
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

    int getCardCount(){
        return cards.size();
    }
}
