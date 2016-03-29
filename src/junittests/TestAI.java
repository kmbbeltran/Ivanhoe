package junittests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ai.AI;
import ai.Strategy;
import ai.StrategyPlayAll;
import ai.StrategyWithdraw;
import config.Config;
import game.GameEngine;
import game.Card;
import game.ActionCard;
import game.ColourCard;
import game.SupportCard;

import network.Server;

public class TestAI {
	static AI withdraw;
	static AI playAll;
	static Server server;
	static GameEngine game; 
	static ArrayList<Card> withdrawCards;
	static ArrayList<Card> playAllCards;
	
	@BeforeClass
	public static void BeforeClass(){
		System.out.println("BeforeClass: TestAI");
	}
	
	public static void giveCards(){
		withdrawCards = new ArrayList<Card>();
    	withdrawCards.add(new ColourCard("red", 3));
    	withdrawCards.add(new ColourCard("purple", 5));
    	withdrawCards.add(new ColourCard("yellow", 3));
    	withdrawCards.add(new ColourCard("blue", 4));
    	withdrawCards.add(new ColourCard("yellow", 3));
    	withdrawCards.add(new ColourCard("yellow", 2));
    	withdrawCards.add(new ActionCard("riposte"));
    	withdrawCards.add(new SupportCard("squire", 2));
    	withdraw.setCards(withdrawCards);
    	game.removeAllFromDeck(withdrawCards);
    	
    	playAllCards = new ArrayList<Card>();
    	playAllCards.add(new ColourCard("red", 5));
    	playAllCards.add(new ColourCard("green", 1));
    	playAllCards.add(new ColourCard("yellow", 4));
    	playAllCards.add(new ColourCard("blue", 4));
    	playAllCards.add(new ColourCard("green", 1));
    	playAllCards.add(new ActionCard("unhorse"));
    	playAllCards.add(new SupportCard("squire", 2));
    	playAllCards.add(new SupportCard("squire", 3));
    	playAll.setCards(playAllCards);
    	game.removeAllFromDeck(playAllCards);
	}
	
	@Before
	public void setUp(){
		System.out.println("Before: TestAI");
		
		//server = new Server();
		game = new GameEngine();
		withdraw = new AI(new StrategyWithdraw("Withdraw"));
		playAll = new AI(new StrategyPlayAll("PlayAll"));
		
		//game = server.getGame();
		
		game.joinGame(withdraw);
		assertTrue(game.getJoined());
		game.joinGame(playAll);
		assertTrue(game.getJoined());
		
		game.startGame();
		giveCards();
		
		withdraw.getStrategy().getHand(withdrawCards);
		playAll.getStrategy().getHand(playAllCards);
	}
	
	@Test
	public void testWithdrawStartTournament() {
		System.out.println("AI will start the tournament");
		
		String colour = withdraw.getStrategy().startTournament();
		assertEquals(Config.RED, colour);
	}
	
	@Test
	public void testWithdrawPlayACard(){
		System.out.println("AI will start the tournament and a play a card to continue");
		
		String colour = withdraw.getStrategy().startTournament();
		assertEquals(Config.RED, colour);
		
		withdraw.getStrategy().setStarted(true);
		String compare = withdraw.getStrategy().playACard();
		Card toPlay = withdraw.getStrategy().getToPlay();
		String message = Config.PLAY + " " + toPlay.getType() + " " + toPlay.getValue();
		
		assertEquals(compare, message);
	}
	
	@Test
	public void testWithdrawWithdraws(){
		System.out.println("a real player will set the tournament colour and play a turn, then the AI will withdraw");
		
		String compare = withdraw.getStrategy().playACard();
		assertEquals(Config.WITHDRAW, compare);
	}

}
