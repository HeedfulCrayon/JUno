
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -919021369126386690L;

	static enum Color { RED, BLUE, GREEN, YELLOW, WILD};
	static enum Value { ZERO("00"), ONE("01"), TWO("02"), THREE("03"), FOUR("04"), FIVE("05"), SIX("06"), SEVEN("07"), EIGHT("08"), NINE("09"), SKIP("S"), DRAW2("D2"), REVERSE("R"), WILD("00"), WILDD4("D4") ;
		private String numVal;
		Value(String numVal){
			this.numVal = numVal;
		}
		public String getNumVal(){
			return numVal;
		}
	};
	private Color color;
	private Value value;	

	Card(Color color, Value value) {
		setColor(color);
		setValue(value);
		setImage();
	}

	private void setImage() {
		ImageIcon cardImage = new ImageIcon(getClass().getResource(getColor() + "-" + getValue() + ".png"));
		setIcon(cardImage);
	}

	private Color getColor() {
		return color;
	}

	private void setColor(Color color) {
		this.color = color;
	}

	private String getValue() {
		return value.getNumVal();
	}

	private void setValue(Value value) {
		this.value = value;
	}

}
