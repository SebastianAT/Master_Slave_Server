package server;

public class Resource { 
	private int valueCalc = 0; 
	
	public synchronized int incValue(int clientID) { 
		int value = valueCalc; 
		System.out.println("Client with ID: " + clientID + " begins with status = " + value); 
		value++; 
		
		try { 
			Thread.sleep(50); 
			} 
		catch(Exception e) { 	
		} 
		
		valueCalc = value; 
		System.out.println("Client with ID: " + clientID + " ends with status = " + value); 
		return valueCalc; 
	} 
}