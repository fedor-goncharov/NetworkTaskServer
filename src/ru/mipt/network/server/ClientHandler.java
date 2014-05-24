package ru.mipt.network.server;
import java.net.Socket;

public class ClientHandler extends Thread {

	int id; 		//client id
	Socket client = null;
	String name;	//client name
	Object sync_object = null;	//thread synchronization Object
	
	public ClientHandler(int id, Socket client, Object sync_object) {
		this.id = id;
		this.client = client;
		this.sync_object = sync_object;
	}
			
	@Override
	public void run() {
		//1 - get name from client, give him initial cash -- synchronize from server
		//
	}
}
