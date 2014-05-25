package ru.mipt.network.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

	int id; 		//client id
	int cash;		//current player money
	Socket client = null;
	String name;	//client name
	SyncObject sync_object = null;	//thread synchronization Object
	DataInputStream inReader = null;	//data reader from the socket
	DataOutputStream outWriter = null; 	//data writer to the socket
	
	private GameServer gameServer = null;	//reference to master class
	private GameState  gameState = null;
	
	public ClientHandler(int id, Socket client, SyncObject sync_object,
			GameServer gameServer,
			GameState gameState,
			int cash) {
		this.id = id;
		this.client = client;
		this.sync_object = sync_object;
		this.cash = cash;
		this.gameServer = gameServer;
		this.gameState = gameState;
		try {
			inReader = new DataInputStream(client.getInputStream());
			outWriter = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	@Override
	public void run() {
		try {
			gameState.addClientName(id, inReader.readUTF());
			outWriter.writeInt(cash);	//send it's initial cash
			synchronized (sync_object) {	//start game
				if (!sync_object.startGame) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (sync_object) {	//generate question, all threads must wait until question being
											//generated
				if (!sync_object.askQuestion) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			int question_id = sync_object.question_id;
			outWriter.writeInt(question_id);
			outWriter.writeUTF(gameServer.questionsMap.get(new Integer(question_id)));
			gameState.addAnswerToSet(inReader.readInt()); //add first answer to set
			//must synchronize here on all threads, that everybody has finished their answer
			//block here
			
			// --> block all threads
			//server summarizes all the results and gives an array
			//threads unblocked, send all variants
			//read answer
			gameState.finalAnswerMap.put(new Integer(id), new Integer(inReader.readInt()));
			//check unswers
			//send score
			//new game
			synchronized (sync_object) {
				if (!sync_object.newRound) {
					try {
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * setter, invoked when game starts
	 * @param gameState
	 */
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
}
