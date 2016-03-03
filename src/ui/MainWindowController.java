package ui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

import config.Config;
import config.Observer;
import config.Subject;
import game.Card;

public class MainWindowController implements Observer, Subject{
	private int totalPlayers;
	private int playerNum;
	private int tournamentColour;
	private int currPlayer;
	
	public int moved;
	public MainWindow window;
	public String lastMessege;
	public ArrayList<String>tokenStrings;
	public Color backgroundColours[] = {new Color(128,156,229),new Color(255,0,40),new Color(255,223,0), new Color(81,186,91), new Color(161,89,188)};
	public Card lastCard;
	public Logger log = Logger.getLogger("UI");
	
	private ArrayList<Card> playerCards;
	private ArrayList<ArrayList<Card>> playedCards;
	private ArrayList<Integer>playerScores;
	private ArrayList<String>playerNames;
	private ArrayList<Observer>observers = new ArrayList<Observer>();
	
	public MainWindowController(){
		playerCards = new ArrayList<Card>();
		playerCards.clear();
		window = new MainWindow();
		window.registerObserver(this);
		window.testing = false;
		playedCards = new ArrayList<ArrayList<Card>>();
		playerNames = new ArrayList<String>();
		playerScores = new ArrayList<Integer>();
		moved = 0;
		tokenStrings = new ArrayList<String>();
		tokenStrings.add("resources/icons/blue_full.png");
		tokenStrings.add("resources/icons/red_full.png");
		tokenStrings.add("resources/icons/yellow_full.png");
		tokenStrings.add("resources/icons/green_full.png");
		tokenStrings.add("resources/icons/purple_full.png");
	}
	
	public void showWindow(){window.setVisible(true);}
	
	/* GETTERS */
	public String getNameFromPlayer(){return JOptionPane.showInputDialog("Enter your name");}
	public Object getScore(int player) {return this.playerScores.get(player);}
	public Object getName(int player) {return this.playerNames.get(player);}
	public int numCards(){return playerCards.size();}
	public int getPlayerCardSize(){return this.playerCards.size();}
	public int getPlayerNum() {return playerNum;}
	public int getTotalPlayers() {return totalPlayers;}
	public Card getPlayedCard(int player, int index) {return this.playedCards.get(player).get(index);}
	public int getCurrPlayer() {return currPlayer;}
	
	/* SETTERS */
	public void setPlayerTurn(int i){this.currPlayer = i;}
	public void setPlayerNum(int player) {playerNum = player;}
	public void setTotalPlayers(int totalPlayers) {this.totalPlayers = totalPlayers;}
	public void setScore(int player, int score) {
		this.playerScores.set(player, score);	
		window.playerPoints[player].setText(""+score);
	}
	
	public void setName(int player, String name) {
		this.playerNames.set(player, name);
		window.playerNames[player].setText(name);
	}
	
	public int setTournament(){
		String[] options = new String[] {"Blue", "Red", "Yellow", "Green","Purple"};
	    int response = JOptionPane.showOptionDialog(null, "Pick a tournament colour", "New Round",JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
		log.info("Tournament colour has been set to " + response);
	    return response;
	}
	
	public void setTournamnetColour(int i) {
		this.tournamentColour = i;
		this.window.getContentPane().setBackground(this.backgroundColours[i]);
	}
	
	public void setCurrPlayer(int currPlayer) {
		this.currPlayer = currPlayer;
		if(currPlayer==this.playerNum){
			window.startTurn();
		}
	}
	
	public void setNumPlayers(int i){
		this.setTotalPlayers(i);
		for(int j = 0; j < i; j++){
			this.playedCards.add(new ArrayList<Card>());
			this.playerNames.add("");
			this.playerScores.add(0);
		}
		log.info("Total number of players is: " + i);
	}
	
	/* OBSERVER PATTERN */
	@Override
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}
	
	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}
	
	@Override
	public void notifyObservers(String message) {
		for (Observer ob : observers){
			ob.update(message);
		}
	}
	
	@Override
	public void update(String message) {
		lastMessege = message;
		switch(message){
			case "leftclick": leftClick();
			break;
			case "rightclick": rightClick();
			break;
			case "withdrawclick": withdrawClick();
			break;
			case "endturnclick": endturnClick();
			break;
			case "playedcard":playCard();
			break;
			case "viewdisplay":displayCards();
			break;
		}
		log.info("Updated Subjects");
	}

	public void addCard(Card newCard){
		playerCards.add(newCard);
		if(playerCards.size()<11){
			this.window.addPlayerCard(playerCards.size()-1, newCard.getCardImage());
		}
		log.info("Adding new card");
	}
	
	public void removeCard(int i){
		if(playerCards.size()>10){
			if(moved != 0){moved--;}
		}
		
		System.out.println(i);
		playerCards.remove(i);
		this.resetCards();
		log.info("Card removed");
	}
	
	public void addPlayedCard(int player, Card card){
		this.playedCards.get(player).add(card);
		this.window.addPlayedCard(player, card.getCardImage());
		log.info("Player "+ this.playedCards.get(player) + " added " + card + " to their hand");
	}
	
	public void displayCards() {
		CardDisplayPopUp popup = new CardDisplayPopUp(this.playedCards.get(this.window.playedCard));
		popup.setVisible(true);
		log.info("Cards are being displayed");
	}
	
	public void endturnClick() {
		System.out.println("endTurn click");
		this.window.endTurnClicked();
		this.update(Config.END_TURN);
		log.info("Player has ended their turn");
	}

	public void withdrawClick() {
		System.out.println("withdraw click");
		this.window.withdrawClicked();
		this.update(Config.DROP_OUT);
		log.info("Player has withdrawed");
	}

	public void rightClick() {
		System.out.println("right click");
		if(moved < playerCards.size()-10){
			moved++;
			this.window.rightArrowClicked(playerCards.get(moved+9).getCardImage());
		}
	}

	public void leftClick(){
		System.out.println("left click");
		if(moved != 0){
			moved--;
			this.window.leftArrowClicked(playerCards.get(moved).getCardImage());
		}
	}
	
	public void startRound() {
		for(int i = 0; i < this.playerNum; i++){
			setScore(i,0);
			this.window.addPlayedCard(i, "resources/cards_small/simpleCards18.jpg");
			this.playedCards.get(i).clear();
			log.info("Player " + this.playedCards.get(i) + "started their round");
		}	
		this.window.endedTurn();
	}
	
	public void playCard() {
		System.out.println("played card "+this.window.lastCard+"");
		log.info("Played card " + this.window.lastCard+"");
		if(this.window.lastCard<this.playerCards.size()){
			this.lastCard= this.playerCards.get(this.window.lastCard+this.moved);
			this.notifyObservers(Config.PLAYEDCARD);
			this.removeCard(this.window.lastCard+this.moved);
		}
		//signal to the client
	}
	
	public void resetCards(){		
		int maxx = Math.min(10, playerCards.size());
		int i;
		for(i = 0; i < maxx; i++){
			this.window.addPlayerCard(i, playerCards.get(i+moved).getCardImage());
		}
		
		for(i = i; i < 10; i++){
			this.window.playerCards[i].setIcon(null);
		}
		log.info("Cards have been reset");
	}
	
	public void addToken(int player, int token){
		this.window.setToken(player, token, tokenStrings.get(token));
		log.info("Adding " + tokenStrings.get(token) + " token");
	}
}
