package com.kaminari.networkchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server implements Runnable {

	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;

	private List<ServerClient> clients = new ArrayList<ServerClient>();

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}

	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					// Managing
				}
			}
		};
		manage.start();
	}

	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data,
							data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}

	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if (string.startsWith("/c/")) {
			clients.add(new ServerClient(string.substring(3), packet
					.getAddress(), packet.getPort(), new Random().nextInt()));
			broadcast("/m/Server: Welcome " + string.substring(3));
		} else if (string.startsWith("/m/")) {
			sendMessage(string, packet);
		} else {
			System.out.println(string);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port){
		send = new Thread("Send"){
			@Override
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void sendMessage(String message, DatagramPacket packet){
		for(ServerClient client : clients){
			if(client.address != packet.getAddress() && client.port != packet.getPort())
			send(message.getBytes(), client.address, client.port);
		}
	}

	private void broadcast(String message) {
		for(ServerClient client : clients){
			send(message.getBytes(), client.address, client.port);
		}
	}

}