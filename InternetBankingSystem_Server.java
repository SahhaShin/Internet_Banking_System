package account;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

//Server

public class InternetBankingSystem_Server {
	InternetBankingSystem_Server(){
		//nothing
	}
	
	public void start() {
		//1. Open the server socket.
		ServerSocket serverSocket = null;
						
		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("\nserver say => server ready");
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
		while(true) {	
				
			try {
				//2. I keep waiting for the client's access. (=block)
				//2. If the connection is successful(=accept), a socket that knows the client's information(IP,PORT_NUM) is created.
				Socket socket = serverSocket.accept();
				System.out.println("\nserver say => "+"address : "+socket.getInetAddress()+" and port: "+socket.getPort()+" client connect success!");
				
				//11. Create and execute threads of Server Receiver objects.
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}//while
	}//start
	
	
	
	public static void main(String[] args) {
		
		new InternetBankingSystem_Server().start();
		
	}//main
	
	///////////ServerReceiver//////////////
	class ServerReceiver extends Thread{
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		ServerReceiver(Socket socket){
			
			try {
				//12. Create a stream to send messages from Server(=out) and read messages from Client(=in).
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				
			}catch(Exception e) {
				e.printStackTrace();
				
			}
		} //ServerReceiver(Socket socket)

		public void run() {
			try {
			
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				//13. When a message comes in through a socket connected to the client, it is read.
				//13. The message contains information.(work, name, other_name, balance)
				String message = "";
				message=in.readUTF();
				
				//14. The information is divided into commas and stored in each variable.
				String[] customers = message.split(",");		
				int work = 0;
				work=Integer.parseInt(customers[0]);
				String name = "";
				name=customers[1];
				int balance=0;
				balance=Integer.parseInt(customers[3]);
				String other_name = "";
				other_name=customers[2];				
				System.out.println("\nserver say => "+name+" Client information setting is complete.");
								
						
				//15. Open and read files with five customer information.
				//15. The file contains the name and the deposited money.
				HashMap<String, Integer> clients= new HashMap<String, Integer> ();
				String fileName="client_list.txt";
						
				try {
					Scanner inputStream=new Scanner(new File(fileName));
					//15. Store customer information in hashmap clients.
					for (int i=0;i<5;i++) {
						String[] info = (inputStream.nextLine()).split(" ");
						clients.put(info[0], Integer.parseInt(info[1]));
								
					}
					System.out.println(clients);
					inputStream.close();
						
					
					//16. Check if you have an account through name.
					int price = 0;
					int other_price=0;
					int flag=0; //0 : Normal / 1 : Abnormal.
					
					if(clients.containsKey(name)&&(work==1||work==2||work==3)){
						//16. In the case of work 1, 2, and 3, you only need to check yourself.
						System.out.println("\nserver say => "+name+" have an account.");

						price = clients.get(name);
					}
					else if(clients.containsKey(other_name)&&work==4) {
						//16. In the case of work 4, you have to check both yourself and the other party's information.
						System.out.println("\nserver say => sender "+name+" and receiver "+other_name+" have an account.");
						price = clients.get(name); 
						other_price = clients.get(other_name);
					}
					else {
						//17. If you don't have an account, send it to you.
						System.out.println("\nserver say => Account doesn’t exist So, disconnect!");
						out.writeUTF("\nserver say => Account doesn’t exist");
						flag=1;
						
								
					}
							
					//18. Each task is processed using threads.
					//The threadList contains threads currently being worked on.
					ArrayList<Thread> threadList = new ArrayList<Thread>();
			
					//transfer
					ClientInfo sender= new ClientInfo(name,price,balance,3);
					ClientInfo receiver= new ClientInfo(other_name,other_price,balance,2);
						
					//check
					ClientInfo who1= new ClientInfo(name,price,balance,work);
						
					//deposit
					ClientInfo who2 = new ClientInfo(name,price,balance,work);
							
					//withdraw
					ClientInfo who3 = new ClientInfo(name,price,balance,work);
					
					//18. (1. check == HerThread / 2. deposit == YourThread / 3. withdraw==MyThread / 4. transfer == YourThread + MyThread)
					if(work==4 && flag==0) {
						//18. If the sender tries to send more money than he has, Server refuses to work.
						if(balance>price) {
							System.out.println("\nserver say => sender "+name+" don't have enough money in your account.");
						}
						else {
							//18. In the case of work 4, my account proceeds with the withdraw work, and the other account proceeds with the deposit work. 
							Thread thread_sender = new Thread(new MyThread(sender));
							//Add a thread to the threadList
							threadList.add(thread_sender);
							
							
							Thread thread_receiver = new Thread(new YourThread(receiver));
							//Add a thread to the threadList
							threadList.add(thread_receiver);
							
							System.out.println("\nserver say => sender "+name+" and receiver "+other_name+" transfer start...");
							
							thread_sender.start();
							thread_receiver.start();
									
							//18. Wait for the threads to be processed.
							try {
								for(Thread t : threadList) {
									t.join();
								}
										
										
							}catch(Exception e) {
								System.out.println("\nserver say => thread join error!");
								e.printStackTrace();
							}
							
							//18. There has been a change in the deposited money of clients stored in the hashmap, so it is updated.
							clients.put(sender.your_name, sender.getPrice());
							clients.put(receiver.your_name, receiver.getPrice());
									
						}
								
					}
					else if(work==1&&flag==0){
						//check
						//18. In the case of work 1, check the deposited money and there is no change.
						Thread thread_check = new Thread(new HerThread(who1));
						System.out.println("\nserver say => "+name+" account check start...");
						thread_check.start();
					}
					else if(work==2&&flag==0){
						//deposit
						//18. In the case of work 2, money is deposited.
						Thread thread_check = new Thread(new YourThread(who2));
						//Add a thread to the threadList
						threadList.add(thread_check);
								
						System.out.println("\nserver say => "+name+" account deposit "+balance+"$ start...");
						thread_check.start();

						//18. Wait for the threads to be processed.
						try {
							for(Thread t : threadList) {
								t.join();
							}
									
									
						}catch(Exception e) {
							System.out.println("\nserver say => thread join error!");
							e.printStackTrace();
						}
						//18. There has been a change in the deposited money of clients stored in the hashmap, so it is updated.
						clients.put(who2.your_name, who2.getPrice());
						System.out.println("hashmap update ");
					}
					else if(work==3&&flag==0){
						//withdraw
						//18. In the case of work 3, money is deducted.
						Thread thread_check = new Thread(new MyThread(who3));
						//Add a thread to the threadList
						threadList.add(thread_check);
								
						System.out.println("\nserver say => "+name+" account withdraw "+balance+"$ start...");
						thread_check.start();
								
						//18. Wait for the threads to be processed.
						try {
							for(Thread t : threadList) {
								t.join();
							}
									
									
						}catch(Exception e) {
							System.out.println("\nserver say => thread join error!");
							e.printStackTrace();
						}
						//18. There has been a change in the deposited money of clients stored in the hashmap, so it is updated.
						clients.put(who3.your_name, who3.getPrice());
					}
					else {
								
					}
							
					//19. If there has been a change in the information of the clients, text file information is updated.
					if((work==2 || work==3 || work==4)&&flag==0) {
						System.out.println("\nserver say => File information update...");
						try {
							FileWriter fw = new FileWriter(fileName);
							PrintWriter printWriter = new PrintWriter(fw);
								
							for(Entry<String, Integer> elem : clients.entrySet()){ 
								System.out.println(elem.getKey() + " " + elem.getValue());
								printWriter.println(elem.getKey() + " " + elem.getValue()); 
							}
							printWriter.close();
						
									
						}catch(FileNotFoundException e) {
							System.out.println("\nserver say => Error opening the file "+fileName);
							System.exit(0);
						}
								
						System.out.println("\nserver say => File information update complete!");
					}
						
							
					////////////////////////////////////////////////////////////////
							
							
					//20. Send a result message to the client connected to the socket.
					if(work==4&&flag==0) {
						//transfer
						//20. If client send more money than he have, the server sends a message of rejection.
						if(balance>price) {
							out.writeUTF(name+" don't have enough money in your account.");
						}
						else {
							//20. In the case of transfer work, only the remaining money is sent to the result message.
							out.writeUTF("\ntransfer complete => "+name+" now has "+sender.getPrice()+"$");
						}
								
								
					}
					else if(work==1&&flag==0){
						//check
						out.writeUTF("\ncheck complete => "+name+" has "+who1.getPrice()+"$");
					}
					else if(work==2&&flag==0){
						//deposit
						out.writeUTF("\ndeposit complete => "+name+" now has "+who2.getPrice()+"$");
					}
					else if(work==3&&flag==0){
						//withdraw
						if(balance>price) {
							//20. If client send more money than he have, the server sends a message of rejection.
							out.writeUTF(name+" don't have enough money in your account.");
						}
						else {
							out.writeUTF("withdraw complete => "+name+" now has "+who3.getPrice()+"$");
						}
					}
					else {
						//nothing
					}
							

					} catch(FileNotFoundException e) {
						System.out.println("\nError opening the file "+fileName);
						System.exit(0);
					}

					
				}catch(IOException e) {
					System.out.println("\nServer Error : IOException");
					e.printStackTrace();
				}
		}
		
	}//ServerReceiver
	
}//class


//withdraw
class MyThread implements Runnable { 
	ClientInfo account;
	public MyThread (ClientInfo s) { 
		account = s;
	}
	
	public void run() { 
		account.withdraw(); 
	} 
} // end class MyThread

//deposit
class YourThread implements Runnable { 
	ClientInfo account;
	public YourThread (ClientInfo s) { account = s;}
	public void run() { 
		account.deposit(); 
		
	} 
} // end class YourThread

//check
class HerThread implements Runnable { 
	ClientInfo account;
	public HerThread (ClientInfo s) { 
		account = s; 
	}
	public void run() {
		account.check(); 
	} 
} // end class HerThread
