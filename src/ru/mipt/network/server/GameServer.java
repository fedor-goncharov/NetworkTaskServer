package ru.mipt.network.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Game server, sends questions, counts score and
 * sends answers to everybody.
 * @author fedor.goncharov.ol@gmail.com
 *
 */
public class GameServer {

	public GameServer(String dataPath, int port) {
		try {
			parseDataFile(dataPath);
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOError occured, server stopped. Check datapath and port");
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Format Error occured, server stopped. Bad format for port number");
		} catch (SecurityException e) {
			e.printStackTrace();
			System.out.println("Security Error occured, no permissions to open file, or start server on such port");
		}
		
	}
	
	public void startServer(int playersNumber) {
		try {	
			gameState = new GameState(playersNumber, initial_cash, 
					numberOfQuestions, answersMap);
			
			while (true) {	//always listen for new connections
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected:" + clientSocket.getInetAddress().toString());
				if (current_players < playersNumber) {
					current_players += 1;
					clientSocketList.add(clientSocket);	//add new connection
					ClientHandler clientHandler = new ClientHandler(
							current_players-1, 
							clientSocket,
							sync_object, 
							this,
							gameState,						//initial state is undefined
							initial_cash
					);
					clientHandlerList.add(clientHandler);
					clientHandler.start();				//start thread
				}
				if (current_players == playersNumber) {
					try {
						startGameSession();
						for (Thread client : clientHandlerList) {
							try {
								client.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						System.out.println("Exception occured, reloading server; message:" + e.getMessage());
						cleanSession();
						continue;
					}
					cleanSession();
					gameState.cleanState();
					gameState.round = 1;
					sync_object.clean();
				}
			}
		} catch (Exception e) {
			System.out.println("Exception occured, stopping server; message:" + e.getMessage());
			cleanSession();
		}
	}
	
	/**
	 * Opens socket for connection to specified port, creates threads for clients 
	 */
	private void startGameSession() {
		while (gameState.round < default_rounds + 1) {
			synchronized (sync_object) {	//game started
				sync_object.startGame = true;
				sync_object.notifyAll();
				System.out.println("server:-----Unblocked startGame-----");
			}
			sync_object.question_id = gameState.generateQuestionID();
			sync_object.correct_answer = answersMap.get(sync_object.question_id);
			System.out.println("Question is ready");
			synchronized (sync_object) {	//all clients wait for the question
				sync_object.askQuestion = true;
				sync_object.notifyAll();
				System.out.println("server:----Unblocked askQuestion-----");
			}
			synchronized (sync_object) {
				if (!sync_object.namesWritten) {
					try {
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (sync_object) {
				if (!sync_object.allAnswered) {
					try {
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (sync_object) {	//stop until all clients give their answer
				if (!sync_object.allFinallyAnswered) {
					try {
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			gameState.checkUpdateScore(sync_object.correct_answer);
			synchronized (sync_object) {
				sync_object.scoreCheckedUpdated = true;
				sync_object.notifyAll();
				System.out.println("server:----Unblocked scoreCheckedUpdated----");
			}
			synchronized (sync_object) {
				if (!sync_object.newRound) {
					try {
						sync_object.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			gameState.cleanState();
			synchronized (sync_object) {
				gameState.round = gameState.round + 1;	//step
				sync_object.clean();
				sync_object.updateRound = true;
				sync_object.notifyAll();
				System.out.println("server:----Unblocked updateRound----");
			}
		}
	}
	
	private ServerSocket serverSocket = null;
	private int current_players = 0;
	private int initial_cash = 10;
	public int default_rounds = 10;
	private int numberOfQuestions = 40;
	
	//synchronization object, to control client threads
	private SyncObject sync_object = new SyncObject();	
	
	HashMap<Integer, String> questionsMap = new HashMap<Integer, String>();	//all quiestions 
	HashMap<Integer, Integer> answersMap = new HashMap<Integer, Integer>();	//all answers
	LinkedList<Socket> clientSocketList = new LinkedList<Socket>();	//socket connetions to clients
	LinkedList<ClientHandler> clientHandlerList = new LinkedList<ClientHandler>(); //client threads
	GameState gameState = null;
	//private methods
	/**
	 * parsing file with questions and answers, saving all results 
	 */
	public void parseDataFile(String dataPath) throws IOException, NumberFormatException {
		
		File file = new File(dataPath);
		BufferedReader buffReader = new BufferedReader(new FileReader(file));
		
		String questionLine, answerLine;	//read questions and answers line-by-line
		int questionNumber = 0;
		while ((questionLine = buffReader.readLine()) != null && 
				(answerLine = buffReader.readLine()) != null) {
			questionsMap.put(questionNumber, questionLine);	//put question
			Integer answer = Integer.valueOf(answerLine.replaceAll("\\s+",""));
			answersMap.put(questionNumber, answer);	//put answer
			questionNumber += 1;
		}
		numberOfQuestions = questionNumber;
		buffReader.close();
	}
	private void cleanSession() {
		for (Socket client : clientSocketList) {
			try {
				client.close();			//close connections with clients
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		clientSocketList.clear();
		clientHandlerList.clear();
		current_players = 0;
	}
}
