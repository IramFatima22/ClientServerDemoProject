import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LibServer {

	private static final int PORT_NUMBER = 4446;

	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String USER_ID = "USER_ID";
	private static final String USER_PASSWORD = "USER_PASSWORD";
	private static final String LAST_NAME = "LAST_NAME";

	private static final String REQUEST_TYPE = "REQUEST_TYPE";
	private static final String LOGIN_REQUEST_TYPE = "LOGIN";
	private static final String REGISTER_REQUEST_TYPE = "REGISTER";
	private static final String ADD_TO_CART_REQ_TYPE = "ADDTOCART";
	private static final String REMOVE_FROM_BOOK_REQ_TYPE = "REMOVEFROMBOOK";
	private static final String PLACE_BOOK_REQ_TYPE = "PLACEBOOK";
	private static final String EXIT_REQ_TYPE = "EXIT";
	private static final String DATA_FIELD = "DATA";
	private static final String DATA_FIELD2 = "DATA2";

	private static final String SLOT_ID = "ID";
	private static final String ITEM_NAME = "NAME";
	private static final String ITEM_STOCK = "STOCK";
	private static final String BOOK_IND = "BOOKIND";
	private static final String DAY_ID = "DAYID";
	private static final String REPORT1_REQUEST_TYPE = "REPORT1REQTYPE";
	private static final String REPORT2_REQUEST_TYPE = "REPORT2REQTYPE";
	private static final String REPORT3_REQUEST_TYPE = "REPORT3REQTYPE";
	private static final String FETCH_SLOT_REQUEST_TYPE_ADMIN = "REPORT3REQTYPE";
	private static final String AUTH_TOKEN = "AUTH_TOKEN";

	private static final String AUTH_DATA = "AUTH_DATA";
	private static final String AUTH_FAIL_CODE = "AUTH_FAIL_CODE";
	private static final String FETCH_SLOT_REQUEST_TYPE = "FETCH_INV";
	
	private static final String ADD_TO_CART_STTAUS = "ADD_TO_CART_STATUS";
	private static final String REMOVE_FROM_CART_STTAUS = "REMOVE_FROM_CART_STATUS";	

	private static int noOfClients = 1;
	private static List<HashMap<String,String>> usersList = new ArrayList<HashMap<String,String>>(); //list of all users
	private static List<HashMap<String,String>> authUsersList = new ArrayList<HashMap<String,String>>(); //list of all auth users
	private static HashMap<String,HashMap<String,String>> userCartMap = new HashMap<String,HashMap<String,String>>(); //list of all auth users
	
	private static Map<String,String> itemArray[] = new HashMap[8];
	private HashMap<String,Object> tempItem= new HashMap<String,Object>();
	private HashMap<String,Object> bookedItem= new HashMap<String,Object>();
	private HashMap<String,Object> userBookedItem= new HashMap<String,Object>();
	//private HashMap<String,String> item1= new HashMap<String,String>();
	//private HashMap<String,String> item2= new HashMap<String,String>();
	
	public static void main(String arg[]){

		System.out.println("Server Listening......");

		Socket socket=null;
		ServerSocket serverSocket=null;		
		LibServer serv = new LibServer();

		//initialize dummy users
		serv.initializeUsers();

		//initialize items
		System.out.println("calling initialize items");
		serv.initializeItems();

		//Thread to read the command and display inventory		
		/*new Thread(){			
			public void run(){
				System.out.println("Please enter the command:");
				Scanner scan = new Scanner(System.in);
				String temp = null;
				while((temp = scan.nextLine()) != null){
					if(temp.equals("display inventory")){
						serv.displayInventory();
					}
				}
			}			
		}.start();*/		

		//Start server
		try{
			serverSocket = new ServerSocket(PORT_NUMBER);
			
			while(true){
				System.out.println("waiting to accept");
				socket= serverSocket.accept();
				System.out.println("accepted");
				//System.out.println("connection Established");
				ServerThread1 st=serv.new ServerThread1(socket,noOfClients++);
				st.start();
				//Thread.sleep(3000);
			}			
		}
		catch(Exception e){			
			e.printStackTrace();
			System.out.println("Server error -->"+e.getMessage());
		}	
	}

	/**
	 * This method gets all the inventory in the server
	 */
	private HashMap<String, Object> fetchAllInventory(){
		//return itemArray;
		System.out.println("in fetch all inv l");
		return tempItem;
	}
	private HashMap<String, Object> fetchBookedInventory(){
		//return itemArray;
		System.out.println("in booked all inv");
		System.out.println(bookedItem);
		System.out.println(userBookedItem);
		return userBookedItem;
	}

	/**
	 * This method is used to display the inventory
	 */
	/*private void displayInventory(){		

		System.out.println("-------------Inventory Details-------------\n");
		System.out.println("SLOT_ID\t\tITEM NAME\tSTOCK");

		for(int i=0; i<itemArray.length; i++){			
			HashMap<String, String> temp = (HashMap)itemArray[i];
			System.out.println(temp.get(SLOT_ID)+"\t\t"+
					temp.get(ITEM_NAME)+"\t\t"+
					Integer.parseInt(temp.get(ITEM_STOCK)));			
		}		
	}*/

	/**
	 * This method is used to initialize a dummy set of users
	 */
	private void initializeUsers() {				
		for(int i=1; i<6; i++){
			usersList.add(createUser("user"+i, "user"+i+"@uic.edu", 
					"user"+i, "Seetam"));			
		}
		usersList.add(createUser("admin", "admin", "admin123", "admin"));
		System.out.println(usersList);
	}	

	/**
	 * This method is used to create a user	 
	 */
	private HashMap<String,String> createUser(String firstName, String userId,String password, String lastName){

		HashMap<String,String> userMap = new HashMap<String,String>();		
		userMap.put(FIRST_NAME, firstName);
		userMap.put(USER_ID, userId);
		userMap.put(USER_PASSWORD, password);
		userMap.put(LAST_NAME, lastName);		

		return userMap;
	}

	/**
	 * This method is used to initialize items
	 */
	private void initializeItems() {
		//HashMap<String,Object> tempItem= new HashMap<String,Object>();
		for(int j=1; j<=31; j++){				
			HashMap<String,String> item1 = new HashMap<String,String>();
			//item1 = new HashMap<String,String>();
			for(int i=0; i<8; i++){					
				/*//HashMap<String,String> tempItem= new HashMap<String,String>();
				item1.put(SLOT_ID, (i+1)+"");
				//tempItem.put(ITEM_NAME, "Item"+(i+1));
				if(i<1){
					item1.put(ITEM_NAME,"0"+(i+9)+"00-"+(i+10)+"00");
				}
				else{
					item1.put(ITEM_NAME,(i+9)+"00-"+(i+10)+"00");
				}
				//tempItem.put(ITEM_NAME,(i+9)+"-"+(i+10)+"");
				item1.put(ITEM_STOCK,"N");
				itemArray[i] = item1;*/
				
				item1.put(String.valueOf(i),"N");
			}
			tempItem.put(String.valueOf(j),item1);
			System.out.println(tempItem);
		}
	}

	class ServerThread1 extends Thread{  

		private Socket socket;
		private int clientNo;
		
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;

		public ServerThread1(Socket socket, int clientNo){
			this.socket=socket;
			this.clientNo=clientNo;
		}

		@Override
		public void run() {
			
			try{
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
										
				Object tempObj = null;
				
				while((tempObj = ois.readObject()) != null){
					System.out.println("in here man");
					Map<String, Object> inputMap = (Map<String,Object>) tempObj;
					String requestType = (String)inputMap.get(REQUEST_TYPE);			
					Map<String,String> dataMap = (Map<String,String>)inputMap.get(DATA_FIELD);
					System.out.println(dataMap);
					
					System.out.println("check 2");
					System.out.println(requestType);
					//Login request
					if(requestType.equals(LOGIN_REQUEST_TYPE)){
						handleLoginRequest(requestType,dataMap);
					}
					
					//Registration request
					else if(requestType.equals(REGISTER_REQUEST_TYPE)){					
						oos.writeObject(new String(registerUser(dataMap)));
						oos.flush();
					}
					
				/*	//Add to cart request
					else if(requestType.equals(ADD_TO_CART_REQ_TYPE)){						
						//always verify auth info. expecxted to be sent from the client
						if(inputMap.get(AUTH_DATA) != null 
								&& verifyAuthInfo((HashMap<String,String>)inputMap.get(AUTH_DATA))){
							addToCart((HashMap<String, String>)inputMap.get(AUTH_DATA), 
									(HashMap<String, String>)dataMap);
						}						
					}*/
					
					//Remove from cart request
					else if(requestType.equals(REMOVE_FROM_BOOK_REQ_TYPE)){						
						//always verify auth info. expecxted to be sent from the client
						if(inputMap.get(AUTH_DATA) != null 
								&& verifyAuthInfo((HashMap<String,String>)inputMap.get(AUTH_DATA))){
							
							System.out.println("before remove from book");
							removeFromBook((HashMap<String, String>)inputMap.get(AUTH_DATA), 
									(HashMap<String, String>)dataMap);
						}						
					}
					
					//place order request
					else if(requestType.equals(PLACE_BOOK_REQ_TYPE)){						
						//always verify auth info. expecxted to be sent from the client
						if(inputMap.get(AUTH_DATA) != null 
								&& verifyAuthInfo((HashMap<String,String>)inputMap.get(AUTH_DATA))){
							placeBook((HashMap<String, String>)inputMap.get(AUTH_DATA), 
									(HashMap<String, String>)dataMap);
						}						
					}
					
					else if(requestType.equals(REPORT1_REQUEST_TYPE)){						
						//always verify auth info. expecxted to be sent from the client
						if(inputMap.get(AUTH_DATA) != null 
								&& verifyAuthInfo((HashMap<String,String>)inputMap.get(AUTH_DATA))){
							System.out.println("checking req type in");
							genReport1((HashMap<String, String>)inputMap.get(AUTH_DATA), 
									(HashMap<String, String>)dataMap);
						}						
					}
					
					//fetch inventory request
					else if(requestType.equals(FETCH_SLOT_REQUEST_TYPE)){					
						System.out.println("slot req 2");
						if(inputMap.get(AUTH_DATA) != null 
								&& verifyAuthInfo((HashMap<String,String>)inputMap.get(AUTH_DATA))){
							
							System.out.println("passed");
							HashMap<String,Object> invData = new HashMap<String,Object>();
							invData.put(AUTH_DATA, inputMap.get(AUTH_DATA));
							invData.put(REQUEST_TYPE, FETCH_SLOT_REQUEST_TYPE);
							System.out.println("going to fetch inv -");
							//invData.put(DATA_FIELD, new HashMap<String, Object>(tempItem));
							invData.put(DATA_FIELD, fetchAllInventory());
							invData.put(DATA_FIELD2, fetchBookedInventory());
							
							System.out.println(tempItem);
							System.out.println("inv data :"+invData);
							oos.reset();
							oos.writeObject(invData);
							oos.flush();							
						}
						
					}
					
					//exit request type
					else if(requestType.equals(EXIT_REQ_TYPE)){
						
						HashMap<String,String> authData = (HashMap<String,String>)inputMap.get(AUTH_DATA);
						
						//removed the user from the authorized user list
						for(HashMap<String,String> userMap: authUsersList){
							if(userMap.get(USER_ID).equals(authData.get(USER_ID))){
								authUsersList.remove(userMap);
								break;
							}
						}
						
						break;
					}
				}
				
				ois.close();
				oos.close();
				socket.close();
				
			}catch(Exception e){
				System.out.println("Exception while handling client request -->"+e.getMessage());
				e.printStackTrace();
			}
		}
		
		/**
		 * This method is used to place order
		 */
	
		
		private void genReport1(HashMap<String, String> authData, 
				HashMap<String, String> dataMap) throws Exception{
			System.out.println("gen report 1 - service");
			System.out.println(userBookedItem);			
			oos.writeObject(userBookedItem);
			oos.flush();
		}
		

		/**
		 * This method handles the remove request from client
		 */
		private void removeFromBook(HashMap<String, String> authData, 
				HashMap<String, String> dataMap) throws Exception{
			System.out.println("in remove from book");
			HashMap<String, Object> outputMap = new HashMap<String,Object>();
			outputMap.put(REQUEST_TYPE, REMOVE_FROM_BOOK_REQ_TYPE);
			String userId = authData.get(USER_ID); 
			String slotId = dataMap.get(SLOT_ID);
			String dayId = dataMap.get(DAY_ID);
			
			//check if the item is already added for this user in the server
			//delete this item for this user
			/*System.out.println("cart stuff");
			if(userCartMap.get(userId) != null){
				HashMap<String, String> tempDataMap = 
						(HashMap<String,String>)userCartMap.get(userId);
				if(tempDataMap.get(SLOT_ID).equals(slotId)){
					tempDataMap.remove(SLOT_ID);
				}
			}*/
			
			System.out.println("remove time");
			HashMap<String, String> daySlotMap = (HashMap<String, String>) tempItem.get(dayId); 
			daySlotMap.put(slotId, "N");			
			tempItem.put(dayId, daySlotMap);
			
			if(userBookedItem.get(userId) != null){
				System.out.println(userId);
				System.out.println("selected day for removal "+dayId);
				System.out.println(slotId);
							
				HashMap<String, Object> tempDayBook = null;
				HashMap<String, String> tempSlotBook = null;				
				
				System.out.println(userBookedItem);
				tempDayBook = (HashMap<String, Object>) userBookedItem.get(userId);
				System.out.println(tempDayBook);
				tempSlotBook = (HashMap<String, String>) tempDayBook.get(dayId);
				System.out.println(tempSlotBook);
				tempSlotBook.put(slotId,"N");
				tempDayBook.put(dayId, tempSlotBook);
				userBookedItem.put(userId, tempDayBook);
				System.out.println("user booked : "+userBookedItem);
			}
			
			//bookedItem.remove(dayId);
			//removed from the main array
			System.out.println("done removing");
			outputMap.put(REMOVE_FROM_CART_STTAUS, 1);
			
			oos.writeObject(outputMap);
			oos.flush();
		}

		/**
		 * This method is used to add an item to Cart	   
		 */
		private void placeBook(HashMap<String, String> authData, 
				HashMap<String, String> dataMap) throws Exception{
				
			HashMap<String, Object> outputMap = new HashMap<String,Object>();
			outputMap.put(REQUEST_TYPE, PLACE_BOOK_REQ_TYPE);
			
			String userId = authData.get(USER_ID); 
			String slotId = dataMap.get(SLOT_ID);
			String dayId = dataMap.get(DAY_ID);
			String bookInd = dataMap.get(BOOK_IND);
			System.out.println("new book ind : "+bookInd);
			System.out.println(tempItem);
			System.out.println(slotId);
			System.out.println(dayId);
			System.out.println(userId);
			System.out.println(bookInd);
			//int stockToBeAdded = Integer.parseInt(dataMap.get(ITEM_STOCK));
			//int invStock = 0;
			//int alreadyAddedStock = 0;
			//boolean alreadyAdded = false;
			
			//get available stock for this item in original inventory
			if(tempItem != null){
				HashMap<String, String> daySlotMap = (HashMap<String, String>) tempItem.get(dayId);
				daySlotMap.put(slotId, bookInd);
				tempItem.put(dayId, daySlotMap);
				
				
				//to be chekced
		/*		HashMap<String, Object> bookedItemLocal = null;
				
				if(userBookedItem.get(userId) != null){
					bookedItemLocal = (HashMap<String, Object>)userBookedItem.get(userId);
				}else{
					bookedItemLocal = new HashMap<String, Object>();
				}
								 
				bookedItemLocal.put(dayId, daySlotMap);
				userBookedItem.put(userId, bookedItemLocal);
				System.out.println(userBookedItem);*/
				
				///
				
				HashMap<String, String> tempBookMap = null;
				HashMap<String, Object> tempUserBook = null;
				
				System.out.println(userBookedItem);
				System.out.println("entering");
				HashMap<String, String> item1 = new HashMap<String,String>();
				HashMap<String, Object> item2 = new HashMap<String,Object>();
				if(userBookedItem.get(userId) == null){
	
						for(int i=0; i<8; i++){													
							item1.put(String.valueOf(i),"N");
						}
						item2.put(dayId,item1);
						System.out.println(item2);
						userBookedItem.put(userId, item2);
						System.out.println("new userbooked item in place book : "+userBookedItem);
				}
				else{
					HashMap<String, Object> item3 = (HashMap<String, Object>) userBookedItem.get(userId);
					System.out.println(item3);
					if(item3.get(dayId) == null){
						for(int i=0; i<8; i++){													
							item1.put(String.valueOf(i),"N");
						}
						item3.put(dayId,item1);
						System.out.println(item3);
						userBookedItem.put(userId, item3);
						System.out.println("new day item in place book : "+userBookedItem);
					}
				}
				
				
				for (Map.Entry<String, Object> en : userBookedItem.entrySet()){
					if(en.getKey().equals(userId)){
						String userId1 = en.getKey();
						System.out.println(en.getKey());					
						System.out.println("user id in map found : "+userId);
						tempUserBook = (HashMap<String, Object>) en.getValue();
						System.out.println(tempUserBook);
						for (Map.Entry<String, Object> entry : tempUserBook.entrySet()){
							if(entry.getKey().equals(dayId)){
								tempBookMap = (HashMap<String, String>) entry.getValue();
								tempBookMap.put(slotId, "Y");
								System.out.println(tempBookMap);
								tempUserBook.put(dayId, tempBookMap);
								System.out.println(tempUserBook);
							}							
							/*tempBookMap = (HashMap<String, String>) entry.getValue();
							System.out.println(tempBookMap);*/							
							//tempBookMap.put(slotId, value)
							/*for (Map.Entry<String, String> entry1 : tempBookMap.entrySet())
							{
								System.out.println(entry1);
								//cartTableModel.addRow(new Object[]{entry1.getKey(),CONST_SLOTS[Integer.parseInt(entry1.getKey())],day1});
								if(entry1.getValue().equals("Y")){
									//cartTableModel.addRow(new Object[]{entry1.getKey(),CONST_SLOTS[Integer.parseInt(entry1.getKey())],entry.getKey()});
									tempBookMap.put(slotId, "Y");
								}
								//p++;
							}*/							
						}
						
					}
					
				}
				userBookedItem.put(userId, tempUserBook);
				System.out.println(userBookedItem);
				///
			}
						
			oos.writeObject(outputMap);
			oos.flush();
		}
		
		/**
		 * This method is used to handle the login request  
		 */
		private void handleLoginRequest(String requestType, 
				Map<String, String> dataMap) throws Exception{
			
			//prepare output
			HashMap<String, Object> outputMap = new HashMap<String,Object>();
			outputMap.put(REQUEST_TYPE, requestType);
			
			//validate user
			int valResult = validateUser(dataMap);
			
			//validation successful
			if(valResult == 0){							
				outputMap.put(AUTH_DATA, generateAuthToken(dataMap));							
			}
			
			//email id and pwd empty
			else if(valResult == -1 || valResult == -3){
				outputMap.put(AUTH_FAIL_CODE, new String("-1"));
			}
			
			//user already logged in
			else if(valResult == -2){
				outputMap.put(AUTH_FAIL_CODE, new String("-2"));
			}
			
			//write the output
			oos.writeObject(outputMap);
			oos.flush();		
		}

		/**		  
		 * This method validates the client by checking the email id and password
		 */
		private int validateUser(Map<String,String> dataMap){			
			String user_id2 = null;
			//if the input data doesnt have email id and password, return false.
			if(dataMap.get(USER_ID) == null){
				return -1;
			}
			
			if(dataMap.get(USER_PASSWORD) == null){
				return -4;
			}
			
			//Check if the user is already logged in
			for(int i=0; i<authUsersList.size(); i++){
				Map<String,String> tempMap = (Map<String,String>)authUsersList.get(i);
				if(tempMap.get(USER_ID).equals(dataMap.get(USER_ID))){
					return -2;
				}
			}

			//iterate through the user list and check if the email id and password matches.
			for(int i=0; i<usersList.size(); i++){
				Map<String,String> tempMap = (Map<String,String>)usersList.get(i);
				System.out.println("user list : "+tempMap);
				if(tempMap.get(USER_ID).equals(dataMap.get(USER_ID))
						&& tempMap.get(USER_PASSWORD).equals(dataMap.get(USER_PASSWORD))){
					return 0;
				}				
			}

			return -3;
		}

		/**
		 * This method is used to register the user.
		 */
		private String registerUser(Map<String,String> dataMap){

			System.out.println("Registering user..");

			//if the input data doesnt have email id and password, return false.
			if(dataMap.get(USER_ID) == null || dataMap.get(USER_PASSWORD) == null){
				return "-1";
			}
			
			if((dataMap.get(USER_ID).length() < 6) || (dataMap.get(USER_ID).length() > 12)){
				return "-2";
			}
			
			if((dataMap.get(USER_PASSWORD).length() < 6) || (dataMap.get(USER_PASSWORD).length() > 12)){
				return "-3";
			}

			for(int i=0; i<usersList.size(); i++){
				Map<String,String> tempMap = (Map<String,String>)usersList.get(i);
				if(tempMap.get(USER_ID).equals(dataMap.get(USER_ID))){
					return "-4";
				}				
			}
			
			
			
			//add to the user list
			usersList.add(createUser(dataMap.get(FIRST_NAME), dataMap.get(USER_ID), 
					dataMap.get(USER_PASSWORD), dataMap.get(LAST_NAME)));

			return "0";
		}
	}

	/**
	 * This method is used to generate authentication token 
	 */
	public HashMap<String,String> generateAuthToken(Map<String, String> dataMap) {
		HashMap<String,String> authMap = new HashMap<String,String>();
		authMap.put(USER_ID, dataMap.get(USER_ID));
		authMap.put(AUTH_TOKEN, String.valueOf(System.currentTimeMillis()));
		authUsersList.add(authMap);
		return authMap;
	}

	/**
	 * This method verifies authentication info 
	 */
	public boolean verifyAuthInfo(HashMap<String,String> authMap){

		if(authMap.get(USER_ID) != null 
				&& authMap.get(AUTH_TOKEN) != null){

			for(HashMap<String, String> tempMap: authUsersList){				
				if(tempMap.get(USER_ID) != null 
						&& tempMap.get(USER_ID).equals(authMap.get(USER_ID)) 
						&& tempMap.get(AUTH_TOKEN) != null
						&& tempMap.get(AUTH_TOKEN).equals(authMap.get(AUTH_TOKEN))){
					return true;
				}				
			}			
		}		
		return false;
	}
}

