package account;

import java.io.*;
import java.net.*;
import java.util.*;

//Clients

public class InternetBankingSystem {

	public static void main(String[] args) {
		
		try {
			//3. Create a socket to make a socket on the server side. Attempt to access to the 9999 server socket.
			Socket socket = new Socket("127.0.0.1", 9999);
			System.out.println("\nConnection Success!");
			
			//4. Request work from the server.
			//4. At this time, the task is entered as a number.(1.check / 2.deposit / 3.withdraw / 4.transfer)
			System.out.println("\nPlease press the number of work you want.\n(1. check / 2. deposit / 3. withdraw / 4. transfer)");
			Scanner scanner = new Scanner(System.in);
			int work = scanner.nextInt();
			//4. If it is not a number between 1 and 4, please input it again.
			while(work<1||work>4) {
				System.out.println("\nThe work is wrong. (Please enter a number between 1 and 4)");
				work = scanner.nextInt();
			}
			
			
			int balance=0;
			String other_name ="XXX";
			
			//5. In the case of deposit and withdraw, you receive how much you want to insert and subtract.
			//5. After that, it is stored in a variable called balance.
			if(work==2 || work==3) {
				System.out.println("\nHow much do you want?(deposit or withdraw)");
				balance = scanner.nextInt(); // work == 원하는 작업 
			}
			else if(work==4) {
				//6. In the case of transfer, you receive how much you will send(=balance) to the other party(=other_name).
				System.out.println("\nWho would you send it to?");
				other_name = scanner.next();
				System.out.println("\nHow much would you like to send?");
				balance = scanner.nextInt();

			}
			//7. The user's name is input.
			scanner.nextLine();
			System.out.println("\nPlease enter your name.");
			String name = scanner.nextLine();
			
			/////////////////////////////////////////////////
			
			//8. The task is performed by creating a thread for each client.
			Thread sender_receiver = new Thread(new ClientSender(socket,work,name,other_name,balance));
			sender_receiver.start();

		}catch(IOException e) {
			e.printStackTrace();
		}
	
	}//main
	
	//////////Clients thread/////////////////
	static class ClientSender extends Thread{
		Socket socket;
		DataOutputStream out;
		DataInputStream in;
		String name;
		String other_name;
		int work;
		int balance;
	
		ClientSender(Socket socket, int work, String name, String other_name,int balance){
			this.socket=socket;
			this.name=name;
			this.work=work;
			this.balance=balance;
			this.other_name=other_name;
			
			try {
				//9. Create a stream to send messages from clients(=out) and read messages from servers(=in).
				out=new DataOutputStream(socket.getOutputStream());
				in=new DataInputStream(socket.getInputStream());
			}
			catch(Exception e){
				System.out.println("\n!!!!!DataInputStream DataOutputStream create error!!!!!");
				e.printStackTrace();
			}
		}
		public void run(){
			//10. Send a message to the server. (work, name, other_name, balance)
			String message="";
			try {
				out.writeUTF(work+","+name+","+other_name+","+balance);
				
				//서버메세지를 기다린다.
				while(in != null) {
					try {
						message=in.readUTF();
						if(message!=null) {
							System.out.println(message);
							break;
						}
						
						
						
					}catch(Exception e){
						System.out.println("\n!!!!!DataInputStream read error!!!!!");
						e.printStackTrace();
					}
				}
				
			
				try {
					in.close();
					out.close();
					socket.close();
				
				}catch(Exception e){
					System.out.println("\n!!!!!DataInputStream & socket close error!!!!!");
					e.printStackTrace();
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}	
			
	}//ClientSender
}