package ru.mipt.network.server;

public class ServerMain {

	/**
	 * Testing area for project
	 * @param args
	 */
	public static void main(String[] args) {
			String dataPath = "/home/fedor/Programming/workspace/NetworkTask/questions.txt";
			
			GameServer gameServer = new GameServer(dataPath, 7777);
			gameServer.startServer(2);

	}

}
