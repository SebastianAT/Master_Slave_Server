package server;

import java.io.*;  
import java.net.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;  
public class MyServer {  
	/*
	 * TCP Server -Master Slave pattern
	 * @Trattnig Sebastian
	 * @Macher Markus
	 * @Singh Soni
	 */
	public static void main(String[] args){ 
		int slaveCount, timeout; 
		final int port;
		Resource res = new Resource();
	
		try{
			//kommandozeilenparameter  set parameter -> run configuration - arugments - 9120 3 30
			port = Integer.parseInt(args[0]);
			slaveCount = Integer.parseInt(args[1]);
			timeout = Integer.parseInt(args[2]);
			
			//socket setzen
			ServerSocket ss=new ServerSocket(9131);
			System.out.println("Connection ready!");
			System.out.println("Waiting mode begins. " + slaveCount + " slaves should connect");
			MyClient[] slaves = new MyClient[slaveCount];
			
			int clients = 0;
			final int[] clientIDs = new int[slaveCount];
			final int[] clientsSize = new int[slaveCount];
			boolean[] timerOff = new boolean[1];
			DataInputStream dis = null;
			DataOutputStream dout = null;
			Socket s = null;
			 BufferedReader Reader;
			  BufferedWriter Writer;
			//begin waiting mode either 3 slaves are connected or 20sek are over
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				  @Override
				  public void run() {
					  
					System.out.println("Server (Master) Timeout - check if there is at least 1 Slave connected"); 
					int clientSize = clientsSize[0];
					if(clientSize > 0) {
						timerOff[0] = true;
						
						if(clientSize == 1) {
							System.out.println("\nWaiting mode ends. There is " + clientSize + " Slave connected.");
							System.out.println("Distribute tasks to slave. \n");
						} else {
							System.out.println("\nWaiting mode ends. There are " + clientSize + " Slaves connected.");
							System.out.println("Distribute tasks to slaves. \n");
						}
						
						distributeTasks(slaves, clientSize, clientIDs, ss);
						System.exit(0);
						
					} else {
						System.out.println("\nWaiting mode ends.. No Slaves connected.");
						System.out.println("The master will now die ...");
						
						try {
							ss.close();
							System.out.println("CONNECTION CLOSED");
							System.exit(0);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					timer.cancel();
				  }
				}, 2*timeout*1000);
			
			
			int i = 0;
			while(clients < slaveCount) {
				System.out.println("\nWaiting for more slaves... ");
				 s=ss.accept();//establishes connection   
				System.out.println("Client tries to connect...");
				dis = new DataInputStream(s.getInputStream());
                clientIDs[i] = dis.readInt();
				
				System.out.println("The client with id: "+clientIDs[i] + " is connected!!!"); 
				//slaves[i] = new MyClient(res, clientIDs[i]);
				int valueA = (int)(Math.random() * 10) + 1;
	            int valueB = (int)(Math.random() * 10) + 1;
	            
	            slaves[i] = new MyClient(valueA, valueB, clientIDs[i]);
				clients++;
				clientsSize[0] = clients;
				if(clients == 1) {
					System.out.println(clients + " slave is connected.");
				} else {
					System.out.println(clients + " slaves are connected.");
				}
				
				i++;
			}
			timer.cancel();
			dout = new DataOutputStream(s.getOutputStream());
			BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
			 Reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		      Writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		    //System.out.println("server repeat as long as client not send null");
		      String str="",str2="";  
		      while(!str.equals("stop")){  
		      str=dis.readUTF();  
		      System.out.println("client says: "+str);  
		      str2=kb.readLine();  
		      dout.writeUTF(str2);  
		      dout.flush();  
		      }  
			/*
			//dont forget do cancel timer --> error
			if(timerOff[0] == false) {
				timer.cancel();
				if(clients == 1) {
					System.out.println("\nWaiting mode ends. There is " + clients + " Slave connected.");
					System.out.println("Distribute task to slave. \n");
				} else if(clients > 1) {
					System.out.println("\nWaiting mode ends. There are " + clients + " Slaves connected.");
					System.out.println("Distribute task to slaves. \n");
				}
				
				distributeTasks(slaves, clients, clientIDs, ss);
			}
			*/
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(e1);
		}  
	}

	private static void distributeTasks(MyClient[] slaves, int e, int[] clientIDs, ServerSocket ss) {
		for(int y = 0; y < e; y++) { 
			slaves[y].start();
			//System.out.println(slaves[y].getName() + " started and ready for work!");
		}

		// wait for slaves to die: 
		int finalResult = 0;
		for(int x = 0; x < e; x++) { 
			try { 
				slaves[x].join();
				finalResult += slaves[x].getResult();
				System.out.println("Result from Slave with ID "+ clientIDs[x] +" = " + slaves[x].getResult());
				
			} catch(InterruptedException ie) { 
				System.err.println(ie.getMessage()); 
			} finally { 
				System.out.println(slaves[x].getName() + " with ID: " + clientIDs[x] + " has died"); 
			} 
		} 
		
		System.out.println("\n(Master) Finale Result: "+ finalResult);
		System.out.println("The master will now die ... ");

		try {
			ss.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		System.out.println("CONNECTION CLOSED");
	}
}  