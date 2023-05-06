package account;

//processing through the work.
public class ClientInfo extends InternetBankingSystem_Server {
	String your_name;
	int price;
	int balance;
	int work;
	
	ClientInfo(){
		//nothing
	}
	ClientInfo(String your_name, int price, int balance, int work){
		this.your_name=your_name;
		this.price=price;
		this.balance=balance;
		this.work=work;

	}
	
	
	public synchronized void deposit() {
		price+=balance;
		System.out.println(your_name+" \"deposit\" is complete.");
		
	}
	
	public synchronized void withdraw() {
		//가진 돈보다 더 많이 뺸다면 안된다.
		if(balance>price) {
			System.out.println(your_name+" don't have enough money in your account.");
		}
		else {
			price-=balance;
			System.out.println(your_name+" \"withdraw\" is complete.");
		}
		
	}
	public synchronized void check() {
		System.out.println(your_name+" \"check\" is complete.");
	}
	
	//getter
	int getPrice() {
		return price;
	}
	

}