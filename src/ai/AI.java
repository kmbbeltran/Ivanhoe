package ai;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import config.Config;
import config.Observer;
import config.Subject;
import game.Card;
import game.Player;
import network.Server;

public class AI extends Player implements Subject{
	private Strategy strategy;
	private ArrayList<Card> hand = new ArrayList<Card>();
	
	private Logger log = Logger.getLogger("AI");
	private boolean currentPlayer;
	private ArrayList<Observer> observers = new ArrayList<Observer>();
	
	private String output = "result";
	
	public AI(Strategy s){
		this.strategy = s;
		this.strategy.setName("AI");
	}
	
	public void processInput(String msg){
		System.out.println("AI received: " + msg);
		if(msg.contains(Config.HAND)){
			strategy.processPlayerName(msg);
		}
		
		else if(msg.contains("input") || msg.contains(Config.GAME_WINNER)){
			// do nothing 
			
		}else{
			output = strategy.processInput(msg);
			notifyObservers(output);
		}
	}

	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void notifyObservers(String message) {
		Observer ob = observers.get(0);
		ob.update(message);
	}
}
