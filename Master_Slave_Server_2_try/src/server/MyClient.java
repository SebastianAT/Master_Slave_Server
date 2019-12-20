package server;

import java.io.*;  
import java.net.*;
import java.util.Scanner;  
public class MyClient extends Thread {  
	boolean done = false;
	private Resource resource; 
	private int clientIDs, valueA, valueB, clientID, result;
	
	 public MyClient(int valueA, int valueB, int clientID){
	        this.valueA = valueA;
	        this.valueB = valueB;
	        this.clientID = clientID;
	 }
	 
	 public MyClient(Resource rcs, int clientIDs) { 
			resource = rcs; 
			this.clientIDs = clientIDs;
	 } 
	
	public static void main(String[] args) {
		int clientID = 0;
		DataInputStream dis;
		DataOutputStream dout;
		Socket s = null;
		 BufferedReader Reader;
		  BufferedWriter Writer;
		try{      
			s = new Socket("localhost",9131);  
			dout = new DataOutputStream(s.getOutputStream()); 
			dis = new DataInputStream(s.getInputStream());
			Scanner sc = new Scanner(System.in);
			System.out.println("Geben Sie eine ID für den client ein!");
			clientID = sc.nextInt();
			dout.writeInt(clientID); 
			
			
			BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
			Writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		      Reader = new BufferedReader(new InputStreamReader(s.getInputStream()));

		      String str="",str2="";  
		      while(!str.equals("stop")){  
		      str=kb.readLine();  
		      dout.writeUTF(str);  
		      dout.flush();  
		      str2=dis.readUTF();  
		      System.out.println("Server says: "+str2);  
		      }  
			
			/*dout.flush();  
			dout.close();  
			s.close(); */ 
		}catch(Exception e){
			System.out.println(e);
		} 
		


	} 

	public void run() { 
	//	while(!done) { 
		//	done = task(); 	
			try {
				Thread.sleep(1000); 
                result = valueA+valueB;
                System.out.println("Client with ID: " + clientID +" calculates: " +valueA +"+"+ valueB +"=" +result);
			} 
			// sleep for 1 sec. 
			catch(Exception e) {
			} 
		} 
	//} 
	
	protected boolean task() {  
		int valueCalculated = resource.incValue(clientIDs);
		return 5 < valueCalculated; 
	} 
	
	public int getResult(){
        return result;
    }
}  