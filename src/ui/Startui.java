package ui;

import game.ColourCard;

public class Startui {
	public static void main(String[] args) {
		//MainWindow window =new MainWindow();
		MainWindowController control= new MainWindowController();
		control.showWindow();
		control.setNumPlayers(3);
		control.setName(0, control.getNameFromPlayer());
		
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addCard(new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addCard(new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.setScore(0, 5);
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(1, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 4,"resources/cards_small/simpleCards15.jpg"));
		control.addPlayedCard(0, new ColourCard("purple", 3,"resources/cards_small/simpleCards14.jpg"));
		control.addToken(0, 1);
		control.addToken(0, 3);
		control.addToken(0, 4);
		control.addToken(1, 0);
		control.addToken(1, 2);
		control.setTournamnetColour(control.setTournament());
		//System.out.println();
		//window.setVisible(true);
	}
}
