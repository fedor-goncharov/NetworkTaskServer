package ru.mipt.network.server;

/**
 * public class, for client-thread communication
 * @author fedor
 *
 */
public class SyncObject {
	public boolean startGame = false;
	public boolean newRound = false;
	public boolean askQuestion = false;
	/**
	 * change game session to initial state
	 */
	public void respawn() {
		newRound = false;
		askQuestion = false;
		//TODO
		//recreate boolean variables
	}
}
