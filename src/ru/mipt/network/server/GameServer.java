package ru.mipt.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
		} catch (NumberFormatException e) {
			e.printStackTrace();	//TODO show message, that entered number is illegal
		} catch (SecurityException e) {
			e.printStackTrace();	//TODO show message, such port cannot be used
		}
		
	}
	
	public void startServer(int playersNumber) {
		try {		
			while (true) {	//always listen for new connections
				Socket clientSocket = serverSocket.accept();
				current_players += 1;
				if (current_players < playersNumber) {
					clientSocketList.add(clientSocket);	//add new connection
					ClientHandler clientHandler = new ClientHandler(
							current_players-1, 
							clientSocket, 
							sync_object, 
							this,
							null,						//initial state is undefined
							initial_cash
					);
					clientThreadList.add(clientHandler);
					clientHandler.start();				//start thread
				} else {
					clientSocket.close();
					startGameSession(clientThreadList);
					clearSession();
					current_players = 0;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens socket for connection to specified port, creates threads for clients 
	 */
	public void startGameSession(LinkedList<ClientHandler> clientList) {
		GameState gameState = new GameState(clientList.size());
		for (ClientHandler client : clientList) {
			client.setGameState(gameState);
		}
		
		
	}
	
	private ServerSocket serverSocket = null;
	private int current_players = 0;
	private int initial_cash = 200;	//starting money
	//TODO
	//sync object must be very special
	private Object sync_object = new Object();
	
	HashMap<Integer, String> questionsMap = new HashMap<Integer, String>();	//all quiestions 
	HashMap<Integer, Integer> answersMap = new HashMap<Integer, Integer>();	//all answers
	LinkedList<Socket> clientSocketList = new LinkedList<Socket>();	//socket connetions to clients
	LinkedList<ClientHandler> clientThreadList = new LinkedList<ClientHandler>(); //client threads
	Set<Integer> answerSet = Collections.synchronizedSet(new HashSet<Integer>());	//thread-protected set,
																					//for client-answers
	//private methods
	/**
	 * parsing file with questions and answers, saving all results 
	 */
	private void parseDataFile(String dataPath) throws IOException, NumberFormatException {
		
		BufferedReader buffReader = new BufferedReader(
										new InputStreamReader(
										new DataInputStream(
										new FileInputStream(dataPath)
										)));
		String questionLine, answerLine;	//read questions and answers line-by-line
		int questionNumber = 0;
		while ((questionLine = buffReader.readLine()) != null && 
				(answerLine = buffReader.readLine()) != null) {
			questionsMap.put(questionNumber, questionLine);	//put question
			Integer answer = Integer.valueOf(answerLine);
			answersMap.put(questionNumber, answer);	//put answer
			questionNumber += 1;
		buffReader.close();	//close stream after parsing
		}
	}
	private void clearSession() {
		for (Socket client : clientSocketList) {
			try {
				client.close();	//close connections with clients
			} catch (IOException e) {
				e.printStackTrace();
			}
			answerSet.clear();
		}
	}
}
