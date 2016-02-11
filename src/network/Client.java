package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import config.Config;
import org.apache.log4j.Logger;

public class Client implements Runnable {
	public int ID = 0;
	public Socket socket = null;
	public Thread thread = null;
	public ClientThread client = null;
	public BufferedReader console = null;
	public BufferedReader inStream = null;
	public BufferedWriter outStream = null;
	String testing;
	public Logger log = Logger.getLogger("Client");

	public int getID(){
		return this.ID;
	}
	

	public String testMessages() {
		return testing;
	}
	
	public Client(){
		connectToServer(Config.DEFAULT_HOST, Config.DEFAULT_PORT);
	}
	
	public boolean connectToServer(String serverIP, int serverPort) {
		System.out.println(ID + ":Establishing connection. Please wait... ");
		log.info(ID + ":Establishing connection. Please wait... ");
		boolean connected = false;
		
		try{
			this.socket = new Socket(serverIP, serverPort);
			this.ID = socket.getLocalPort(); 
			
			log.info(ID + ": Connected to server: " + socket.getInetAddress());
	    	log.info(ID + ": Connected to portid: " + socket.getLocalPort());
	    	
	    	this.start();
	    	log.info("Client has started");
	    	connected = true;
		}catch(IOException e){
			connected = false;
			log.error(e);
		}
		return connected;
	}
	
	private void start() {
		try{
			console = new BufferedReader(new InputStreamReader(System.in));
			inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			log.info("Initializing buffers");
			
			if(thread == null){
				client = new ClientThread(this, socket);
				thread = new Thread(this);
				thread.start();
				log.info("New ClientThread has started");
			}
		}catch (IOException e){
			log.error(e);
		}
	}

	@Override
	public void run() {
		System.out.println(ID + ": Client Started...");
		while (thread != null) {  
			try {  
				if (outStream != null) {
					outStream.flush();
					outStream.write(console.readLine() + "\n");
				} else {
					System.out.println(ID + ": Stream Closed");
				}
         }
         catch(IOException e) {  
         	log.error(e);
         	stop();
         }}
		System.out.println(ID + ": Client Stopped...");
	}

	public void handle(String msg) {
		System.out.println("Msg3333: "+ msg);
		System.out.println("1: " + testing);
	   	if (msg.equalsIgnoreCase("quit!")) {  
				System.out.println(ID + "Good bye. Press RETURN to exit ...");
				stop();
			} else {
				testing = msg;
				System.out.println(msg);
			}
	}


	public void stop() {
		
	}


}
