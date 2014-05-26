package ru.mipt.network.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientHandler extends Thread {

	int id; 		//client id
	int cash;		//current player money
	Socket client = null;
	String name;	//client name
	SyncObject sync_object = null;	//thread synchronization Object
	DataInputStream inReader = null;	//data reader from the socket
	DataOutputStream outWriter = null; 	//data writer to the socket
	ObjectOutputStream objWriter = null; //object writer to the socket
	
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
			objWriter = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
@Override
public void run() {
	try {
		int rubbish = inReader.readInt(); //rubbish when first read from socket
		name = inReader.readUTF();
		System.out.println("Client name:" + name);	//someone connected
		while (gameState.round < gameServer.default_rounds + 1) {
			
			System.out.println(name + " : Round:" + gameState.round);
			synchronized (sync_object) {	//start game
				if (!sync_object.startGame) {
					try {
						System.out.println(name + ": Client "+ id +" Blocked at startGame");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			synchronized (sync_object) {	//generate question, all threads must wait until question being
											//generated
				if (!sync_object.askQuestion) {
					try {
						System.out.println(name + ": Client " + id + " Blocked at askQuestion");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			int question_id = sync_object.question_id;
			outWriter.writeInt(question_id);
			outWriter.writeUTF(gameServer.questionsMap.get(new Integer(question_id)));
			if (gameState.round == 1) {
				synchronized (sync_object) {	//atomically add all names to the names array
					gameState.names.put(new Integer(id), name);
					if (gameState.names.size() == gameState.numberOfPlayers) {
						sync_object.namesWritten = true;
						sync_object.notifyAll();
					} else {
						try {
							sync_object.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				outWriter.writeInt(cash);	//send it's initial cash
				objWriter.writeObject(new ArrayList<String>(gameState.names.values()));
			}
			
			Integer answer = new Integer(inReader.readInt());
			synchronized (sync_object) {
				gameState.answerArray.add(answer);
				if (gameState.answerArray.size() == gameState.numberOfPlayers) { //add first answer to set
					gameState.answerSet = new HashSet<Integer>(gameState.answerArray);
					sync_object.allAnswered = true;								//check if the last thread called this method
					sync_object.notifyAll();
				} else {
					try {
						System.out.println(name + ": Client " + id + " Blocked at allAnswered");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			objWriter.writeObject(new ArrayList<Integer>(gameState.answerSet));	//send all answers to the client
			Integer finalAnswer = new Integer(inReader.readInt());
			Integer bet = new Integer(inReader.readInt());			
			
			System.out.println(name + ": Client " + id + " Final Answer:" + finalAnswer);
			System.out.println(name + ": Client " + id + " Bet:" + bet);
			//wait for everyone to send their results
			synchronized (sync_object) {
				gameState.finalAnswerMap.put(new Integer(id), finalAnswer);
				gameState.bets.put(new Integer(id), bet);
				if (gameState.finalAnswerMap.size() == gameState.numberOfPlayers) {
					sync_object.allFinallyAnswered = true;
					sync_object.notifyAll();
				} else {
					try {
						System.out.println(name + ": Client " + id + " Blocked at finalAnswer");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			//count round
			synchronized (sync_object) {
				if (!sync_object.scoreCheckedUpdated) {
					try {
						System.out.println(name + ": Client " + id + " Blocked at scoreCheckUpdated");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			outWriter.writeBoolean((gameState.winners.get(new Integer(id)) == true ? true : false));
			objWriter.writeObject(new ArrayList<Integer>(gameState.score.values()));
			outWriter.writeInt(sync_object.correct_answer);
			synchronized (sync_object) {
				sync_object.sent = sync_object.sent + 1;
				if (sync_object.sent == gameState.numberOfPlayers) {
					sync_object.newRound = true;
					sync_object.updateRound = false;	//strange command, but required for sync
					sync_object.notifyAll();
				} else {
					try {
						System.out.println(name +": Client " + id + " Blocked at newRound");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println(name + ": Round " + gameState.round + " finished." );
			synchronized (sync_object) {
				if (!sync_object.updateRound) {
					try {
						System.out.println(name + ": Client " + id + " Blocked at updateRound");
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} 
			}
		}
	} catch (Exception e) {
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
