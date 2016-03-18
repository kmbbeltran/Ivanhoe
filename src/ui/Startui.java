package ui;

import config.Config;
import game.Card;
import game.ColourCard;

public class Startui {
	public static void main(String[] args) {
		//MainWindow window =new MainWindow();
		MainWindowController control= new MainWindowController();
		control.showWindow();
		control.setNumPlayers(3);
		//control.setName(0, control.getNameFromPlayer());
		control.setPlayerNum(0);
		Card c1=new ColourCard("purple", 3,Config.IMG_PURPLE_3);
		Card c2= new ColourCard("purple", 4,Config.IMG_PURPLE_4);
		c1.setCardDescription("this is a purple 3");
		c2.setCardDescription("this is a purple 4");
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		control.addCard(c1);
		control.addCard(c2);
		
		control.setScore(0, 5);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(1, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);
		control.addPlayedCard(0, c1);
		control.addPlayedCard(0, c2);

		/*control.addToken(0, 1);
		control.addToken(0, 3);
		control.addToken(0, 4);
		control.addToken(1, 0);
		control.addToken(1, 2);*/
		//control.playerNames.add("kelly");
		//control.playerNames.add("katie");
		//control.playerNames.add("brit");
		control.playerName=control.getNameFromPlayer();
		control.setName(0, control.playerName);
		control.playerNames.add(0, control.playerName);
		//control.removeToken(1, Config.BLUE);
		
		//control.playerWithdraws("jo");
		control.setCurrPlayer(control.getPlayerNum());
		control.window.startTurn();
		//control.startRound();
	
		//testing various pop ups
		//control.setTournamnetColour(control.setTournament());
		//System.out.println(control.playerPickToken() );
		//control.showWaiting();
		//System.out.println(control.getIPPortFromPlayer());
		//control.hideWaitng();
		//System.out.println(control.getNumberOfPlayersFromPlayer());
		//System.out.println(control.changeColour());
		//System.out.println(control.pickAName("testing to pick a name."));
				//System.out.println(
		
	}
}
