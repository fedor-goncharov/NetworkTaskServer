package ru.mipt.network.server;

import java.util.HashMap;

/**
 * @author fedor.goncharov.ol@gmail.com
 *
 */
public class GameState {
	
	public HashMap<Integer,Integer> score = new HashMap<Integer, Integer>();	//game score
	
	public GameState(int numberOfPlayers) {
		for (int i = 0; i < numberOfPlayers; ++i) {	//initial score
			score.put(i, 0);
		}
	}
}
