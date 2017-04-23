

import javax.swing.*;
import java.awt.*;

public class Card extends JButton {
	private static final long serialVersionUID = 1700177456984817126L;

	/**
	 * 
	 */
	static enum Color { RED, BLUE, GREEN, YELLOW, WILD };
	static enum Value { ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, SKIP, DRAW2, REVERSE, WILD, WILDD4 	};
	private Color color;
	private Value value;

	Card(String location){
		ImageIcon cardImage;
		switch (location) {
			case BorderLayout.EAST:
				cardImage = new ImageIcon(getClass().getResource("back-east.png"));
				break;
			case BorderLayout.WEST:
				cardImage = new ImageIcon(getClass().getResource("back-west.png"));
				break;
			case BorderLayout.CENTER:
				cardImage = new ImageIcon(getClass().getResource("back.png"));
				break;
			default:
				cardImage = new ImageIcon(getClass().getResource("back.png"));
				break;
		}
		setIcon(cardImage);
		setBorder(BorderFactory.createEmptyBorder());
	}

	Card(Color color, Value value) {
		setColor(color);
		setValue(value);
		setImage();
		setBorder(BorderFactory.createEmptyBorder());
	}

	private void setImage() {
		ImageIcon cardImage = new ImageIcon(getClass().getResource(getColor() + "-" + getValue() + ".png"));
		setIcon(cardImage);
	}

	Color getColor() {
		return color;
	}

	private void setColor(Color color) {
		this.color = color;
	}

	Value getValue() {
		return value;
	}

	private void setValue(Value value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Card card = (Card) o;

		if (color != card.color) return false;
		return value == card.value;

	}

	@Override
	public int hashCode() {
		int result = color != null ? color.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
