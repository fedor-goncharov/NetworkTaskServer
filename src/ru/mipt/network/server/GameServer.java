package ru.mipt.network.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Game server, sends questions, counts score and
 * sends answers to everybody.
 * @author fedor
 *
 */
public class GameServer {

	public GameServer(String dataPath, int port) {
		this.port = port;
		try {
			parseDataFile(dataPath);
			//TODO
			//create GameState class
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO
		//create GameState class, write all info there
	}
	
	/**
	 * Opens socket for connection to specified port, creates threads for clients 
	 */
	public void startServer() {
		
	}
	
	private int max_clients = 20;
	private int current_clients = 0;
	private int port = 6666;
	
	GameState state = null;
	HashMap<Integer, String> questionsMap = new HashMap<Integer, String>();	//quiestions
	HashMap<Integer, Integer> answersMap = new HashMap<Integer, Integer>();	//answers
	
	LinkedList<Socket> clientList = new LinkedList<Socket>();	//connetions to clients
	//private methods
	/**
	 * parsing file with questions and answers, saving all results 
	 */
	private void parseDataFile(String dataPath) throws IOException {
		
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
		}
		//TODO
		//implement parsing of the data file
	}
}
