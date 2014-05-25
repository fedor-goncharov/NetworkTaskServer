package ru.mipt.network.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author fedor.goncharov.ol@gmail.com
 *
 */
public class GameState {
	
	public HashMap<Integer,Integer> score = new HashMap<Integer, Integer>();	//game score
	private Set<Integer> unusedQuestionIDs = new HashSet<Integer>();
	public int round = 0;
	
	//session variables
	public Set<Integer> answerSet = Collections.synchronizedSet(new HashSet<Integer>());
	public HashMap<Integer, Integer> finalAnswerMap = new HashMap<Integer, Integer>();
	
	public GameState(int numberOfPlayers, int initial_cash) {
		for (int i = 0; i < numberOfPlayers; ++i) {
			score.put(i, initial_cash);					//initial score - 0
			unusedQuestionIDs.add(new Integer(i));		//initial set of unused questions
		}
	}
	/**
	 * Returns an Integer value, id of the question, that hasn't been used yet.
	 * Deletes the chosen value from set. 
	 * @return Integer
	 */
	public Integer generateQuestionID() {
		int size = unusedQuestionIDs.size();
		int item = new Random().nextInt(size);
		int i = 0;
		for (Integer questionID : unusedQuestionIDs) {
			if (i == item) {
				unusedQuestionIDs.remove(questionID);
				return questionID;
			}
			i = i + 1;
		}
		
		return 1;
	}
}
