package ru.mipt.network.server;

/**
 * public class, for client-thread communication, all synchronized blocks based
 * on this class, should be the singleton class
 * @author fedor
 *
 */
public class SyncObject {
	public boolean startGame = false;
	public boolean newRound = false;
	public boolean askQuestion = false;
	public boolean allAnswered = false;
	public boolean allFinallyAnswered = false;
	public boolean scoreCheckedUpdated = false;
	public boolean updateRound = false;
	public boolean namesWritten = false;
	
	int question_id = 0;	//current question
	int correct_answer = 0;
	int sent = 0;
	
	/**
	 * change game session to initial state
	 */
	public void clean() {
		startGame = false;
		newRound = false;
		askQuestion = false;
		allAnswered = false;
		allFinallyAnswered = false;
		scoreCheckedUpdated = false;
		updateRound = false;
		namesWritten = false;
		sent = 0;
		question_id = 0;
		correct_answer = 0;
	}
}
