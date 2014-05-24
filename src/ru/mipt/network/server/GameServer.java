package ru.mipt.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();	//TODO show message, such port cannot be used
		}
		
	}
	
	public void startServer(String dataPath, int playersNumber, int port) {
		try {		
			while (true) {	//always listen for new connections
				Socket clientSocket = serverSocket.accept();
				current_players += 1;
				if (current_players == playersNumber) {
					clientList.add(clientSocket);	//add new connection
				} else {
					clientSocket.close();
					startGameSession(clientList);
					clearSession();
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens socket for connection to specified port, creates threads for clients 
	 */
	public void startGameSession(LinkedList<Socket> clientList) {
		//TODO
		//create GameState
		GameState gameState = new GameState();
	}
	
	private ServerSocket serverSocket = null;
	private int current_players = 0;
	
	GameState state = null;
	HashMap<Integer, String> questionsMap = new HashMap<Integer, String>();	//quiestions
	HashMap<Integer, Integer> answersMap = new HashMap<Integer, Integer>();	//answers
	LinkedList<Socket> clientList = new LinkedList<Socket>();	//connetions to clients
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
		for (Socket client : clientList) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//TODO
		//delete addresses, prepare for new round
	}
}
