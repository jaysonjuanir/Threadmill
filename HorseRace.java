import java.util.concurrent.atomic.*;
import java.util.*;
import java.io.*;
public class HorseRace implements Runnable{
	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    	private static final int RANDOM_STRING_LENGTH = 1;
	static AtomicInteger ai = new AtomicInteger(0);
	HorseRace hr;
	private String name="";
	private int distance = 10;
	static int gate=100;
	private Object objectLock;
	static int finish=0;
	private int randomHealth;
	static List<String> rank = new ArrayList<>();
	
	public HorseRace( Object lock){
		objectLock=lock;
		randomHealth = ((int)(Math.random()*3)+1);
	}
	
	public static void setDistance(int distance){
		gate = distance;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return name;
	}
	public int getRandomHealth(){
		return randomHealth;
	}
	@Override
	public void run(){
		try{
			while(distance >0){
				int r = (int)(Math.random()*5)+1;
				System.out.println(name+" from Barn: " +(distance-=r) + " hops: " + r + " " +System.currentTimeMillis());
			}
			System.out.println("\t\t\t\t"+name + " is ready "+System.currentTimeMillis());
			synchronized(objectLock){
				finish++;
				if(finish==ai.get()){
					System.out.println("GO!\n" +System.currentTimeMillis());
					objectLock.notifyAll();
				}
				else{
					objectLock.wait();
				}
			}
			int gateDistance = gate;
			System.out.println();
			while(gateDistance >0){
				int r = (int)(Math.random()*5)+1;
				System.out.println(name+" from Gate: " +(gateDistance) + " hops: " + r + " remaining: " + (gateDistance-=r)+ " " +System.currentTimeMillis());
			}
			System.out.println("\t\t\t\t"+name + " finished " + System.currentTimeMillis());
		}catch(Exception ex){}
		finish++;
		
		rank.add(name);
		try{
			synchronized(objectLock){
				if(finish==(ai.get()*2)){
					//System.out.println("GO!\n" +System.currentTimeMillis());
					System.out.println("\t\t\t\t"+name + " finished " + System.currentTimeMillis() + " all horses was finished!");

					System.out.println ("Rankings!");
					rank.forEach(System.out::println);
					objectLock.notifyAll();
				}
				else{
					System.out.println("\t\t\t\t"+name + " finished " + System.currentTimeMillis() + " and will wait!");
					objectLock.wait();
	
				}
			}
		}
		catch(InterruptedException iex){
				iex.printStackTrace();
		}
	}
	
	
	public static void main(String [] args)throws IOException{
		Object lock = new Object();
		List<String> horses = new ArrayList<>();
		List<HorseRace> horses1 = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
		boolean input = true;
		int numHorse=0;
		while(input){
			try{
				System.out.println("Enter number of horses");
				numHorse = Integer.parseInt(br.readLine());
				input=false;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		while(!input){
			try{
				System.out.println("Enter distance");
				int meter = Integer.parseInt(br.readLine());
				setDistance(meter);
				input =true;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		String nameHorse = "";
		for(int x=1;x<=numHorse;x++){
			horses.add("");
		}
		horses.stream()
			.forEach((name)->{horses1.add(new HorseRace(lock));});
		System.out.println("List of Horses that were available: ");
		
		horses1.stream()
			.filter(health->health.getRandomHealth()!=1)
			.forEach(a->{
				a.setName(a.generateRandomString());
				System.out.println(ai.getAndIncrement()+1 +". "+a.getName());	
				new Thread(a).start();
			});

		rank.forEach(System.out::print);
	}
	public String generateRandomString(){
		StringBuffer randStr = new StringBuffer();
		for(int i=0; i<RANDOM_STRING_LENGTH; i++){
		    int number = getRandomNumber();
		    char ch = CHAR_LIST.charAt(number);
		    randStr.append(ch);
		}
		return randStr.toString();
	}
	private int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(CHAR_LIST.length());
		if (randomInt - 1 == -1) {
		    return randomInt;
		} else {
		    return randomInt - 1;
	        }
	}
}
