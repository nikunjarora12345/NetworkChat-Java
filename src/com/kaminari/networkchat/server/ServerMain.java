package com.kaminari.networkchat.server;

public class ServerMain {
	
	private int port;
	private Server server;
	
	public ServerMain(int port){
		this.port = port;
		server = new Server(this.port);
	}

	public static void main(String[] args) {
		int port;
		if(args.length != 1){
			System.out.println("Usage: java -jar NetworkChatServer.java [port]");
			return;
		}
		
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}

}
