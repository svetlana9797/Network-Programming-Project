package code;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import ca.pfv.spmf.test.MainTestDefMe_saveToFile;


//send info back to client ->diff function
public class UDPserver {

//create the server socket
private DatagramSocket server;
	
	public UDPserver() throws SocketException {
		this.server = new DatagramSocket(509);
		//System.out.println(server.isBound());
	}
	
	
	//receive the objects from the client
	private void receiveLogInfo() throws IOException, ClassNotFoundException {
		
		int port=0;
		InetAddress ipAddress=null;
		FileWriter writer = new FileWriter("log_results.txt");
		
		//receive the info and write it to a file
		while(true) {
		
			byte[] buffer = new byte[4096];
			//recieve the datagram packet from the client
			DatagramPacket dps = new DatagramPacket(buffer, buffer.length);
			server.receive(dps);
			
			//take the IP adress and port of the sender
			ipAddress = dps.getAddress();
		    port = dps.getPort();
		    getAddress(port, ipAddress);
			
			String incomingLine = new String(dps.getData());
			//writing the content to a file in the appropriate format
			String[] vars = incomingLine.split(",");
			if(vars.length<4) {//  || (incomingLine.charAt(0)!='"')) {
				break;
			}
			
			
				String content = vars[5];
			
			//extract the first number from the content description
			String number="";
			int temp=0;
			for(int i=0;i<content.length();i++) {
				char c=content.charAt(i);
			 if(Character.isDigit(c)) {
				 temp=1;
				 while(Character.isDigit(c)) {
				     number+=c;
				     i=i+1;
				     c=content.charAt(i);
			       }
				}	
			 if(temp==1)
				 break;
		     }
		
			//write the user id for each log
			writer.write(number);
			writer.flush();
			writer.write("\r\n");
			writer.flush();
			
		}
		writer.close();
		
	 }

	//write the ipAddress and port to a file
	void getAddress(int port, InetAddress ipAddr) throws IOException {
		FileWriter writerAddr = new FileWriter("address.txt");
		writerAddr.write(Integer.toString(port));
		writerAddr.flush();
		writerAddr.write("\r\n");
		writerAddr.flush();
		String host=ipAddr.getHostName();
		writerAddr.write(host);
		writerAddr.flush();
		writerAddr.close();
	
	}
	
	void sendInfo() throws IOException {
		//get the IpAddress and port of the host
		String ipHost = null;
		int port=0;
		Scanner sc1 = new Scanner(new FileInputStream("address.txt"));
		
		port = Integer.parseInt(sc1.nextLine());
		ipHost=sc1.nextLine();
		InetAddress ipAddr= InetAddress.getByName(ipHost);
		sc1.close();
			 
		//send the info to the client
		//System.out.println("send info");
		String fileName = "output.txt";
		String line = "";
		Scanner scnr = new Scanner(new FileInputStream(fileName));
		
		//first line is total number of rows
		
		while(scnr.hasNextLine()) {
		line=scnr.nextLine(); 
		byte[] data = line.getBytes();
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, ipAddr, port);

		server.send(datagramPacket); 
	
	}
		scnr.close();
}
	
	public static void main(String[] args) throws SocketException, IOException, ClassNotFoundException {
		
		UDPserver s = new UDPserver();
		s.receiveLogInfo();
		
		//run DefMe algorithm
		final String[] res = {"log_results.txt","output.txt"};
		System.out.println("Main\n");
		MainTestDefMe_saveToFile.main(res);
		
	    //send info back to client
    	s.sendInfo();
		
		
	}
}
