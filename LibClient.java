import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class LibClient extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8685645706771120199L;

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
	private static final String REPORT1_REQUEST_TYPE = "REPORT1REQTYPE";
	private static final String REPORT2_REQUEST_TYPE = "REPORT2REQTYPE";
	private static final String REPORT3_REQUEST_TYPE = "REPORT3REQTYPE";
	private static final String FETCH_SLOT_REQUEST_TYPE_ADMIN = "REPORT3REQTYPE";

	private static final String SLOT_ID = "ID";
	private static final String BOOK_IND = "BOOKIND";
	private static final String ITEM_STOCK = "STOCK";
	private static final String DAY_ID = "DAYID";

	private static final String AUTH_DATA = "AUTH_DATA";
	private static final String AUTH_FAIL_CODE = "AUTH_FAIL_CODE";
	private static final String FETCH_SLOT_REQUEST_TYPE = "FETCH_INV";
	private static final String ADD_TO_CART_STTAUS = "ADD_TO_CART_STATUS";
	private static final String REMOVE_FROM_CART_STTAUS = "REMOVE_FROM_CART_STATUS";	
	private static final String[] CONST_SLOTS = {"0900-1000","1000-1100","1100-1200","1200-1300","1300-1400","1400-1500","1500-1600","1600-1700"};

	private JTextField userIdField = null; 
	private JPasswordField userPwdField = null;
	private JPasswordField userConfPwdField = null;
	private JTextField lastNameField = null;
	private JTextField firstNameField = null;

	private JTextField loginEmail = null;
	private JPasswordField loginPwd = null;

	private static Socket socket = null;
	private static ObjectInputStream is = null;
	private static ObjectOutputStream os = null;

	private static HashMap<String, Object> authMap = null; //Required to be sent with each request after login

	private JTable invTable = null;
	private DefaultTableModel tableModel = null;
	private DefaultTableModel cartTableModel = null;
	private String slotValue = null;
	private String slotValueAdmin = null;
	private String userValueAdmin = null;
	
	private JTable report1Table = null;
	private JTable report2Table = null;
	private JTable report3Table = null;
	private DefaultTableModel adminReport1Model = null;
	private DefaultTableModel adminReport2Model = null;
	private DefaultTableModel adminReport3Model = null;

	public static void main(String arg[]){

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				LibClient client = new LibClient();		
				client.setSize(700, 700);
				client.createLoginPanel(client.getContentPane());
				client.setLocationRelativeTo(null); //center the window
				client.setVisible(true);
				client.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				client.setResizable(false);

				//add a window close listener
				client.addWindowListener(new WindowAdapter() {					
					@Override
					public void windowClosing(WindowEvent e) {
						try{
							HashMap<String,Object> inMap = new HashMap<String,Object>();
							inMap.put(REQUEST_TYPE, EXIT_REQ_TYPE);
							inMap.put(AUTH_DATA, authMap);
							inMap.put(DATA_FIELD, new HashMap<String,String>());
							os.writeObject(inMap);
							os.flush();
							os.close();
							is.close();
							socket.close();
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				});

				try{	
					socket = new Socket("localhost", 4446);
					os = new ObjectOutputStream(socket.getOutputStream());
					is = new ObjectInputStream(socket.getInputStream());

				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Exception :: "+ex.getMessage());
				}				
			}
		});		
	}

	/**
	 * This method is used to create the login panel
	 */
	private void createLoginPanel(Container cont){
		// Setting up JFrame
		setLayout(null);
		setTitle("Library User Registration/Login");

		//Adding objects on the frame
		JLabel user_req_form = new JLabel("Registration Form");
		user_req_form.setForeground(Color.blue);
		user_req_form.setFont(new Font("Serif", Font.BOLD, 20));


		JLabel user_id = new JLabel("User ID:");
		JLabel cr_password = new JLabel("Create Password:");
		JLabel cn_password = new JLabel("Confirm Password:");
		JLabel first_name = new JLabel("First Name:");
		JLabel last_name = new JLabel("Last Name:");

		userIdField = new JTextField();
		userPwdField = new JPasswordField();
		userConfPwdField = new JPasswordField();
		lastNameField = new JTextField();
		firstNameField = new JTextField();

		JButton submit = new JButton("Submit");
		JButton clear = new JButton("Clear");

		submit.addActionListener(this);
		clear.addActionListener(this);

		user_req_form.setBounds(100, 30, 400, 30);

		// Setting object size and position
		user_id.setBounds(80, 70, 200, 30);
		first_name.setBounds(80, 110, 200, 30);
		last_name.setBounds(80, 150, 200, 30);
		cr_password.setBounds(80, 190, 200, 30);
		cn_password.setBounds(80, 230, 200, 30);

		userIdField.setBounds(300, 70, 200, 30);
		firstNameField.setBounds(300, 110, 200, 30);
		lastNameField.setBounds(300, 150, 200, 30);
		userPwdField.setBounds(300, 190, 200, 30);
		userConfPwdField.setBounds(300, 230, 200, 30);

		submit.setBounds(170, 270, 100, 30);
		clear.setBounds(310, 270, 100, 30);

		cont.add(user_req_form);       
		cont.add(user_id);
		cont.add(userIdField);
		cont.add(first_name);
		cont.add(firstNameField);
		cont.add(last_name);
		cont.add(lastNameField);
		cont.add(cr_password);
		cont.add(userPwdField);
		cont.add(cn_password);
		cont.add(userConfPwdField);
		cont.add(submit);
		cont.add(clear);

		JLabel account_already = new JLabel("Already have an account?");
		JLabel login = new JLabel("Login Form");
		login.setForeground(Color.blue);
		login.setFont(new Font("Serif", Font.BOLD, 20));

		JLabel email_id1 = new JLabel("User ID:");
		JLabel password1 = new JLabel("Password:");

		loginEmail = new JTextField();
		loginPwd = new JPasswordField();

		JButton login_btn = new JButton("Login");
		JButton clear1 = new JButton("Clear");

		login_btn.addActionListener(this);
		clear1.addActionListener(this);
		
		account_already.setBounds(100, 400, 400, 30);
		login.setBounds(100, 430, 400, 30);
		email_id1.setBounds(80, 460, 400, 30);
		password1.setBounds(80, 500, 400, 30);
		loginEmail.setBounds(300, 460, 200, 30);
		loginPwd.setBounds(300, 500, 200, 30);
		login_btn.setBounds(170, 540, 100, 30);
		clear1.setBounds(310, 540, 100, 30);

		cont.add(account_already);
		cont.add(login);
		cont.add(email_id1);
		cont.add(loginEmail);
		cont.add(password1);
		cont.add(loginPwd);
		cont.add(login_btn);
		cont.add(clear1);		
	}

	/**
	 * This method is used to create the inventory panel
	 */

	static String[] addElement(String[] a, String e) {
		a  = Arrays.copyOf(a, a.length + 1);
		a[a.length - 1] = e;
		return a;
	}
	
	private void createAdminPanel(){
		//create header
		JLabel headerLabel = new JLabel("Booking List");
		headerLabel.setFont(new Font("Serif", Font.BOLD, 16));	
		headerLabel.setForeground(Color.BLUE);
		//remove whatever is present
		getContentPane().removeAll(); 
		getContentPane().revalidate();
		getContentPane().repaint();
		//create header
		JButton userList = new JButton("User List");
		JButton dayReservations = new JButton("Monthly Status");
		JButton userRegisteration = new JButton("User Registeration");
		
		userList.setBounds(120, 30, 400, 100);
		dayReservations.setBounds(120, 250, 400, 100);
		userRegisteration.setBounds(120, 140, 400, 100);
		
		getContentPane().add(userList);
		getContentPane().add(dayReservations);
		getContentPane().add(userRegisteration);
		getContentPane().setVisible(true);
		
		userList.addActionListener(this);
		dayReservations.addActionListener(this);
		userRegisteration.addActionListener(this);
		
		
	}
	
	
	private void createReport1(){
		//remove whatever is present
		getContentPane().removeAll(); 
		getContentPane().revalidate();
		getContentPane().repaint();
		
		//create header
		JLabel headerLabel = new JLabel("List of Reservations");
		headerLabel.setFont(new Font("Serif", Font.BOLD, 16));	
		headerLabel.setForeground(Color.BLUE);
		
		JButton backBtn = new JButton("Back");
		
		adminReport1Model = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		adminReport1Model.setColumnIdentifiers(new Object[]{"User ID","Time Slot","Oct-(Day)"});		
		report1Table = new JTable(adminReport1Model);
		report1Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollPanel1 = new JScrollPane(report1Table);
		
		headerLabel.setBounds(120, 30, 400, 20);
		scrollPanel1.setBounds(120, 100, 400, 200);
		backBtn.setBounds(120, 500, 120, 20);
		
		getContentPane().add(scrollPanel1);
		getContentPane().add(backBtn);
		getContentPane().add(headerLabel);
		
		backBtn.addActionListener(this); 
	}

	// For report 2 start 
	
	private void createReport2(){
		//remove whatever is present
		getContentPane().removeAll(); 
		getContentPane().revalidate();
		getContentPane().repaint();

		//create header
		JLabel headerLabel = new JLabel("Per day booking list");
		headerLabel.setFont(new Font("Serif", Font.BOLD, 16));	
		headerLabel.setForeground(Color.BLUE);

		//create table
		
		adminReport2Model = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		adminReport2Model.setColumnIdentifiers(new Object[]{"Slot Id","Time Slot","Booked?"});		
		report2Table = new JTable(adminReport2Model);
		report2Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollPanel = new JScrollPane(report2Table);

		//create dropdown for days
		JLabel instLabel = new JLabel("Select a day");
		

		//create a dropdown

		String days[] = {"1","2"};
		for(int i=3;i<=31;i++){
			days = addElement(days,String.valueOf(i));
		}
		JComboBox cb=new JComboBox(days);

		//Get list of time slots
		JButton getSlotsBtnAdmin = new JButton("Get Slots");
		JButton backBtn = new JButton("Back");
		
		//mailGuy.setBounds(250,60,400,20);
		headerLabel.setBounds(120, 30, 400, 20);
		scrollPanel.setBounds(120, 100, 400, 200);
		instLabel.setBounds(120, 60, 400, 20);
		cb.setBounds(250,60,70,20);
		getSlotsBtnAdmin.setBounds(350,60, 100, 20);
		backBtn.setBounds(120, 500, 120, 20);
		
		//add to the pane
		getContentPane().add(headerLabel);
		getContentPane().add(scrollPanel);
		getContentPane().add(instLabel);
		getContentPane().add(cb);
		getContentPane().add(getSlotsBtnAdmin);
		getContentPane().add(backBtn);
		
		backBtn.addActionListener(this); 
		
		getSlotsBtnAdmin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				slotValueAdmin = cb.getSelectedItem().toString();
				try{
					requestForInventory();
					Object temp = null;
					System.out.println("before there is ob");
					while((temp = is.readObject()) != null){
						System.out.println("there is object");
						System.out.println(temp);
						HashMap<String, Object> invData1 = (HashMap<String,Object>)temp;
						if(invData1.get(REQUEST_TYPE).equals(FETCH_SLOT_REQUEST_TYPE)){
							//update inventory table
							
							if(invData1.get(DATA_FIELD) != null){
								System.out.println(invData1.get(DATA_FIELD));
								//tableModel.getDataVector().removeAllElements();
								populateAllInventoryAdmin((HashMap<String, Object>)invData1.get(DATA_FIELD));									
								break;
							}
						}

					}

				}
				catch(Exception e1){
					
				}
			}
		});

		
	}
		
	// For report 2 end
	
	// For report 3 start
	
	
		private void createReport3(){
			//remove whatever is present
			getContentPane().removeAll(); 
			getContentPane().revalidate();
			getContentPane().repaint();

			//create header
			JLabel headerLabel = new JLabel("Per User booking list");
			headerLabel.setFont(new Font("Serif", Font.BOLD, 16));	
			headerLabel.setForeground(Color.BLUE);

			
			JButton backBtn = new JButton("Back"); 
			//create table
			
			adminReport3Model = new DefaultTableModel(){
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			adminReport3Model.setColumnIdentifiers(new Object[]{"Slot Id","Time Slot","Oct-(Day)"});		
			report3Table = new JTable(adminReport3Model);
			report3Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
			JScrollPane scrollPanel = new JScrollPane(report3Table);

			//create textbox to enter the user info
			JLabel instLabel = new JLabel("User");

			//create a text box
			final JTextField cb = new JTextField();

			//Get list of time slots
			JButton getUserBtnAdmin = new JButton("Get Bookings");

			//mailGuy.setBounds(250,60,400,20);
			headerLabel.setBounds(120, 30, 400, 20);
			scrollPanel.setBounds(120, 100, 400, 200);
			instLabel.setBounds(120, 60, 400, 20);
			cb.setBounds(250,60,120,20);
			getUserBtnAdmin.setBounds(350,60, 130, 20);
			backBtn.setBounds(120, 500, 120, 20);
			
			//add to the pane
			getContentPane().add(headerLabel);
			getContentPane().add(scrollPanel);
			getContentPane().add(instLabel);
			getContentPane().add(cb);
			getContentPane().add(getUserBtnAdmin);
			getContentPane().add(backBtn);
			backBtn.addActionListener(this); 
			
			
			getUserBtnAdmin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					userValueAdmin = cb.getText().toString();
					
					try{
						requestForInventory();
						Object temp = null;

						while((temp = is.readObject()) != null){
							
							HashMap<String, Object> invData1 = (HashMap<String,Object>)temp;
							if(invData1.get(REQUEST_TYPE).equals(FETCH_SLOT_REQUEST_TYPE)){
								//update inventory table
								
								if(invData1.get(DATA_FIELD2) != null){
									//tableModel.getDataVector().removeAllElements();
									populateBookedInventoryAdmin((HashMap<String, Object>)invData1.get(DATA_FIELD2),userValueAdmin);									
									break;
								}
							}

						}

					}
					catch(Exception e1){
						
					}
				}
			});

			
		}
		
		
		// For report 2 end

	
	// For report 3 end
	
	private void createInventoryPanel(){
		//remove whatever is present
		getContentPane().removeAll(); 
		getContentPane().revalidate();
		getContentPane().repaint();

		//create header
		JLabel headerLabel = new JLabel("Booking List");
		headerLabel.setFont(new Font("Serif", Font.BOLD, 16));	
		headerLabel.setForeground(Color.BLUE);

		//create table
		
		tableModel = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableModel.setColumnIdentifiers(new Object[]{"Slot Id","Time Slot","Booked?"});		
		invTable = new JTable(tableModel);
		invTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollPanel = new JScrollPane(invTable);

		//create drop down to enter days
		JLabel instLabel = new JLabel("Select a day");
		//JTextField stockField = new JTextField();

		//create a dropdown

		String days[] = {"1","2"};
		for(int i=3;i<=31;i++){
			days = addElement(days,String.valueOf(i));
		}
		JComboBox cb=new JComboBox(days);

		//Get list of time slots
		JButton getSlotsBtn = new JButton("Get Slots");

		//button to go back
		JButton addBtn = new JButton("Book");

		//booking header
		JLabel cartHeader = new JLabel("My Bookings");
		cartHeader.setFont(new Font("	Serif", Font.BOLD, 16));
		cartHeader.setForeground(Color.BLUE);

		//Booking Table
		cartTableModel = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		cartTableModel.setColumnIdentifiers(new Object[]{"Slot Id","Time Slot","Oct-(Day)"});		
		JTable cartTable = new JTable(cartTableModel);
		cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		JScrollPane scrollPanel1 = new JScrollPane(cartTable);

		//button to remove from cart
		JButton removeBtn = new JButton("Cancel booking");


		//set positions
		//mailGuy.setBounds(250,60,400,20);
		headerLabel.setBounds(120, 30, 400, 20);
		scrollPanel.setBounds(120, 100, 400, 200);
		instLabel.setBounds(120, 60, 400, 20);
		cb.setBounds(250,60,70,20);
		getSlotsBtn.setBounds(350,60, 100, 20);
		
		addBtn.setBounds(120, 330, 150, 20);
		cartHeader.setBounds(120,380,400,20);
		scrollPanel1.setBounds(120,410,400,200);
		removeBtn.setBounds(120,630,150,20);
		//placeOrderBtn.setBounds(300, 630, 120, 20);

		//add to the pane
		getContentPane().add(headerLabel);
		getContentPane().add(scrollPanel);
		getContentPane().add(instLabel);
		
		getContentPane().add(addBtn);
		getContentPane().add(cartHeader);
		getContentPane().add(scrollPanel1);
		getContentPane().add(removeBtn);
		//getContentPane().add(placeOrderBtn);
		getContentPane().add(cb);
		getContentPane().add(getSlotsBtn);
		
		getSlotsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				slotValue = cb.getSelectedItem().toString();
				try{
					requestForInventory();
					Object temp = null;

					while((temp = is.readObject()) != null){
						
						HashMap<String, Object> invData1 = (HashMap<String,Object>)temp;
						if(invData1.get(REQUEST_TYPE).equals(FETCH_SLOT_REQUEST_TYPE)){
							//update inventory table
							
							if(invData1.get(DATA_FIELD) != null){
								//tableModel.getDataVector().removeAllElements();
								populateAllInventory((HashMap<String, Object>)invData1.get(DATA_FIELD));									
								break;
							}
						}

					}

				}
				catch(Exception e1){
					
				}
			}
		});

		//Action listener for booking
		addBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(invTable.getSelectedRow() == -1){
					JOptionPane.showMessageDialog(null, "Please select a time slot!!");
					return;
				}


				String slotId = (String)tableModel.getValueAt(invTable.getSelectedRow(), 0);
				String timeSlot = (String)tableModel.getValueAt(invTable.getSelectedRow(), 1);
				String dayId = cb.getSelectedItem().toString();
				String selectedInd = (String)tableModel.getValueAt(invTable.getSelectedRow(), 2);
				//String bookInd = (String)tableModel.getValueAt(invTable.getSelectedRow(), 2);
				String bookInd = "Y";


				//check if already added
				boolean alreadyAdded = false;
				;
				
				if(selectedInd.equals("Y")){
					alreadyAdded = true;
				}
				
				if(cartTable.getRowCount() == 3){
					
				}
			

				try{
					//update server
					//HashMap<String, Object> outMap = (HashMap<String,Object>)is.readObject();
					//add only if not already added
					
					if(!alreadyAdded){		
						if(cartTable.getRowCount() < 3){
						
							cartTableModel.addRow(new Object[]{slotId,timeSlot,dayId});
							cartTableModel.fireTableDataChanged();

							//update slots in server
							for(int i=0; i<invTable.getRowCount(); i++){
								if(tableModel.getValueAt(i, 0).toString().equals(slotId)){
									
									HashMap<String,String> dataMap = new HashMap<String,String>();
									dataMap.put(SLOT_ID, slotId);
									dataMap.put(BOOK_IND, bookInd);
									dataMap.put(DAY_ID, dayId);

									
									Map<String,Object> inputMap = new HashMap<String,Object>();
									inputMap.put(REQUEST_TYPE, PLACE_BOOK_REQ_TYPE);
									inputMap.put(AUTH_DATA, authMap);
									inputMap.put(DATA_FIELD, dataMap);

									os.writeObject(inputMap);			
									os.flush();

									Object temp2 = is.readObject();
									tableModel.setValueAt("Y",i,2);
									break;
								}
							}

							JOptionPane.showMessageDialog(null, "Slot booked successfully.");
						}
						else{
							JOptionPane.showMessageDialog(null, "You have reached the maximum number of bookings");
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Slot is already booked!!");
					}					
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Exception :: "+ex.getMessage());
				}
			}
		});

		//action listener for the remove button
		removeBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(cartTable.getSelectedRow() == -1){
					JOptionPane.showMessageDialog(null, "Please select a slot to remove!!");
					return;
				}

				String slotId = (String)cartTableModel.getValueAt(cartTable.getSelectedRow(), 0);
				String timeSlot = (String)cartTableModel.getValueAt(cartTable.getSelectedRow(), 1);
				String day = (String)cartTableModel.getValueAt(cartTable.getSelectedRow(), 2);
			

				try{
					removeBookRequest(slotId, timeSlot, day);

					HashMap<String, Object> outMap = (HashMap<String,Object>)is.readObject();
					if(outMap.get(REQUEST_TYPE).equals(REMOVE_FROM_BOOK_REQ_TYPE)){
						if(outMap.get(REMOVE_FROM_CART_STTAUS).toString().equals("1")){
							
							cartTableModel.removeRow(cartTable.getSelectedRow());
							cartTableModel.fireTableDataChanged();

							String dayId = cb.getSelectedItem().toString();
							if(dayId.equals(day)){
								tableModel.setValueAt("N",Integer.parseInt(slotId),2);	
							}
							JOptionPane.showMessageDialog(null, "Booking removed Successfully!!");
						}
					}

				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});

		//action listener for the place order button
			}

	/**
	 * This method is used to send a remove request to server
	 */	
	protected void removeBookRequest(String slotId, String timeSlot, 
			String day) throws Exception{

		HashMap<String,String> dataMap = new HashMap<String,String>();
		dataMap.put(SLOT_ID, slotId);
		dataMap.put(DAY_ID, day);
		
		
		Map<String,Object> inputMap = new HashMap<String,Object>();
		inputMap.put(REQUEST_TYPE, REMOVE_FROM_BOOK_REQ_TYPE);
		inputMap.put(AUTH_DATA, authMap);
		inputMap.put(DATA_FIELD, dataMap);
		
		os.writeObject(inputMap);			
		os.flush();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			
		if(e.getSource() instanceof JButton){
			JButton btn = (JButton)e.getSource();
			
			
			if(btn.getText().equals("User List")){
				adminReport1();
			}
			if(btn.getText().equals("Monthly Status")){
			
						requestForInventory();
						Object temp = null;
						int o=1;
						while((temp = is.readObject()) != null){
							HashMap<String, Object> tempMap = (HashMap<String, Object>)temp;
							//String requestType = (String)tempMap.get(REQUEST_TYPE);				
							createReport2();	
							populateAllInventoryAdmin((HashMap<String, Object>)tempMap.get(DATA_FIELD));
							
							break;
						}
						
				
			}
			if(btn.getText().equals("User Registeration")){				
					createReport3();
					

			}
			if(btn.getText().equals("Back")){
				
				createAdminPanel();
			}
			
			//if submit clicked
			if(btn.getText().equals("Submit")){

				//check passwords
				if(!String.valueOf(userPwdField.getPassword())
						.equals(String.valueOf(userConfPwdField.getPassword()))){
					JOptionPane.showMessageDialog(null, "Passwords do not match!!");
					return;
				}

				//send create request to server
				createUser();
			}

			//if login clicked
			if(btn.getText().equals("Login")){
				loginUser();
			}

			//if clear clicked
			if(btn.getText().equals("Clear")){
				clearAllFields();
			}
			
		}
		}
		catch(Exception e3){
			
		}
	}

	/**
	 * This method is used to clear all fields in this frame 
	 */	
	private void clearAllFields() {
		Component[] allComp = getContentPane().getComponents();
		for (Component comp : allComp){
			if(comp instanceof JTextField){
				((JTextField)comp).setText("");
			}
		}
	}

	/**
	 * This method sends the login request to the server
	 */	
	
	private void adminReport1(){
		
		try{
			
			Map<String,Object> inputMap = new HashMap<String,Object>();
			inputMap.put(REQUEST_TYPE, REPORT1_REQUEST_TYPE);
			
			//Data field
			HashMap<String,String> userMap = new HashMap<String,String>();		
			userMap.put(USER_ID, loginEmail.getText());
			userMap.put(USER_PASSWORD, String.valueOf(loginPwd.getPassword()));		
			inputMap.put(DATA_FIELD, userMap);
			inputMap.put(AUTH_DATA, authMap);

			
			os.writeObject(inputMap);			
			os.flush();
			
			
			Object temp = null;
			
			temp = is.readObject();
				//if(temp !=null){
					HashMap<String, Object> userBookedData = (HashMap<String, Object>) temp;
					HashMap<String, String> tempBookMap = null;
					HashMap<String, Object> tempUserBook = null;
					if(userBookedData != null){
						if(!userBookedData.isEmpty()){
							
							createReport1();
							
							if (adminReport1Model.getRowCount() > 0) {
							    for (int i = adminReport1Model.getRowCount() - 1; i > -1; i--) {
							    	adminReport1Model.removeRow(i);
							    }
							}
							
							String userId = null;
							for (Map.Entry<String, Object> en : userBookedData.entrySet()){
									userId = en.getKey();									// get userid as key
									tempUserBook = (HashMap<String, Object>) en.getValue();  // get day and obj pair
									for (Map.Entry<String, Object> entry : tempUserBook.entrySet()){ 
										String day1 = entry.getKey();							//get day as key
										tempBookMap = (HashMap<String, String>) entry.getValue();  //slot and obj pair
										
										for (Map.Entry<String, String> entry1 : tempBookMap.entrySet())
										{
											String slot = entry1.getKey();
											String ind = entry1.getValue();																		
											if(ind.equals("Y")){
												
												adminReport1Model.addRow(new Object[]{userId,CONST_SLOTS[Integer.parseInt(slot)],day1});
											}
											//p++;
										}
									}
											
							}	
						}
						else{
							JOptionPane.showMessageDialog(null, "No Reservations to display");
						}
						
					}
					
				
				else{
					JOptionPane.showMessageDialog(null, "No Reservations to display");
				}
			
		}
		catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Exception :: "+ex.getMessage());
		}
	}
	
	private void loginUser() {
		//Request Type
		Map<String,Object> inputMap = new HashMap<String,Object>();
		inputMap.put(REQUEST_TYPE, LOGIN_REQUEST_TYPE);

		//Data field
		HashMap<String,String> userMap = new HashMap<String,String>();		
		userMap.put(USER_ID, loginEmail.getText());
		userMap.put(USER_PASSWORD, String.valueOf(loginPwd.getPassword()));		
		inputMap.put(DATA_FIELD, userMap);

		try{
			//send login request to server
			//os = new ObjectOutputStream(getSocket().getOutputStream());
			os.writeObject(inputMap);
			os.flush();

			//Thread.sleep(1000);

			//is = new ObjectInputStream(getSocket().getInputStream());

			Object temp = null;
			int o=1;
			while((temp = is.readObject()) != null){
				HashMap<String, Object> tempMap = (HashMap<String, Object>)temp;

				String requestType = (String)tempMap.get(REQUEST_TYPE);

				if(requestType.equals(LOGIN_REQUEST_TYPE)){					
					if(tempMap.get(AUTH_DATA) != null){
						JOptionPane.showMessageDialog(null, "Logged in Successfully.");

						authMap = (HashMap<String, Object>)tempMap.get(AUTH_DATA);
						if(loginEmail.getText().equals("admin")){
							
							createAdminPanel();
							break;
						}
						else{
							requestForInventory();
						}
						//if logged in get all inventories

					}else{
						String authFailCode = tempMap.get(AUTH_FAIL_CODE).toString();

						if(authFailCode.equals("-1")){
							JOptionPane.showMessageDialog(null, "Login failed! Invalid User ID/Password.");
						}

						if(authFailCode.equals("-2")){
							JOptionPane.showMessageDialog(null, "User already logged in!!");
						}

						break;
					}					
				}

				if(requestType.equals(FETCH_SLOT_REQUEST_TYPE)){
					if(tempMap.get(DATA_FIELD) != null){
						createInventoryPanel();
						
						populateAllInventory((HashMap<String, Object>)tempMap.get(DATA_FIELD));
				
					}

					if(tempMap.get(DATA_FIELD2) != null){
						populateBookedInventory((HashMap<String, Object>)tempMap.get(DATA_FIELD2),loginEmail.getText());
					}

					break;
				}

			}
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Exception :: "+ex.getMessage());
		}
	}

	/**
	 * This method sends a request to server to get all inventories
	 */
	private void requestForInventory() throws Exception{

		Map<String,Object> inputMap = new HashMap<String,Object>();
		inputMap.put(REQUEST_TYPE, FETCH_SLOT_REQUEST_TYPE);
		inputMap.put(AUTH_DATA, authMap);
		inputMap.put(DATA_FIELD, new HashMap<String,String>());
System.out.println("in req for inv ");
		os.writeObject(inputMap);			
		os.flush();
	}

	private void populateAllInventoryAdmin(HashMap<String, Object> invData) {
		
		HashMap<String, String> tempMap = null;
		
		if(slotValueAdmin == null){
			tempMap = (HashMap<String, String>) invData.get("1");
			
			for (Map.Entry<String, String> entry : tempMap.entrySet())
			{
				System.out.println(entry.getKey() + "/" + entry.getValue());
				
				adminReport2Model.addRow(new Object[]{entry.getKey(),CONST_SLOTS[Integer.parseInt(entry.getKey())],entry.getValue()});
				
			}
		}
		else{
			
			tempMap = (HashMap<String, String>) invData.get(slotValueAdmin);
			int j=0;
			
			for (Map.Entry<String, String> entry : tempMap.entrySet())
			{
				
				adminReport2Model.setValueAt(entry.getKey(), j, 0);
				adminReport2Model.setValueAt(entry.getValue(), j, 2);
				j++;
			}
		}

	}
	
	private void populateBookedInventory(HashMap<String, Object> userBookedData,String userId) {
		if(userBookedData !=null){
			
			
			HashMap<String, String> tempBookMap = null;
			HashMap<String, Object> tempUserBook = null;
			
			
					
			if (cartTableModel.getRowCount() > 0) {
			    for (int i = cartTableModel.getRowCount() - 1; i > -1; i--) {
			    	cartTableModel.removeRow(i);
			    }
			}
			
			for (Map.Entry<String, Object> en : userBookedData.entrySet()){
				if(en.getKey().equals(userId)){
					String userId1 = en.getKey();
									
					
					tempUserBook = (HashMap<String, Object>) en.getValue();
					
					for (Map.Entry<String, Object> entry : tempUserBook.entrySet()){
						String day1 = entry.getKey();
						
						tempBookMap = (HashMap<String, String>) entry.getValue();
						
						for (Map.Entry<String, String> entry1 : tempBookMap.entrySet())
						{
							
							if(entry1.getValue().equals("Y")){
								cartTableModel.addRow(new Object[]{entry1.getKey(),CONST_SLOTS[Integer.parseInt(entry1.getKey())],entry.getKey()});
							}
							//p++;
						}
					}
					
				}
			}

		}
	}

	private void populateBookedInventoryAdmin(HashMap<String, Object> userBookedData,String userId) {
		if(userBookedData !=null){
			
			
			HashMap<String, String> tempBookMap = null;
			HashMap<String, Object> tempUserBook = null;
			
			
						
			if (adminReport3Model.getRowCount() > 0) {
			    for (int i = adminReport3Model.getRowCount() - 1; i > -1; i--) {
			    	adminReport3Model.removeRow(i);
			    }
			}
			
			for (Map.Entry<String, Object> en : userBookedData.entrySet()){
				if(en.getKey().equals(userId)){
					String userId1 = en.getKey();
					
					tempUserBook = (HashMap<String, Object>) en.getValue();
					
					for (Map.Entry<String, Object> entry : tempUserBook.entrySet()){
						String day1 = entry.getKey();
						
						tempBookMap = (HashMap<String, String>) entry.getValue();
						//int p=0;
						for (Map.Entry<String, String> entry1 : tempBookMap.entrySet())
						{
							
							if(entry1.getValue().equals("Y")){
								adminReport3Model.addRow(new Object[]{entry1.getKey(),CONST_SLOTS[Integer.parseInt(entry1.getKey())],entry.getKey()});
							}
							
						}
					}
					
				}
			}
		}
	}

	/**
	 * This method populates all inventory detail in the inventory table 
	 */
	private void populateAllInventory(HashMap<String, Object> invData) {
		/*		String day1 = null;
		String slot1 = null;
		String ind1 = null;*/
		
		HashMap<String, String> tempMap = null;
		
		if(slotValue == null){
			tempMap = (HashMap<String, String>) invData.get("1");
			//int i=0;
			//String timeS=null;
			for (Map.Entry<String, String> entry : tempMap.entrySet())
			{
				tableModel.addRow(new Object[]{entry.getKey(),CONST_SLOTS[Integer.parseInt(entry.getKey())],entry.getValue()});
				
			}
		}
		else{
			
			tempMap = (HashMap<String, String>) invData.get(slotValue);
			int j=0;
			
			for (Map.Entry<String, String> entry : tempMap.entrySet())
			{
				
				tableModel.setValueAt(entry.getKey(), j, 0);
				tableModel.setValueAt(entry.getValue(), j, 2);
				j++;
			}
		}

		/*
		for(HashMap<String, String> tempMap1 : tempMap){
			tableModel.addRow(new Object[]{tempMap.get(SLOT_ID),
					tempMap.get(BOOK_IND),tempMap.get(ITEM_STOCK)});
		}*/
	}

	/**
	 * This method is used to create the user
	 */
	private void createUser() {

		Map<String,Object> inputMap = new HashMap<String,Object>();
		inputMap.put(REQUEST_TYPE, REGISTER_REQUEST_TYPE);

		HashMap<String,String> userMap = new HashMap<String,String>();		
		userMap.put(FIRST_NAME, firstNameField.getText());
		userMap.put(LAST_NAME, lastNameField.getText());
		userMap.put(USER_ID, userIdField.getText());
		userMap.put(USER_PASSWORD, String.valueOf(userPwdField.getPassword()));

		
		inputMap.put(DATA_FIELD, userMap);

		try{
			//os = new ObjectOutputStream(getSocket().getOutputStream());
			os.writeObject(inputMap);
			os.flush();

			//is = new ObjectInputStream(getSocket().getInputStream());

			//TODO need to change
			String temp = null;
			temp = (String)is.readObject();
			while(temp != null){
				if(temp.equalsIgnoreCase("0")){
					JOptionPane.showMessageDialog(null, "Registered Successfully. Please login.");
					
					break;
				}
				else if(temp.equalsIgnoreCase("-1")){
					JOptionPane.showMessageDialog(null, "User Id or Password is missing");
					
					break;
				}
				else if(temp.equalsIgnoreCase("-2")){
					JOptionPane.showMessageDialog(null, "Length of User Id should be between 6 and 12 characters");
					
					break;
				}
				else if(temp.equalsIgnoreCase("-3")){
					JOptionPane.showMessageDialog(null, "Length of Password should be between 6 and 12 characters");
					
					break;
				}
				else if(temp.equalsIgnoreCase("-4")){
					JOptionPane.showMessageDialog(null, "User Id already exists");
					
					break;
				}
			}

		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Exception :: "+ex.getMessage());
		}
	}


}
