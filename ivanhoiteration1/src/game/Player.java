package game;

import java.util.ArrayList;

public class Player {
	
	private String name;
	private String startTokenColour;
	private ArrayList<Card> cards;
	private ArrayList<Card> display;
	private int totalCardValue;
	
	public String chooseRoundColour(String colour) {
		return colour;
	}
	
	public ArrayList<String> getColourPossibilities() {
		//TO DO: determine which colours the player can choose based on the available cards in their deck;
		ArrayList<String> colours = new ArrayList<>();
		return colours;
	}
	
	public ArrayList<Card> getPlayPossibilities() {
		//TO DO: determine what cards can be played from hand based on tournament colour
		ArrayList<Card> playableCards = new ArrayList<>();
		return playableCards;
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTokenColour() {
		return startTokenColour;
	}
	public void setStartTokenColour(String startTokenColour) {
		this.startTokenColour = startTokenColour;
	}
	public ArrayList<Card> getCards() {
		return cards;
	}
	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public ArrayList<Card> getDisplay() {
		return display;
	}

	public void setDisplay(ArrayList<Card> display) {
		this.display = display;
	}

	public int getTotalCardValue() {
		return totalCardValue;
	}

	public void setTotalCardValue(int totalCardValue) {
		this.totalCardValue = totalCardValue;
	}
	

}
