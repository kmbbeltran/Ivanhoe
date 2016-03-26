package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import config.Config;

public class GameEngine {
	private int numPlayers;
	private String tournamentColour;
	private ArrayList<Player> players;
	private ArrayList<String> tokens;
	private ArrayList<Card> drawDeck;
	private ArrayList<Card> discardPile;
	private Player currentPlayer;
	private int turnNumber = 0;
	private boolean choosePurple = false;
	
	
	/* Testing variables */
	private boolean startTournament = false; 
	private boolean joined = false;
	private String supporters = "supporters";
	private boolean token = false; 
	private String round = "round";
	private boolean testCurrPlayer = false; 
	
	public boolean getStart(){return startTournament;}
	public boolean getJoined(){return joined;}
	public String getSupporters(){return supporters;}
	public boolean getToken(){return token;}
	public String getRound(){return round;}
	public boolean checkCurrPlayer(){return testCurrPlayer;}
	
	
	public GameEngine() {
		players = new ArrayList<>();
		discardPile = new ArrayList<>();
	}
	
	public String processInput(String input) {
		String output = "input";
			// input = start <number of players>
			if (input.contains(Config.START)) {
				output = processStart(input); // output = prompt join OR output = max 5 (if number of players is too high)
			// input = join <player name>
			} else if (input.contains(Config.JOIN)) {
				output = processJoin(input); // output = need players OR output = hand name <player name> cards <type_value> <type_value> ...

			}else if (input.contains(Config.DUPLICATE)){
				output = Config.DUPLICATE;
				
			// input = begin tournament	
			} else if (input.contains(Config.START_TOURNAMENT)) {	
				output = processStartTournament(); // output = purple <player name> turn <player name> (first turn) <card picked up> 
														// OR output = turn <player name> <card picked up> (subsequent turns)
			// input = colour <colour>	
			} else if (input.contains(Config.COLOUR_PICKED)) {
				output = processColourPicked(input); // output = play <colour picked>
			// input = play red 4 (can be continued on input at a time for as many cards as available)
			//OR IF action card, input = play action param_1 ... param_n depending on action
			// talked to Kelly about this. Can get popups where necessary and append variables necessary for action cards to card play string
			} else if (input.contains(Config.PLAY)) {
				output = processPlay(input); // output = waiting <card played> OR output = waiting <unplayable>
											// KATIE TO DO: If output = stunned <card played> then send me end turn
			// input = end turn
			} else if (input.contains(Config.END_TURN)) {
				output = processEndTurn(); // output = <player name> points <player points> [continue OR withdraw] <next player> <card picked up>
												// IF tournament is won, add: <colour> winner <winner name> 
												// OR IF tournament is won and tournamentColour is purple, add: purple win <winner name>  
												// IF game is won, add: game winner <winner name>
												
			// input = purple_win <colour>
			} else if (input.contains(Config.PURPLE_WIN)) {
				output = processPurpleWin(input); // output = same as a normal tournament win of any colour
			// input = withdraw
			} else if (input.contains(Config.WITHDRAW)) {
				withdraw();				
				output = processEndTurn();	// see above: only change is that the player has chosen to withdraw instead of being forced
			}
		return output;
	}
	
	public String processStart(String input) {
		String output;

		String[] start = input.split(" ");
		numPlayers = Integer.valueOf(start[1]);
		if (numPlayers > Config.MAX_PLAYERS) {
			output = Config.MAX;
		} else {
			output = Config.PROMPT_JOIN;
		}
		return output;
	}
	
	public String processJoin(String input) {
		String output = "";
		String name = input.replace("join ", "");
		Player player = new Player(name);
		joinGame(player);
		if (players.size() < numPlayers) 
			output = Config.NEED_PLAYERS;
		else if (players.size() == numPlayers) {
			//prompt first player to start their turn
			//pick tokens happens automatically 
			startGame();
			output += Config.HAND + " ";
			for (Player p: players) {
				output += " " + Config.PLAYER_NAME + " " + p.getName() + " " + Config.PLAYER_CARDS; 
				for (Card c: p.getCards()) {
					output += " " + c.getType() + "_" + c.getValue();
				}
			}					
		}
		return output;
	}
	
	public String processStartTournament() {
		String output = "";
		Card picked = pickupCard();
		String purple;
		int nonAction = 0;
		for (Card c: currentPlayer.getCards()) {
			if (!c.getCardType().equals(Config.ACTION)) {
				nonAction ++;
			}
		}
		if (nonAction == 0) {
			currentPlayer = getNext();
		}
		for (Player p: players) {
			if (p.getStartTokenColour() == Config.PURPLE) {
				purple = p.getName();
				output = Config.PICKED_PURPLE + " " + purple + " " 
						+ Config.TURN + " " + currentPlayer.getName() 
						+ " " + picked.getType() + "_" + picked.getValue();
			} else {
				output = Config.TURN + " " + currentPlayer.getName()
				+ " " + picked.getType() + "_" + picked.getValue();
			}
		}
		startTournament = true;
		return output;
	}
	
	public String processColourPicked(String input) {
		String output;
		String[] pick = input.split(" ");
		String colour = pick[1];
		currentPlayer.chooseTournamentColour(colour);
		startTurn();
		output = Config.COLOUR + " " + colour;
		return output;
	}
	
	public String processPlay(String input) {
		String output = Config.WAITING;
		String[] play = input.split(" ");
		String type = play[1];
		String value = "0";
		if (play.length > 2) {
			value = play[2];
		}
		Card card = null;
		boolean hasMaiden = false;
		for (Card c: currentPlayer.getCards()) {
			if (type.equals(c.getType()) && value.equals(Integer.toString(c.getValue()))
					|| (type.equals(c.getType()) && c.getCardType().equals(Config.ACTION))) {
				card = c;
			}
		}
		if (card.getType().equals(tournamentColour) 
				|| card.getCardType().equals(Config.SUPPORT)) {
			if (tournamentColour.equals(Config.GREEN) && card.getValue() > 1) {
				card.setValue(1);
			}
			
			if (card.getType().equals(Config.MAIDEN)) {
					for (Card c: currentPlayer.getDisplay()) {
						if (c.getType().equals(Config.MAIDEN)) {
							hasMaiden = true;
							output += " " + Config.UNPLAYABLE;
						}
					}
				}
			if (!hasMaiden) {
				playCard(card);
				for (Card c: currentPlayer.getFront()) {
					if (c.getType().equals(Config.STUNNED)) {
						output = Config.STUNNED;
					}
				}
				output += " " + type + "_" + value;
			}
		} else if (card.getCardType().equals(Config.ACTION)) {
			output += processActionCard((ActionCard) card, input);
		} else {
			output += " " + Config.UNPLAYABLE;
		}
		
		output.replace("  ", " ");
		output.trim();
		return output; 
	}
	
	public String processActionCard(ActionCard card, String input) {
		String output = " ";
		String[] cardString = input.split(" ");
		if (card.getType().equals(Config.UNHORSE)) {
			//input = play unhorse <colour>
			String colour = cardString[2];
			if (tournamentColour.equals(Config.PURPLE)) { 
				card.playUnhorse(this, colour);		
				output += Config.UNHORSE + " " + colour; //output = waiting <card played> <colour chosen>
			} else {
				output += Config.UNPLAYABLE;
			}
		} else if (card.getType().equals(Config.CHANGEWEAPON)) {
				//input = play changeweapon <colour>
				String colour = cardString[2];
				if (tournamentColour.equals(Config.RED) 
						|| tournamentColour.equals(Config.BLUE) 
						|| tournamentColour.equals(Config.YELLOW)) {
					card.playChangeWeapon(this, colour);
				}
				output += Config.CHANGEWEAPON + " " + colour; //output = waiting <card played> <colour chosen>
			} else if (card.getType().equals(Config.DROPWEAPON)) {
				//input = play dropweapon
				if (tournamentColour.equals(Config.RED) 
						|| tournamentColour.equals(Config.BLUE) 
						|| tournamentColour.equals(Config.YELLOW)) {
					card.playDropWeapon(this);
				}
				output += Config.DROPWEAPON + " " + Config.GREEN; //output = waiting <card played> green
			} else if (card.getType().equals(Config.BREAKLANCE)) {
				//input = play breaklance <player name> 
				String playerName = cardString[2];
				Player player = getPlayerByName(playerName);
				if (player.getDisplay().size() < 2) {
					output += Config.UNPLAYABLE;
				} else {
					card.playBreakLance(player);
					output += Config.BREAKLANCE + " ";
					output += Config.DISPLAY + " ";
					output += Config.PLAYER_NAME + " " + playerName + " " + player.getTotalCardValue() + " " + Config.PLAYER_CARDS + " ";
	
						for (Card c: player.getDisplay()) {
							output += c.getType() + " " + c.getValue(); 
						}
				}

				//output = waiting <card played> display name <player> <player score> cards <display card> <display card> ...
			} else if (card.getType().equals(Config.RIPOSTE)) {
				//input = play riposte <player name>
				String playerName = cardString[2];
				Player player = getPlayerByName(playerName);
				if (player.getDisplay().size() < 2) {
					output += Config.UNPLAYABLE;
				} else {
					Card cardToSteal = card.playRiposte(player);
					if (cardToSteal != null) {
						currentPlayer.addToDisplay(cardToSteal);
						currentPlayer.setTotalCardValue();
					}
	
					
						output += Config.RIPOSTE + " " + playerName + " " + player.getTotalCardValue() + " "
						+ cardToSteal.getType() + " " + cardToSteal.getValue()  + " " 
								+ currentPlayer.getName()  + " " + currentPlayer.getTotalCardValue();
				}
				//output = waiting <card played> <player stolen from> <player total> <card stolen> <player added to> <player value>
			} else if (card.getType().equals(Config.DODGE)) {
				// input = play dodge <player name> <card type> <card value>

				String playerName = cardString[2];
				String type = cardString[3];
				String value = cardString[4];
				Player player = getPlayerByName(playerName);
				if (player.getDisplay().size() < 2) {
					output += Config.UNPLAYABLE;
				} else {
				for (Card c: player.getDisplay()) {
					if (c.getType().equals(type) && Integer.toString(c.getValue()).equals(value)) {
						card.playDodge(player, c);
						player.setTotalCardValue();
						break;
					}
				}
					output += Config.DODGE + " " + playerName + " " + player.getTotalCardValue() + " " + type + " " + value;
				}
				//output = waiting <card played> <player discarded from> <score> <card discarded> 
			} else if (card.getType().equals(Config.RETREAT)) {
				// input = play retreat <card type> <card value>
				String type = cardString[2];
				String value = cardString[3];
				Card cardToRetreat = null;
				for (Card c: currentPlayer.getDisplay()) {
					if (c.getType().equals(type) && Integer.toString(c.getValue()).equals(value)) {
						cardToRetreat = c;
						break;
					}
				}
				if (currentPlayer.getDisplay().size() < 2) {
					output += Config.UNPLAYABLE;
				} else {
					card.playRetreat(this, cardToRetreat);
					output += Config.RETREAT + " " + currentPlayer.getName() + " " + currentPlayer.getTotalCardValue() + " " + type + " " + value;
				}
				//output = waiting <card played> <currentPlayerName> <score> <card removed from display and put back into hand>
			} else if (card.getType().equals(Config.KNOCKDOWN)) {
				// input = play knockdown <player name>
				String playerName = cardString[2];
				Player player = getPlayerByName(playerName);
				Card cardToSteal = card.playKnockDown(this, player);

				output += Config.KNOCKDOWN + " " + playerName + " " + cardToSteal.getType() + " " + cardToSteal.getValue();
				//output = waiting <card played> <player chosen> (Just remove the first card from that player's hand)
			} else if (card.getType().equals(Config.OUTMANEUVER)) {
				// input = play outmaneuver
				card.playOutmaneuver(this);
				output += Config.OUTMANEUVER + " " + updateDisplays();
				//output = waiting <card played> <current player name> (remove the last card from all other displays that don't have a shield card)
			} else if (card.getType().equals(Config.CHARGE)) {
				// input = play charge
				card.playCharge(this);
				output += Config.CHARGE + " " + updateDisplays();
				//output = waiting <card played> name <opponent 1 name> cards <display card 1> <display card 2> <opponenent 2 name> <display card 1>... for all opponents
			} else if (card.getType().equals(Config.COUNTERCHARGE)) {
				// input = play countercharge
				card.playCounterCharge(this);
				output += Config.COUNTERCHARGE + " " + updateDisplays();
				//output = waiting <card played> name <opponent 1 name> cards <display card 1> <display card 2> <opponenent 2 name> <display card 1>... for all opponents
			} else if (card.getType().equals(Config.DISGRACE)) {
				//input = play disgrace
				card.playDisgrace(this);
				output += Config.DISGRACE + " "  + updateDisplays();
				//output = waiting <card played> <current player name> (can you remove all support cards from everyone but the current player here?) 
			} else if (card.getType().equals(Config.ADAPT)) {
				card.playAdapt(this);
				output +=  Config.ADAPT + " " + updateDisplays();
				//output = waiting <card played> name <opponent 1 name> cards <display card 1> <display card 2> <opponenent 2 name> <display card 1>... for all opponents
			} else if (card.getType().equals(Config.OUTWIT)) {
				//TO DO
			} else if (card.getType().equals(Config.SHIELD)) {
				card.playShield(this, card);
				output += Config.SHIELD + " " + currentPlayer.getName();

			} else if (card.getType().equals(Config.STUNNED)) {
				String playerName = cardString[2];
				Player player = getPlayerByName(playerName);
				card.playStunned(player, card);
				output += Config.STUNNED + " " + playerName;
			} else if (card.getType().equals(Config.IVANHOE)) {
				//TO DO
			}
		
		return output;
	}
	
	public String updateDisplays() {
		String output = Config.DISPLAY + " ";
		for (Player p: players) {
			p.setTotalCardValue();
			output += Config.PLAYER_NAME + " " + p.getName() + " " + p.getTotalCardValue() + " " + Config.PLAYER_CARDS + " ";
			//if (!p.getName().equals(currentPlayer.getName())) {
				for (Card c: p.getDisplay()) {
					output += c.getType() + "_" + c.getValue() + " ";
				//}
			}
		}
		return output;
		//output += <player name> <score> cards <display card 1> <display card 2> <opponenent 2 name> <display card 1>... for all opponents
	}

	//TO DO: Change back to ArrayList if this does not work
	public ArrayList<Player> getActionablePlayers() {
		ArrayList<Player> actionable = new ArrayList<>();
		for (Player p: players) {
			actionable.add(p);
		}
		for (Player p: actionable) {
			for (Card c: p.getFront()) {
				if (c.getType().equals(Config.SHIELD)) {
					actionable.remove(p);
				}
			}
		}
		return actionable;
	}
 	public String processPurpleWin(String input) {
		String output;
		String[] purpleWin = input.split(" ");
		String chosenColour = purpleWin[2];
		if (chosenColour.equals(Config.PURPLE)) {
			choosePurple = true;
		}
		setTournamentColour(chosenColour);
		output = processEndTurn();
		return output;
	}
	
	public String processEndTurn() {
		String output = currentPlayer.getName() + " " + Config.POINTS + " " + currentPlayer.getTotalCardValue();
		Player prevPlayer = currentPlayer;
		endTurn();
		String withdraw = Config.CONTINUE;
		if (prevPlayer.hasWithdrawn()) {
			withdraw = Config.WITHDRAW;
		}
		output += " " + withdraw + " " + currentPlayer.getName();
		startTurn();
		String status = null;
		for (Player p: players) {
			if (p.isWinner() && tournamentColour.equals(Config.PURPLE) && !choosePurple) {
				status = " " + Config.PURPLE_WIN + " " + p.getName();
				currentPlayer = p;
				p.resetTotalCardValue();
				
			}
			else if (p.isWinner() && (!tournamentColour.equals(Config.PURPLE) || choosePurple)) {
				currentPlayer = p;
				announceWinner();
				arrangePlayers();
				resetPlayers();
				status = " " + getTournamentColour() + " " + Config.TOURNAMENT_WINNER + " " + p.getName();
				currentPlayer = p;
			}
			if (p.isGameWinner()) {
				output = " " + Config.GAME_WINNER + " " + p.getName();
				currentPlayer = p;
			}
			
		}
		if (status == null){
			Card picked = pickupCard();
			status = " " + picked.getType() + "_" + picked.getValue();
		}
		
		output += status;
		return output;
	}
	
	public void joinGame(Player player) {
		players.add(player);
		joined = true;
	}

	public void pickTokens() {
		//add all tokens to token array, starting with purple
		tokens = new ArrayList<>();
		tokens.add(Config.PURPLE);
		tokens.add(Config.RED);
		tokens.add(Config.BLUE);
		tokens.add(Config.YELLOW);
		tokens.add(Config.GREEN);
		
		//remove last token in tokens array as long as the number of tokens is greater than the number of players
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.size() > players.size()) {
				tokens.remove(tokens.size()-1);
			}
		}
		
		//pick a random token for each player, and remove that token from the array
		Random random = new Random();
		for (Player p: players) {
			int index = random.nextInt(tokens.size());
			String token = tokens.get(index);
			p.setStartTokenColour(token);
			tokens.remove(index);
		}	
	}
	
	public void startGame() {
		//randomly pick a token for each player
		pickTokens();
		//arange players according to picked token
		arrangePlayers();
		//create the drawDeck
		createDeck();
		
		//shuffle drawDeck (twice to be safe)
		Collections.shuffle(drawDeck);
		Collections.shuffle(drawDeck);

		//for each player, create a hand, add the first 8 cards to the hand and remove them from the drawDeck
		for (Player p: players) {
			ArrayList<Card> hand = new ArrayList<>();
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);			
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);
			hand.add(drawDeck.get(0));
			drawDeck.remove(0);

			//set the current player's hand to the one created
			p.setCards(hand);
		}
	}
	
	public void arrangePlayers() {
				ArrayList<Player> tempPlayers = new ArrayList<Player>();
				for (Player p: players) {
					tempPlayers.add(p);
				}
				for (int i = 0; i < numPlayers; i++) {
					if ((i == 0 && tempPlayers.get(tempPlayers.size() - 1).getStartTokenColour().equals(Config.PURPLE)) ||
							(i != 0 && (tempPlayers.get(i-1).getStartTokenColour().equals(Config.PURPLE)))
							|| (tempPlayers.get(i).isWinner())) {
						players.set(0, tempPlayers.get(i));
						int firstPlayer = i;
						for (int j = 1; j < numPlayers; j++) {
							i++;
							if (i < tempPlayers.size()) {
								players.set(j, tempPlayers.get(i));
							} else {
								for (int k = 0; k < firstPlayer; k++) {
									players.set(j, tempPlayers.get(k));
								}
							}
						}
					}
				}
				currentPlayer = players.get(0);
				testCurrPlayer = true;
	}
	
	public void startTurn() {
		turnNumber ++;
		if (turnNumber == 1) {
			tournamentColour = currentPlayer.getTournamentColour();
		} else if (currentPlayer.getPlayPossibilities(this).isEmpty()) {
			withdraw();
		} 
		int playersLeft = 0;
		for (Player p: players) {
			if (!p.hasWithdrawn())
				playersLeft ++;
		}
		if (playersLeft == 1) {
			announceWinner();
		}
	}
	
	public void discard(Card card) {
		discardPile.add(card);
	}
	
	
	public void playCard(Card card) {
		// play a specific card for current player, handle based on card rules
		if (card.getCardType().equals(Config.COLOUR) || card.getCardType().equals(Config.SUPPORT)) {
			currentPlayer.addToDisplay(card);
		} 
		//remove card from player hand
		currentPlayer.removeCard(card);
		//add card to where it should be added (display, front, discard)
		currentPlayer.setTotalCardValue();
	}
	
	public Card pickupCard() {
		//current player picks up top card
		if (drawDeck.isEmpty()) {
			drawDeck = discardPile;
			Collections.shuffle(drawDeck);
			Collections.shuffle(drawDeck);
			discardPile = new ArrayList<Card>();
		}
		Card picked = drawDeck.get(0);
		currentPlayer.addCard(picked);
		drawDeck.remove(0);
		return picked;
	}
	
	
	public void removeCardfromDeck(Card card) {
		//remove a single card from draw deck (mostly for testing)
		for (int i = 0; i < drawDeck.size(); i++) {
			if (card.getType().equals(drawDeck.get(i).getType()) && (card.getValue() == drawDeck.get(i).getValue())) {
				drawDeck.remove(i);
				break;
			}
		}	}
	
	public void removeAllFromDeck(ArrayList<Card> cards) {
		// remove a number of cards from the draw deck (mostly for testing after cards are dealt)
		for (Card c: cards) {
			for (int i = 0; i < drawDeck.size(); i++) {
				if (c.getType().equals(drawDeck.get(i).getType()) && (c.getValue() == drawDeck.get(i).getValue())) {
					drawDeck.remove(i);
					break;
				}
			}
		}
	}
	
	public void addAllToDeck(ArrayList<Card> cards) {
		//add a number of cards to the draw deck (mostly for testing after cards are dealt)
		for (Card c: cards) {
				drawDeck.add(c);
		}
	}
	
	public void endTurn() {
		for (Player p: players) {
			if ((currentPlayer.getTotalCardValue() <= p.getTotalCardValue()) 
					&& (!currentPlayer.getName().equals(p.getName()))) {
				withdraw();
			}
		}
		currentPlayer = getNext();
	}
	
	private Player getNext() {
		int index = 0;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == currentPlayer) {
				if (i == players.size()-1) {
					index = 0;
				} else {
					index = i + 1;
				}
			}
		}
		return players.get(index);
	}

	public void withdraw() {
		currentPlayer.setWithdrawn(true);
		if (currentPlayer.getCards().size() != 0) {
			for (Card c: currentPlayer.getCards()) {
				discardPile.add(c);
				currentPlayer.removeFromDisplay(c);
			}
			currentPlayer.setDisplay(new ArrayList<Card>());
		}
	}
	
	public void announceWinner() {
		Player winner = currentPlayer;
		int points = players.get(0).getTotalCardValue();
		for (Player p: players) {
			if (p.getTotalCardValue() > points) {
				points = p.getTotalCardValue();
			}
		}
		for (Player p: players) {
			if (p.getTotalCardValue() == points) {
				p.setWinner(true);
				winner = p;
			}
		}
		if ((choosePurple && (!currentPlayer.getCurrentTokens().contains(Config.PURPLE))) || (!tournamentColour.equals(Config.PURPLE))
			&& (!currentPlayer.getCurrentTokens().contains(tournamentColour))) {
			currentPlayer.addToken(tournamentColour);
			choosePurple = false;
		}
		if ((numPlayers <= 3) && (winner.getCurrentTokens().size() == 5)) {
			winner.setGameWinner(true);
		} else if ((numPlayers >= 4) && (winner.getCurrentTokens().size() == 4)) {
			winner.setGameWinner(true);
		}

	}
	
	public void resetPlayers() {
		for (Player p: players) {
			p.setWithdrawn(false);
			p.setWinner(false);
			p.setDisplay(new ArrayList<Card>());
			p.setStartTokenColour("nil");
			p.resetTotalCardValue();
		}
		turnNumber = 0;
	}
	
	public Player getPlayerByName(String name) {
		Player player = null;
		for (Player p: players) {
			if (p.getName().equals(name)) {
				player = p;
			}
		}
		return player;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void setCurrentPlayer(Player currentPlayer) {
		//Set currentPlayer to the correct player in order of game rules
		this.currentPlayer = currentPlayer;
	}

	public int getNumPlayers() {
		return numPlayers;
	}
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}
	public String getTournamentColour() {
		return tournamentColour;
	}
	public void setTournamentColour(String tournamentColour) {
		this.tournamentColour = tournamentColour;
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public ArrayList<Card> getDrawDeck() {
		return drawDeck;
	}

	public void setDrawDeck(ArrayList<Card> drawDeck) {
		this.drawDeck = drawDeck;
	}

	public void createDeck() {
		drawDeck = new ArrayList<>();
		//purple
		drawDeck.add(new ColourCard(Config.PURPLE, 3));
		drawDeck.add(new ColourCard(Config.PURPLE, 3));
		/*drawDeck.add(new ColourCard(Config.PURPLE, 3));
		drawDeck.add(new ColourCard(Config.PURPLE, 3));
		drawDeck.add(new ColourCard(Config.PURPLE, 4));
		drawDeck.add(new ColourCard(Config.PURPLE, 4));
		drawDeck.add(new ColourCard(Config.PURPLE, 4));
		drawDeck.add(new ColourCard(Config.PURPLE, 4));
		drawDeck.add(new ColourCard(Config.PURPLE, 5));
		drawDeck.add(new ColourCard(Config.PURPLE, 5));
		drawDeck.add(new ColourCard(Config.PURPLE, 5));
		drawDeck.add(new ColourCard(Config.PURPLE, 5));
		drawDeck.add(new ColourCard(Config.PURPLE, 7));
		drawDeck.add(new ColourCard(Config.PURPLE, 7));
		*/
		//red
		drawDeck.add(new ColourCard(Config.RED, 3));
		drawDeck.add(new ColourCard(Config.RED, 3));
		/*drawDeck.add(new ColourCard(Config.RED, 3));
		drawDeck.add(new ColourCard(Config.RED, 3));
		drawDeck.add(new ColourCard(Config.RED, 3));
		drawDeck.add(new ColourCard(Config.RED, 3));
		drawDeck.add(new ColourCard(Config.RED, 4));
		drawDeck.add(new ColourCard(Config.RED, 4));
		drawDeck.add(new ColourCard(Config.RED, 4));
		drawDeck.add(new ColourCard(Config.RED, 4));
		drawDeck.add(new ColourCard(Config.RED, 4));
		drawDeck.add(new ColourCard(Config.RED, 4));
		drawDeck.add(new ColourCard(Config.RED, 5));
		drawDeck.add(new ColourCard(Config.RED, 5));
*/
		//blue
		drawDeck.add(new ColourCard(Config.BLUE, 2));
		drawDeck.add(new ColourCard(Config.BLUE, 2));
		drawDeck.add(new ColourCard(Config.BLUE, 2));
/*		drawDeck.add(new ColourCard(Config.BLUE, 2));
		drawDeck.add(new ColourCard(Config.BLUE, 3));
		drawDeck.add(new ColourCard(Config.BLUE, 3));
		drawDeck.add(new ColourCard(Config.BLUE, 3));
		drawDeck.add(new ColourCard(Config.BLUE, 3));
		drawDeck.add(new ColourCard(Config.BLUE, 4));
		drawDeck.add(new ColourCard(Config.BLUE, 4));
		drawDeck.add(new ColourCard(Config.BLUE, 4));
		drawDeck.add(new ColourCard(Config.BLUE, 4));
		drawDeck.add(new ColourCard(Config.BLUE, 5));
		drawDeck.add(new ColourCard(Config.BLUE, 5));
*/
		//yellow
		drawDeck.add(new ColourCard(Config.YELLOW, 2));
		drawDeck.add(new ColourCard(Config.YELLOW, 2));
/*		drawDeck.add(new ColourCard(Config.YELLOW, 2));
		drawDeck.add(new ColourCard(Config.YELLOW, 2));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 3));
		drawDeck.add(new ColourCard(Config.YELLOW, 4));
		drawDeck.add(new ColourCard(Config.YELLOW, 4));
*/		
		//green
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
/*		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
		drawDeck.add(new ColourCard(Config.GREEN, 1));
*/
		//supporters
/*		drawDeck.add(new SupportCard(Config.MAIDEN, 6));
		drawDeck.add(new SupportCard(Config.MAIDEN, 6));
		drawDeck.add(new SupportCard(Config.MAIDEN, 6));
		drawDeck.add(new SupportCard(Config.MAIDEN, 6));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 2));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
*/		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));
		drawDeck.add(new SupportCard(Config.SQUIRE, 3));

		//action
		/*drawDeck.add(new ActionCard(Config.UNHORSE));
		drawDeck.add(new ActionCard(Config.CHANGEWEAPON));
		drawDeck.add(new ActionCard(Config.DROPWEAPON));
		
		drawDeck.add(new ActionCard(Config.RIPOSTE));
		drawDeck.add(new ActionCard(Config.RIPOSTE));
		drawDeck.add(new ActionCard(Config.RIPOSTE));
		drawDeck.add(new ActionCard(Config.DODGE));
		drawDeck.add(new ActionCard(Config.RETREAT));
		drawDeck.add(new ActionCard(Config.KNOCKDOWN));
		drawDeck.add(new ActionCard(Config.KNOCKDOWN));*/
		drawDeck.add(new ActionCard(Config.BREAKLANCE));	
		drawDeck.add(new ActionCard(Config.OUTMANEUVER));
		drawDeck.add(new ActionCard(Config.CHARGE));
		drawDeck.add(new ActionCard(Config.COUNTERCHARGE));
		drawDeck.add(new ActionCard(Config.DISGRACE));
		drawDeck.add(new ActionCard(Config.ADAPT));
		//drawDeck.add(new ActionCard(Config.OUTWIT));
		//drawDeck.add(new ActionCard(Config.SHIELD));
		//drawDeck.add(new ActionCard(Config.STUNNED));
		//drawDeck.add(new ActionCard(Config.IVANHOE));
	}


}
