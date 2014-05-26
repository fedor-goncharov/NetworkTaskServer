package ru.mipt.network.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * @author fedor.goncharov.ol@gmail.com
 *
 */
public class GameState {
	
	//general variables
	public HashMap<Integer,Integer> score = new HashMap<Integer, Integer>();	//game score
	public HashMap<Integer,Integer> answers = new HashMap<Integer, Integer>();	//answers
	public HashMap<Integer, Boolean> winners = new HashMap<Integer, Boolean>();	//winners or loosers
	private Set<Integer> unusedQuestionIDs = new HashSet<Integer>();
	public int round = 1;
	public int numberOfPlayers = 0;
	
	//session variables
	public Map<Integer, String> names = Collections.synchronizedMap(new HashMap<Integer, String>());		//client names
	public Set<Integer> answerSet = Collections.synchronizedSet(new HashSet<Integer>());
	public List<Integer> answerArray = Collections.synchronizedList(new ArrayList<Integer>());
	public Map<Integer, Integer> finalAnswerMap = Collections.synchronizedMap(
				new HashMap<Integer, Integer>());
	public Map<Integer, Integer> bets = Collections.synchronizedMap(	//bets
				new HashMap<Integer, Integer>());
	
	//constructor
	public GameState(int numberOfPlayers, int initial_cash, int numberOfQuestions,
			HashMap<Integer,Integer> answers) {
		this.numberOfPlayers = numberOfPlayers;
		this.answers = answers;
		for (int i = 0; i < numberOfPlayers; ++i) {
			score.put(i, initial_cash);					//default cash-200
		}
		for (int i = 0; i < numberOfQuestions; ++i) {
			unusedQuestionIDs.add(new Integer(i));
		}
	}
	/**
	 * Save client name to game state class.
	 * @param id
	 * @param name
	 */
	public void addClientName(int id, String name) {
		names.put(new Integer(id), name);
	}
	/**
	 * Check correct answers, update score, define who won and who lost this round.
	 * @param correct_answer int - value of the correct answer
	 */
	public void checkUpdateScore(int correct_answer) {
		int distance = Integer.MAX_VALUE;
		int best_answer = 0;
		HashSet<Integer> non_loosers = new HashSet<Integer>();	//empty set, id's of non-loosers, who haven't exceed the correct answer
		
		System.out.println("Correct answer:" + correct_answer);
		for (Integer client_id : finalAnswerMap.keySet()) {
			if ((correct_answer - finalAnswerMap.get(client_id)) < 0) {	//answer is totally(Jeka) wrong
				System.out.println("Final answer:" + finalAnswerMap.get(client_id));
				int new_score = ((score.get(client_id) - bets.get(client_id)) <= 0 ? 1 : 
					score.get(client_id) - bets.get(client_id));
				System.out.println(new_score);
				
				score.put(client_id, new Integer(new_score));
				winners.put(client_id, false);
			} else {
				non_loosers.add(client_id);
				System.out.println("non-loosers found!!!");
				if (correct_answer - (finalAnswerMap.get(client_id)) < distance) {
					distance = correct_answer - finalAnswerMap.get(client_id);
					best_answer = finalAnswerMap.get(client_id);
					
				}
			}
		}
		for (Integer client_id : non_loosers) {
			if (finalAnswerMap.get(client_id) == best_answer) {
				System.out.println("Winneer:" + client_id);
				score.put(client_id, score.get(client_id) + bets.get(client_id));
				winners.put(client_id, true);
			} else {
				int new_score = (score.get(client_id) - bets.get(client_id) <= 0 ? 1 : 
					score.get(client_id) - bets.get(client_id));
				score.put(client_id, new Integer(new_score));
				winners.put(client_id, false);
			}
		}
	}
	/**
	 * Returns an Integer value, id of the question, that hasn't been used yet.
	 * Deletes the chosen value from set. 
	 * @return Integer
	 */
	public int generateQuestionID() {
		int size = unusedQuestionIDs.size();
		int item = new Random().nextInt(size);
		int i = 0;
		for (Integer questionID : unusedQuestionIDs) {
			if (i == item) {
				unusedQuestionIDs.remove(questionID);
				return questionID.intValue();
			}
			i = i + 1;
		}
		return -1;//never should get here
	}
	public void cleanState() {
		winners.clear();
		names.clear();
		answerSet.clear();
		answerArray.clear();
		finalAnswerMap.clear();
		bets.clear();
	}
}
