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
	Object sync_object = null;	//thread synchronization Object
	DataInputStream inReader = null;	//data reader from the socket
	DataOutputStream outWriter = null; 	//data writer to the socket
	
	private GameServer gameServer = null;	//reference to master class
	private GameState  gameState = null;
	
	public ClientHandler(int id, Socket client, Object sync_object,
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
			//get name from client, give him initial cash -- sleep
			name = inReader.readUTF();
			outWriter.writeInt(cash);	//send it's initial cash
			
			// --> block before generating questions
			//TODO generate correct questions
			int question_id = 0;
			outWriter.writeInt(question_id);
			outWriter.writeUTF(gameServer.questionsMap.get(new Integer(question_id)));
			int firstAnswer = inReader.readInt();
			//notify server that you read the answer
			// --> block all threads
			//server summarizes all the results and gives an array
			//threads unblocked, send all variants
			//read answer
			//send score
			//new game
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * After setter, invoked when game starts
	 * @param gameState
	 */
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
}
