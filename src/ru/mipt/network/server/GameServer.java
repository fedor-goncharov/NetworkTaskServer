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
	
	public GameServer(String dataPath) {
		
	}

	public GameServer(String dataPath, int playersNumber, int port) {
		try {
			parseDataFile(dataPath);
			serverSocket = new ServerSocket(port);
			while (true) {	//always listen for new connections
				Socket clientSocket = serverSocket.accept();
				if (current_players < playersNumber) {
					clientList.add(clientSocket);	//add new connection
					current_players += 1;
				} else {
					
				}
				clientSocket.close();	//close connection
				//TODO create thread for 
			}
			//TODO
			//create GameState class
		} catch (IOException e) {
			e.printStackTrace();	//TODO handle such events
		} catch (NumberFormatException e) {
			e.printStackTrace();	//TODO handle such events			
		} catch (SecurityException e) {
			e.printStackTrace();	//TODO handle such events
		}
		//TODO
		//create GameState class, write all info there
	}
	
	/**
	 * Opens socket for connection to specified port, creates threads for clients 
	 */
	public void startGameSession() {
		
	}
	
	private ServerSocket serverSocket = null;
	private int playersNumber = 0;
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
}
