package BMS;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.*;

public abstract class Account {
	
	double BBA_min_limit = 1000;
	double BBA_withdrawal_limit = 30000;
	double SA_min_limit = 5000;
	double SA_withdrawal_limit = 25000;	
	
	String accID;
	double currBalance;
	Date creaDate;
	String currency;
	double ann_int_rate;
	
	
	
	// take input of details for new account from users
	public void Input() throws Exception{
		Scanner input = new Scanner (System.in);
		System.out.print("Enter the amount for Deposit : ");
		currBalance = input.nextDouble();
		input.nextLine();
		System.out.print("Enter the currency [rupees / dollars] : ");
		currency = input.nextLine();
		
		if ((currency.toLowerCase()).equals("rupees")) {
			currBalance = currBalance;
		} 
		else if ((currency.toLowerCase()).equals("dollars")) {
			System.out.print("Enter the Current Dollar Rate : ");
			double t = input.nextDouble();
			input.nextLine();
			currBalance = currBalance * t;
		}
		else {
			throw (new InsufficientFundsException("Coversion of " + currency + " to rupees is not supported!"));
		}
		
		if (currBalance < BBA_min_limit && currBalance < SA_min_limit)
			throw(new InsufficientFundsException("You can not open account with " + currBalance + " Rupees"));
		
		creaDate = getCurrentDate();
	}
	
	// make a String of whole object
	public String toString() {
		String temp = accID+","+currBalance+","+"Rupees"+","+creaDate+","+ann_int_rate+"\n";
		return temp;
	}
	
	// return the Current Date
	public Date getCurrentDate() {
		Date date = null;
		try {
			date = new Date(); 
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
			dt1.format(date);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}			
		return date;
	}
	
	// Generate a unique Account Number for only new Account
	public String generateAccID(String accCode) {
		File myFile = new File ("BMS/files/AccID.txt");
		String id = BANK.readFile(myFile,null);	
		int ID = Integer.parseInt(id);
		if (id.length() > 0) {	
			while (id.length() <= 7) {
				id = '0' + id;
			}
			ID++;
			String Id = String.valueOf(ID);
			BANK.writeFile(myFile,Id,false);
			id = accCode + "-" + id;
			return id;
		}	
		return null;
	}
	
	// return a unique Account Number for new Account
	public String getAccID() { return accID; }
		
	// Display the Account Details of Given Account Number
	public static void displayAccountInfo(String accNo) throws Exception {
		File myFile = null;
		if ((accNo.substring(0,2)).equals("01")) {
			myFile = new File("BMS/AccountInfo/Basic_Banking_Account.txt");
		}
		else if ((accNo.substring(0,2)).equals("02")) {
			myFile = new File("BMS/AccountInfo/Saving_Account.txt");
		}
		else if ((accNo.substring(0,2)).equals("03")) {
			myFile = new File("BMS/AccountInfo/Current_Account.txt");
		}		
		else 
			throw(new Exception("Invalid Account Number"));
		String temp = BANK.readFile(myFile,null);
		String [] parts = (BANK.filter(temp,"\n",accNo)).split(",");
		System.out.println("\n\nCustomer ID : " + parts[0]);
		System.out.println("Account Number : " + parts[1]);
		System.out.println("Current Balance : " + parts[2] + " " + parts[3]);
		System.out.println("Account Created On : " + parts[4]);
		System.out.println("Annual Interset Rate : " + parts[5]);
	}
	
	/*
							TRANSACTION CONTROL FUNCTION
	*/
	
	// convert the dollar into Rupees
	private static double toRupees(double amount) {
		Scanner in = new Scanner (System.in);
		System.out.print("Enter the Rate of per/Dollar : ");
		double x = in.nextDouble();
		amount  = (amount*x);
		return amount;
	}
	
	// convert the foreign currency if user deposit foreign currency.
	private static double currencyConversation(String curr, double amount) throws InsufficientFundsException{
		if ((curr.toLowerCase()).equals("rupees")) {
			amount = amount;
		}
		else if ((curr.toLowerCase()).equals("dollars")) {
			amount = toRupees(amount);
		}
		else {											// if other any dollar or rupees
			throw(new InsufficientFundsException("Coversation from "+curr+" to Rupees is not supported!"));
		}		
		return amount;
	}
	
	// maintain the daily bases activities in separate files
	private static void MaintainDailyReport(String log) {
		String fileName = null;
		try {
			Date date = new Date(); 
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
			fileName = dt1.format(date);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		File myFile = new File("BMS/Transactions/dailyReports/"+fileName+".txt");
		BANK.writeFile(myFile,log,true);
	}
	
	// fetch the total amount which is already withdraw by a particular user
	public static double FindTotalWithdrawalAmount(String accNo) {
		String fileName = null;
		double cb = 0.0;
		try {
			Date date = new Date(); 
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
			fileName = dt1.format(date);
			File myFile = new File("BMS/Transactions/dailyReports/"+fileName+".txt");
			if (myFile.exists()) {
				String contents = BANK.readFile(myFile,null);				// getting the contents of file
				String [] lines = contents.split("\n");						// make each line as a separate index element
				for (int i=0; i<lines.length; i++) {
					if (lines[i].contains(accNo)) {
						String [] part = lines[i].split(",");					// tokenizing the desire record
						if (part[1].equals("Withdrawal"))					// if record is about the withdrawal
							cb += Double.parseDouble(part[2]);				// sum the amount	
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return cb;
	}
	
	// make deduction from the given account No
	private static double makeDeduction(String accNo, double cb,double amount) throws InsufficientFundsException{
		if (cb <= 0) {
			throw(new InsufficientFundsException("You do not have enough money in your account for this transaction"));
		}
		else if ((cb-amount) < 0) {
			throw(new InsufficientFundsException("You do not have enough money in your account for this transaction"));
		}
		else {
			if (!((accNo.substring(0,2)).equals("03"))) {	
				if ((accNo.substring(0,2)).equals("02")) {
					// check the Saving Account.....account should not be less than 5000
					if ((cb-amount)<5000) {
						throw(new InsufficientFundsException("Your account should not be less than " + 5000));
					}
				}
				
				// check that --> user did not exceed from their transaction limit;
				double limit = FindTotalWithdrawalAmount(accNo);
				if(limit > 0)	{
					if ((accNo.substring(0,2)).equals("01")) {
						if (limit >= 30000) {
							throw (new InsufficientFundsException("You are not allow to make anymore withdraw from your account for today!"));
						}
						if ((30000-limit) < amount) {
							throw (new InsufficientFundsException("You can now only make withdrawal of "+(30000-limit)+" rupees for today!"));							
						}
					}
					else if ((accNo.substring(0,2)).equals("02")) {
						if (limit >= 25000) {
							throw (new InsufficientFundsException("You are not allow to make anymore withdraw from your account for today!"));
						}
						if ((25000-limit) < amount) {
							throw (new InsufficientFundsException("You can now only make withdrawal of "+(25000-limit)+" rupees for today!"));							
						}						
					}
				}
				cb = cb-amount;
			}
			else{
				cb = cb-amount;
			}
			
			return cb;
		}
	}
	
	// deposit amount in given account Number
	public static void deposit(String accNo, double amount, String curr) throws Exception {	
		if (((curr.toLowerCase()).equals("rupees")) && amount < 100 ) {
			throw(new InsufficientFundsException("Invalid Amount \n Minimum amount for deposit is 100 Rupees"));
		}
		else if (((curr.toLowerCase()).equals("dollars")) && amount < 10 ) {
			throw(new InsufficientFundsException("Invalid Amount \n Minimum amount for deposit is 10 Dollars"));			
		}
		
		// make File object for updation
		File myFile = null;
		File RF = null;		
		String code = accNo.substring(0,2);
		if (code.equals("01")) {
			myFile = new File("BMS/Transactions/Logs/Basic_Banking_Account.txt");
			RF = new File ("BMS/AccountInfo/Basic_Banking_Account.txt");
		}	
		else if (code.equals("02")) {
			myFile = new File("BMS/Transactions/Logs/Saving_Account.txt");			
			RF = new File ("BMS/AccountInfo/Saving_Account.txt");
		}
		else if (code.equals("03")) {
			myFile = new File("BMS/Transactions/Logs/Current_Account.txt");	
			RF = new File ("BMS/AccountInfo/Current_Account.txt");		
		}
		else {
			throw(new InsufficientFundsException("There is no such Account Exist"));
		}
		//------------------------------------------------------- check the account first
		String accDetails = BANK.readFile(RF,null);				// if no record exit, function throw exception 
		accDetails = BANK.filter(accDetails,"\n",accNo);
		// Now check the Current Balance and convert the foreign currency into local currency
		if (accDetails.length() > 0) 
		{	
			String [] parts = accDetails.split(",");			// tokenize the fetched record
			double cb = Double.parseDouble(parts[2]);			// extract the current Balance 
			amount = currencyConversation(curr,amount);			// check the currency of money
			cb += amount;
			String nv = String.valueOf(cb);						// convert the balance into string
			parts[2] = nv;										// update the new Current Balance
			BANK.updateFile(RF,parts);							// update the record
			
			// Maintain Log
			String log = parts[1] + ","+"Deposited"+","+amount+","+new Date()+"\n";
			BANK.writeFile(myFile,log,true);
			
			// Maintain Day wise Record
			MaintainDailyReport(log);
			
			// acknowlegment
			System.out.println(amount + " Rupees successfully Deposit in your " + parts[1] + ".");	
			System.out.println("Your New Current Balance is " + parts[2]);
		}
		else 
			throw(new InsufficientFundsException("There is no such record exit against " + accNo + "."));
	}

	// withdraw amount from the given account number
	public static void withdraw(String accNo, String pass, double amount) throws Exception {
		double Tamount = amount;
		if (amount < 100 ) {
			throw(new InsufficientFundsException("Invalid Amount \n Minimum amount for withdrawal is 100 Rupees"));
		}
		else if (amount < 10 ) {
			throw(new InsufficientFundsException("Invalid Amount \n Minimum amount for withdrawal is 10 Dollars"));			
		}
		else {
			// make File object for updation
			File myFile = null;
			File RF = null;	
			String code = accNo.substring(0,2);
			if (code.equals("01")) {
				myFile = new File("BMS/Transactions/Logs/Basic_Banking_Account.txt");
				RF = new File ("BMS/AccountInfo/Basic_Banking_Account.txt");
			}	
			else if (code.equals("02")) {
				myFile = new File("BMS/Transactions/Logs/Saving_Account.txt");			
				RF = new File ("BMS/AccountInfo/Saving_Account.txt");
			}
			else if (code.equals("03")) {
				myFile = new File("BMS/Transactions/Logs/Current_Account.txt");	
				RF = new File ("BMS/AccountInfo/Current_Account.txt");		
			}	
			else {
				throw(new InsufficientFundsException("There is no such Account Exist"));
			}			
			//------------------------------------------------------- check the account first
			
			if (RF.exists() && RF.length() > 0 ) {
				String accDetails = BANK.readFile(RF,accNo);								// if no record exit, function throw exception
				// Now check the Current Balance and convert the foreign currency into local currency
				if (accDetails.length() > 0) {	
					String customerDetails = BANK.readFile(new File("BMS/files/AccountDetails.txt"),null);
					if (customerDetails.length() > 0) {
						String [] CustomerRecord = customerDetails.split("\n");				// tokenize the fetched Customer records
						for (int i=0; i<CustomerRecord.length; i++) {
							if (CustomerRecord[i].contains(accNo)) {						// check either the line contain given Account number or not
								String [] str = CustomerRecord[i].split(",");				// tokenize the selected line 
								if (!(str[1].equals(pass))) 								// check the password
									throw(new Exception("Password Not Match Against given Account Number"));
							}
						}
						
						String [] parts = accDetails.split(",");							// tokenize the fetched record
						double cb = Double.parseDouble(parts[2]);							// extract the current Balance

						cb = makeDeduction(accNo,cb,amount);								// make Deduction from the account and update & store everywhere
						
						String nv = String.valueOf(cb);										// convert the balance into string
						parts[2] = nv;														// update the new Current Balance
						
						BANK.updateFile(RF,parts);											// update the record
						
						// Maintain Log
						String log = parts[1] + ","+"Withdrawal"+","+Tamount+","+new Date()+"\n";
						BANK.writeFile(myFile,log,true);
						
						// Maintain Day wise Record
						MaintainDailyReport(log);
						
						// acknowlegment
						System.out.println("\t\tYou new Current Balance is " + cb + " Rupees.");
					}
					else 
						throw(new Exception("Error in Reading Customer Details against A/C " + accNo + "."));
				}
				else 
					throw(new InsufficientFundsException("There is no such record exit against " + accNo + "."));
			}
			else {
				System.out.println("There is no account yet!");
			}
		}
	}

	public double getcurrbalance() {
		return currBalance;
	}

	/*
							E Statments
	*/
	// Print the E Statment of Given Account Number---> Only for the Basic Banking Account
	public static void printStatment(String accNo) {
		try {
			if (((accNo.substring(0,2)).equals("01"))) {
				File myFile = new File("BMS/Transactions/Logs/Basic_Banking_Account.txt");
				String content = BANK.readFile(myFile,null);
				String [] lines = content.split("\n");
				String [] TR = new String[lines.length];
				int j=0;
				for (int i=0; i<lines.length; i++) {
					if (lines[i].contains(accNo)) {
						TR[j++] = lines[i];
					}
				}
				Basic_Banking_Account.printEStatment(TR,j);
			}
			else {
				throw(new InsufficientFundsException("Permission Denied!"));
			}
		}
		catch(InsufficientFundsException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
}