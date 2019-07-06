package code;

import java.net.SocketException;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.FileInputStream;
import java.io.IOException;

public class UDPclient {
	
    private DatagramSocket client;
	
    //create the client socket
	public UDPclient() throws SocketException { 
		this.client = new DatagramSocket();
	}
	
	//send the log file
	public void sendLogs() throws IOException, ClassNotFoundException { 
		
			//String fileName = "log_input.txt";
		String fileName = "log_input.txt";
			String line = null;
			
			Scanner scnr = new Scanner(new FileInputStream(fileName),"UTF-8");
			while(scnr.hasNextLine()) {
				
				 line = scnr.nextLine();
				 sendLogInfo(line);
			}
			sendLogInfo("Stop");
			sendLogInfo("S");
			scnr.close();					
	}
	
	//send a log to the server
	private void sendLogInfo(String line) throws IOException {
		
		//max payload of a UDP packet = 65507
		byte[] data = line.getBytes();
		InetAddress to = InetAddress.getByName("localhost");
		int port = 509;
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, to, port);

		client.send(datagramPacket);  
	}
	
private void receiveInfo() throws IOException, ClassNotFoundException {
			
			byte[] buffer = new byte[4096];
			
			//recieve the datagram packet from the server
			while(true) {
			DatagramPacket dps = new DatagramPacket(buffer, buffer.length);
			client.receive(dps);	
			String incomingLine = new String(dps.getData());
			String[] vars = incomingLine.split("#SUP:");

			System.out.println("ID: "+vars[0]+":"+vars[1]);
			
			}
			
}
	
	public static void main(String[] args) throws SocketException, IOException, ClassNotFoundException { 
		UDPclient c = new UDPclient();
		c.sendLogs();
		c.receiveInfo();
	}

}
