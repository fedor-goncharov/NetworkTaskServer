package ru.mipt.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerMain {

	/**
	 * Main block of server, executing with arguments: dataPath for questions and port
	 * @param args
	 */
	public static void main(String[] args) {
		
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String dataPath = null;
			String port = null;
			String players = null;
			int nPort = 7777;
			int nPlayers = 0;
			try {
				System.out.println("Path to questions:");
				dataPath = in.readLine();
				System.out.println("Port:");
				port = in.readLine();
				nPort = Integer.valueOf(port);
				System.out.println("Players:");
				players = in.readLine();
				nPlayers = Integer.valueOf(players);
			} catch (IOException e) {
				System.out.println("IOError in Path. Shutting down.");
				System.exit(1);
			} catch (NumberFormatException e) {
				System.out.println("Bad port or players input.Shutting down.");
				System.exit(1);
			}
						
			GameServer gameServer = new GameServer(dataPath, nPort);
			gameServer.startServer(nPlayers);

	}

}
