package junittests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import config.Config;
import game.ActionCard;
import game.Card;
import game.ColourCard;
import game.GameEngine;
import game.Player;
import game.SupportCard;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class Test2PlayerManual {
	static GameEngine game;
	static int testNumber = 0;
	static Player player1;
	static Player player2;
	static ArrayList<Card> player1Cards;
	static ArrayList<Card> player2Cards;
	private Card pickup = new ColourCard(Config.YELLOW, 4);

	@BeforeClass
    public static void BeforeClass() {
        System.out.println("@BeforeClass: Setting up tests for a manually set 2 player round, dealing cards, selecting tokens");
        //These tests set the hands and played cards manually to test specific scenarios
        
		game = new GameEngine();
    	
    	//add 2 players to the game
    	player1 = new Player("Katie");
    	player2 = new Player("Kelly");
    	game.joinGame(player1);
    	game.joinGame(player2);

    	
    	//begin game, deal cards
    	game.startGame(); //will automatically deal cards to players, but we will replace them with specific cards for testing
    	game.addAllToDeck(player1.getCards());
    	game.addAllToDeck(player2.getCards());
    	
    	//select tokens (should have been automatically done, but setting manually for tests)
       	player1.setStartTokenColour(Config.BLUE);
    	player2.setStartTokenColour(Config.PURPLE);
    	game.arrangePlayers();
    	
    	ArrayList<Card> player1Cards = new ArrayList<>();
    	player1Cards.add(new ColourCard(Config.BLUE, 4));
    	player1Cards.add(new ColourCard(Config.PURPLE, 5));
    	player1Cards.add(new ColourCard(Config.YELLOW, 3));
    	player1Cards.add(new ColourCard(Config.RED, 4));
    	player1Cards.add(new ColourCard(Config.YELLOW, 3));
    	player1Cards.add(new ColourCard(Config.YELLOW, 2));
    	player1Cards.add(new ActionCard(Config.RIPOSTE));
    	player1Cards.add(new SupportCard(Config.MAIDEN, 6));
    	player1.setCards(player1Cards);
    	game.removeAllFromDeck(player1Cards);
    	
    	ArrayList<Card> player2Cards = new ArrayList<>();
    	player2Cards.add(new ColourCard(Config.GREEN, 1));
    	player2Cards.add(new ColourCard(Config.GREEN, 1));
    	player2Cards.add(new ColourCard(Config.YELLOW, 4));
    	player2Cards.add(new ColourCard(Config.BLUE, 4));
    	player2Cards.add(new ColourCard(Config.GREEN, 1));
    	player2Cards.add(new ActionCard(Config.DROPWEAPON));
    	player2Cards.add(new SupportCard(Config.SQUIRE, 2));
    	player2Cards.add(new SupportCard(Config.SQUIRE, 3));
    	player2.setCards(player2Cards);
    	game.removeAllFromDeck(player2Cards);
    }
	
    @Before
    public void setUp() {
    	testNumber++;
		System.out.println("@Before: Start turn " + testNumber);
    	//test that the current player picks up a card at the beginning of their turn 
		// (adding card instead of using pickup function so that I can specify a card)
			game.getCurrentPlayer().addCard(pickup);
			game.removeCardfromDeck(pickup);
			if (testNumber == 1)
		    	player1.chooseTournamentColour(Config.YELLOW);
	    	game.startTurn();
    }
    
    @After
    public void tearDown() {
    	System.out.println("@After: end turn " + testNumber + "\n");
    	game.endTurn();
    }
    
    @Test
    public void test1Player1() {
    	System.out.println("@Test: Player1 sets tournament to yellow and plays 5 cards");
    	//test that we have the correct current player
    	assertEquals(player1.getName(), game.getCurrentPlayer().getName());
    	
    	//make sure that the first player in the players array is the one that picked purple, and the second player did not
    	assertEquals(Config.BLUE, game.getPlayers().get(0).getStartTokenColour());
    	assertEquals(Config.PURPLE, game.getPlayers().get(1).getStartTokenColour());
    	
    	//test correct number of players
    	int players = game.getPlayers().size();
    	assertEquals(2, players);
    	
    	//test that the draw deck has the correct number of cards
    	assertEquals(93, game.getDrawDeck().size());
    	
    	//test that players have the right size hand
    	assertEquals(9, game.getCurrentPlayer().getCards().size());
    	assertEquals(8, player2.getCards().size());
    	
    	//test that the first player chooses the tournament colour
    	assertEquals(Config.YELLOW, game.getTournamentColour());  	
    	
    	game.playCard(game.getCurrentPlayer().getCards().get(2));
    	game.playCard(game.getCurrentPlayer().getCards().get(3));
    	game.playCard(game.getCurrentPlayer().getCards().get(3));
    	game.playCard(pickup);
    	game.playCard(game.getCurrentPlayer().getCards().get(4));
    	
    	//test the current player's total card value and that it is greater than the value of the other player
    	assertEquals(18, game.getCurrentPlayer().getTotalCardValue());
    	assertEquals(0, player2.getTotalCardValue());
    	assertTrue(player1.getTotalCardValue() > player2.getTotalCardValue());
    	
    	//test that the current player has the correct number of cards left
    	assertEquals(4, game.getCurrentPlayer().getCards().size());
    	
    	//next player card to pick up 
    	pickup = new ColourCard(Config.GREEN, 1);

    }
    
    @Test
    public void test2Player2() {
    	System.out.println("@Test: Player2 sets tournament to green and plays 6 other cards");

    	//make sure that we have the correct current player
    	assertEquals(player2.getName(), game.getCurrentPlayer().getName());
    	
    	//test that players have the correct number of cards
    	assertEquals(4, player1.getCards().size());
    	assertEquals(9, game.getCurrentPlayer().getCards().size());
    	    	
    	game.playCard(game.getCurrentPlayer().getCards().get(5)); //tournament colour is green and cards are each worth 1 point
    	game.playCard(game.getCurrentPlayer().getCards().get(0));
    	game.playCard(game.getCurrentPlayer().getCards().get(1));
    	game.playCard(game.getCurrentPlayer().getCards().get(4));
    	game.playCard(pickup);
    	game.playCard(game.getCurrentPlayer().getCards().get(2));
    	game.playCard(game.getCurrentPlayer().getCards().get(2));
    	
    	//test that the current player has the correct number of cards left
    	assertEquals(2, game.getCurrentPlayer().getCards().size());

    	//test the current player's total card value and that it is greater than the value of the other player
    	assertEquals(5, player1.getTotalCardValue());
    	assertEquals(6, player2.getTotalCardValue());
    	assertTrue(player2.getTotalCardValue() > player1.getTotalCardValue());

    	//next player card to pick up
    	pickup = new ColourCard(Config.BLUE, 4);

    }
    
    @Test
    public void test3Player1() {
    	System.out.println("@Test: Player1 plays RIPOSTE and takes a card from player2's display");
    	//make sure that we have the correct current player
    	assertEquals(player1.getName(), game.getCurrentPlayer().getName());
    	
    	//test that players have the correct number of cards
    	assertEquals(5, game.getCurrentPlayer().getCards().size());
    	assertEquals(2, player2.getCards().size());
    	
    	
    	game.playCard(game.getCurrentPlayer().getCards().get(3)); //player1 plays riposte and takes player2's last played card, a squire of 3
    	
    	//test that the current player has the correct number of cards left
    	assertEquals(3, game.getCurrentPlayer().getCards().size());
    	
    	//test the current player's total card value and that it is greater than the value of the other player
    	assertEquals(6, player1.getTotalCardValue());
    	assertEquals(5, player2.getTotalCardValue());
    	assertTrue(player2.getTotalCardValue() > player1.getTotalCardValue());
    	
    	//next player pickup card
    	pickup = new ColourCard(Config.RED, 3);
    	
    }
    
    @Test
    public void test4Player2() {    	
    	System.out.println("@Test: Player2 has no playable cards");

    	//make sure that we have the correct current player
    	assertEquals(player2.getName(), game.getCurrentPlayer().getName());
    	
    	//test that the current player picks up a card at the beginning of their turn

    	assertEquals(3, player1.getCards().size());
    	assertEquals(3, game.getCurrentPlayer().getCards().size());
    	
    	//current player has no playable cards and is withdrawn
    	pickup = new ColourCard(Config.RED, 3);
    }
    
    @Test
    public void test5Player1() {
    	System.out.println("@Test: Player1 wins the tournament");
    	assertEquals(1, game.getPlayers().size());

    	//make sure that we have the correct current player
    	assertEquals(player1.getName(), game.getCurrentPlayer().getName());
    	
    	//test that the current player picks up a card at the beginning of their turn
    	assertEquals(4, game.getCurrentPlayer().getCards().size());
    	assertEquals(3, player2.getCards().size());
    	
    	//player1 is the last remaining, and is announced as the winner
    	assertTrue(player1.isWinner());
    	assertFalse(player2.isWinner());
    	
    	//test that the winning player gets the correct token
    	assertTrue(game.getCurrentPlayer().getCurrentTokens().contains(game.getTournamentColour()));

    }
    
    
}
