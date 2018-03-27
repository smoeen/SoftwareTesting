package BMS;
import java.io.*;
import java.text.*;
import java.util.*;


public abstract class BANK {
	
	/*
						ACCOUNT OPEN/CLOSE FUNCTIONS
	*/

	private static String checkForCustomer(File myFile, String profile) {
		if (myFile.exists() && myFile.length() > 0) {
			String newRecord = "";
			String CNIC = (profile.split(","))[3];
			String temp = readFile(myFile,null);
			if (temp.length() > 0 ) {
				String [] parts = temp.split("\n");
				for (int i=0; i<parts.length; i++) {
					if (parts[i].contains(CNIC)) {
						newRecord = parts[i];
						return newRecord;
					}
				}
			}
		}
		return null;
	}	
		
	// create a new account for a particular customer
	public static void openAccount(Customer cus, Account acc) {
		File myFile1 = null;
		File myFile2 = null;
		if (acc instanceof Basic_Banking_Account) {
			myFile1 = new File("BMS/Profiles/Basic_Banking_Account.txt");			
			myFile2 = new File("BMS/AccountInfo/Basic_Banking_Account.txt");	
		}
		else if (acc instanceof Saving_Account) {
			myFile1 = new File("BMS/Profiles/Saving_Account.txt");			
			myFile2 = new File("BMS/AccountInfo/Saving_Account.txt");	
		}
		else if (acc instanceof Current_Account) {
			myFile1 = new File("BMS/Profiles/Current_Account.txt");			
			myFile2 = new File("BMS/AccountInfo/Current_Account.txt");	
		}

		File myFile = new File("BMS/files/CusID.txt");
		String cusID = cus.getCusID(myFile);
		String cusProfile = cusID + "," + cus.toString();
		String accInfo = cusID+","+acc.toString();	
		
		String fileName = null;
		try {
			Date date = new Date(); 
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
			fileName = dt1.format(date);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		String AccountHistory = acc.getAccID() + ",OPENED,"+fileName+"\n";
		String PassFile = acc.getAccID() + "," + cus.getPassword() + "\n";
		if (myFile1 != null && myFile2!=null) { 
			BANK.writeFile(myFile1,cusProfile,true);
			BANK.writeFile(myFile2,accInfo,true);
			BANK.writeFile(new File("BMS/files/AccountDetails.txt"), PassFile, true);
			BANK.writeFile(new File("BMS/files/AccountHistory.txt"),AccountHistory,true);
			System.out.println("Account successfuly open !");
			System.out.println("\n\tYour New Account Number (A/C) : " + acc.getAccID());
		}				
	}

	// close a given account
	public static void closeAccount(String accNo) throws Exception{	
		File f0 = null;
		File f1 = null;
		if ((accNo.substring(0,2)).equals("01")) {
			f0 = new File("BMS/AccountInfo/Basic_Banking_Account.txt");
			f1 = new File("BMS/Profiles/Basic_Banking_Account.txt");
		}
		else if ((accNo.substring(0,2)).equals("02")) {
			f0 = new File("BMS/AccountInfo/Saving_Account.txt");
			f1 = new File("BMS/Profiles/Saving_Account.txt");
		}		
		else if ((accNo.substring(0,2)).equals("03")) {
			f0 = new File("BMS/AccountInfo/Current_Account.txt");
			f1 = new File("BMS/Profiles/Current_Account.txt");
		}
		else {
			throw(new Exception("Invalid Account Number"));
		}
		
		// read the Account information and fetch the detail of account against accNo
		// remove from the Account Information file
		String AccInfo = readFile(f0,null);
		String [] parts = AccInfo.split("\n");
		String record = "";
		String [] AccUpdate = new String [parts.length-1];
		for (int i=0,j=0; i<parts.length; i++) {
			if (parts[i].contains(accNo)) {
				record = parts[i];
			}
			else {
				AccUpdate[j++] = parts[i];
			}
		}
		if (record != "") {
			// remove from the login details Files
			String [] LoginDetail = (readFile(new File("BMS/files/AccountDetails.txt"),null)).split("\n");
			String [] LoginUpdate = new String [LoginDetail.length-1];
			for (int i=0,j=0; i<LoginDetail.length; i++) {
				if (!(LoginDetail[i].contains(accNo))) {
					LoginUpdate[j++] = LoginDetail[i];
				}
			}
			String cusID = (record.split(","))[0];
			// remove from the Customer Profiles
			String [] customer = (readFile(f1,null)).split("\n");
			String [] cusUpdate = new String[customer.length-1];
			record = "";
			for (int i=0,j=0; i<customer.length; i++) {
				if (!(customer[i].contains(cusID))) {
					cusUpdate[j++] = customer[i];
				}
				else 
					record = customer[i];
			}
			record += "\n";
			// Update the Customer Profile files 
			writeFile(f1,makeString(cusUpdate,"\n",""),false);
			// Update the Account Information File
			writeFile(f0,makeString(AccUpdate,"\n",""),false);
			// Update the Login Detail File
			writeFile(new File("BMS/files/AccountDetails.txt"),makeString(LoginUpdate,"\n",""),false);
			// maintain a separate closed account file	
			writeFile(new File("BMS/files/closedAccountList.txt"), record, true);
			String fileName = null;
			try {
				Date date = new Date(); 
				SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
				fileName = dt1.format(date);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			String cl = accNo + ",CLOSED,"+fileName+"\n";
			writeFile(new File("BMS/files/AccountHistory.txt"),cl,true);
			System.out.println("Accout Deleted Successfully!");
		}
		else {
			throw (new Exception("There is no such Account Exist!"));
		}
	}

	// re open an account
	public static void reopenAccount(String cnic, String pass, Account acc) throws Exception {
		File myFile = new File("BMS/files/closedAccountList.txt");
		if (myFile.exists() && myFile.length() > 0 ) {
			String account = readFile(myFile,null);
			if (account.length() > 0 ) {
				String [] parts = account.split("\n");
				String [] closedUpdate = new String [parts.length];
				String record = "";
				int j = 0;
				for (int i=0; i<parts.length; i++) {
					if (parts[i].contains(cnic)) {
						record = parts[i];
					}
					else {
						closedUpdate[j++] = parts[i];
					}
				}
				if (record != "") {
					File myFile1 = null;		
					File myFile2 = null;
					if (acc instanceof Basic_Banking_Account) {
						myFile1 = new File("BMS/Profiles/Basic_Banking_Account.txt");			
						myFile2 = new File("BMS/AccountInfo/Basic_Banking_Account.txt");	
					}
					else if (acc instanceof Saving_Account) {
						myFile1 = new File("BMS/Profiles/Saving_Account.txt");			
						myFile2 = new File("BMS/AccountInfo/Saving_Account.txt");	
					}
					else if (acc instanceof Current_Account) {
						myFile1 = new File("BMS/Profiles/Current_Account.txt");			
						myFile2 = new File("BMS/AccountInfo/Current_Account.txt");	
					}
					String cusID = (record.split(","))[0];
					record += "\n";
					String accinfo = cusID + "," + acc.toString();
					String login = acc.getAccID() + "," + pass + "\n";
					// update the Customer profile file
					writeFile(myFile1,record,true);
					// update the Account Infomation file
					writeFile(myFile2,accinfo,true);
					// update the login details file
					writeFile(new File("BMS/files/AccountDetails.txt"), login, true);
					if (j>0) {
						// update the closing account list
						writeFile(new File("BMS/files/closedAccountList.txt"), makeString(closedUpdate,"\n",""),false);
					}
					else {
						writeFile(new File("BMS/files/closedAccountList.txt"),"",false);
					}
					System.out.println("Account Open SuccessFully!");
					System.out.println("\n\t\tYou New Account Number is " + acc.getAccID());
				}
				else {
					System.out.println("1 There is no record against CNIC " + cnic);
				}
			}
			else {
				throw (new Exception("There is no closed account yet!"));
			}
		}
		else {
			System.out.println("There is no closed account yet!");
		}
	}
	
	/*
						VIEW ACCOUNT/CUSTOMER DETAILS FUNCTIONS
	*/
	
	public static void printTodayDetails() {
		String fileName = null;
		try {
			Date date = new Date(); 
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
			fileName = dt1.format(date);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}		
		String superString = "\n\tCURRENT DATE : " + fileName + "\n\n\n";		
		int tA = 0;
		int tC = 0;
		double tW = 0;
		double tD = 0;
		File myFile = new File("BMS/files/AccountHistory.txt");
		if (myFile.exists() && myFile.length() > 0) {
			FileInputStream fin = null;
			int count = 0;
			try {
				fin = new FileInputStream(myFile);
				count = fin.available();
			} catch (IOException ex) { 
				System.out.println("Error in Reading File :: BANK >> printTodayDetails >> AccountHistory.txt"); 
				ex.printStackTrace(); 
			}
			if (count > 0) {
				String [] lines = (readFile(myFile,null)).split("\n");
				if (lines.length > 0) {
					String [] update = new String [lines.length];
					int j=0;
					for (int i=0; i<lines.length; i++) {
						if (lines[i].contains(fileName)) {
							update[j++] = lines[i];
						}	
					}
					for (int i=0; i<j; i++) {
						if (update[i].contains("OPENED")) {
							tA += 1;
						}
						else if (update[i].contains("CLOSED")) {
							tC += 1;
						}
					}
					System.out.println("Total Number of Account opened Today : " + tA);
					superString += "Total Number of Account opened Today : " + tA + "\n";
					System.out.println("Total Number of Account Closed Today : " + tC);
					superString += "Total Number of Account Closed Today : " + tC + "\n";
				}
			}
			else {
				System.out.println("There is no OPENING /CLOSING ACCOUNT ACTIVITY TODAY");
				superString += "There is no OPENING /CLOSING ACCOUNT ACTIVITY TODAY\n";
			}
		}
		else {
			System.out.println("There is no OPENING /CLOSING ACCOUNT ACTIVITY TODAY");
			superString += "There is no OPENING /CLOSING ACCOUNT ACTIVITY TODAY\n";			
		}
		myFile = new File("BMS/Transactions/dailyReports/"+fileName+".txt");
		FileInputStream fin = null;
		int count = 0;
		if (myFile.exists()) {
			try {
				fin = new FileInputStream(myFile);
				count = fin.available();
			} catch (IOException ex) { 
				System.out.println("Error in Reading File :: BANK >> printTodayDetails >> dailReport.txt"); 
				ex.printStackTrace(); 
			}		
			if (count > 0) {
				String [] lines = (readFile(myFile,null)).split("\n");
				if (lines.length > 0) {
					System.out.println("----------------------------------- Transaction Details -----------------------------------");
					superString += "\n\n----------------------------------- Transaction Details -----------------------------------\n";
					double BBAtW = 0;
					double BBAtD = 0;
					double SBAtW = 0;
					double SBAtD = 0;
					double CBAtW = 0;
					double CBAtD = 0;
					String [] BBA = new String [lines.length];
					String [] SBA = new String [lines.length];
					String [] CBA = new String [lines.length];
					int B = 0;
					int S = 0;
					int C = 0;
					for (int i=0; i<lines.length; i++) {
						String [] parts = lines[i].split(",");
						if (lines[i].contains("Deposited")) {
							tD += Double.parseDouble(parts[2]);
							if ((parts[0].substring(0,2)).equals("01")) {
								BBAtD += Double.parseDouble(parts[2]);
								BBA[B++] = lines[i];
							}
							else if ((parts[0].substring(0,2)).equals("02")) {
								SBAtD += Double.parseDouble(parts[2]);
								SBA[S++] = lines[i];
							}					
							else if ((parts[0].substring(0,2)).equals("03")) {
								CBAtD += Double.parseDouble(parts[2]);
								CBA[C++] = lines[i];
							}					
						}
						else if (lines[i].contains("Withdrawal")) {
							tW += Double.parseDouble(parts[2]);		
							if ((parts[0].substring(0,2)).equals("01")) {
								BBAtW += Double.parseDouble(parts[2]);
								BBA[B++] = lines[i];						
							}
							else if ((parts[0].substring(0,2)).equals("02")) {
								SBAtW += Double.parseDouble(parts[2]);	
								SBA[S++] = lines[i];						
							}					
							else if ((parts[0].substring(0,2)).equals("03")) {
								CBAtW += Double.parseDouble(parts[2]);	
								CBA[C++] = lines[i];						
							}										
						}
					}
					
					System.out.println("\n\t\t ********** BASIC BANKING ACCOUNT **********\n");
					superString += "\n\n\n\t\t\t\t\t ********** BASIC BANKING ACCOUNT **********\n";
					System.out.print("\tTotal Amount Deposit : " + BBAtD);
					superString += "\tTotal Amount Deposit : " + BBAtD;
					System.out.println("\t\t Total Amount Withdrawal : " + BBAtW);
					superString += "\t\t Total Amount Withdrawal : " + BBAtW + "\n";
					System.out.println("\t-----------------------------------------------------------------------");
					superString += "\t-----------------------------------------------------------------------\n";
					if (B>0) {
						System.out.println("\tSno \t CREDIT \t DEBIT \t\t\t DATE & TIME\n");
						superString += "\tSno \t CREDIT \t DEBIT \t\t\t\t DATE & TIME\n\n";
						for (int i=0; i<B; i++) {
							String [] parts = BBA[i].split(",");
							System.out.print("\t" + (i+1) + "\t");
							superString += "\t" + (i+1) + "\t\t";
							if (parts[1].contains("Deposited")) {
								System.out.print(parts[2]+"\t\t\t\t\t");
								superString += parts[2]+"\t\t\t\t";
							}
							else if (parts[1].contains("Withdrawal")) {
								System.out.print("\t\t-" + parts[2] + "\t\t\t");
								superString += "\t\t\t-" + parts[2];
							}
							System.out.println(parts[3]);
							superString += "\t\t\t"+parts[3] + "\n";
						}
						superString += "\t-----------------------------------------------------------------------\n";						
					}
					System.out.println("\n\n\t\t ********** SAVING ACCOUNT **********\n");
					superString += "\n\n\n\t\t\t\t\t ********** SAVING ACCOUNT **********\n";
					System.out.print("\tTotal Amount Deposit : " + SBAtD);
					superString += "\tTotal Amount Deposit : " + SBAtD;
					System.out.println("\t\t Total Amount Withdrawal : " + SBAtW);
					superString += "\t\t Total Amount Withdrawal : " + SBAtW + "\n";
					System.out.println("\t-----------------------------------------------------------------------");
					superString += "\t-----------------------------------------------------------------------\n";
					if (S > 0) {
						System.out.println("\tSno \t CREDIT \t DEBIT \t\t\t DATE & TIME\n");
						superString += "\tSno \t CREDIT \t DEBIT \t\t\t\t DATE & TIME\n\n";
						for (int i=0; i<S; i++) {
							String [] parts = SBA[i].split(",");
							System.out.print("\t" + (i+1) + "\t");
							superString += "\t" + (i+1) + "\t\t";
							if (parts[1].contains("Deposited")) {
								System.out.print(parts[2]+"\t\t\t\t\t");
								superString += parts[2]+"\t\t\t\t";
							}
							else if (parts[1].contains("Withdrawal")) {
								System.out.print("\t\t-" + parts[2] + "\t\t\t");
								superString += "\t\t\t-" + parts[2];
							}
							System.out.println(parts[3]);
							superString += "\t\t\t"+parts[3] + "\n";
						}
						superString += "\t-----------------------------------------------------------------------\n";												
					}
					System.out.println("\n\n\t\t ********** CURRENT ACCOUNT **********\n");
					superString += "\n\n\n\t\t\t\t\t ********** CURRENT ACCOUNT **********\n";
					System.out.print("\tTotal Amount Deposit : " + CBAtD);
					superString +="\tTotal Amount Deposit : " + CBAtD;
					System.out.println("\t\t Total Amount Withdrawal : " + CBAtW);
					superString += "\t\t Total Amount Withdrawal : " + CBAtW + "\n";
					System.out.println("\t-----------------------------------------------------------------------");
					superString += "\t----------------------------------------------------------------------\n";
					if (C > 0) {
						System.out.println("\tSno \t CREDIT \t DEBIT \t\t\t DATE & TIME\n");
						superString += "\tSno \t CREDIT \t DEBIT \t\t\t\t DATE & TIME\n\n";
						for (int i=0; i<C; i++) {
							String [] parts = CBA[i].split(",");
							System.out.print("\t" + (i+1) + "\t");
							superString += "\t" + (i+1) + "\t\t";
							if (parts[1].contains("Deposited")) {
								System.out.print(parts[2]+"\t\t\t\t\t");
								superString += parts[2]+"\t\t\t\t";
							}
							else if (parts[1].contains("Withdrawal")) {
								System.out.print("\t\t-" + parts[2] + "\t\t\t");
								superString += "\t\t\t-" + parts[2];
							}
							System.out.println(parts[3]);
							superString += "\t\t\t"+parts[3] + "\n";
						}
						superString += "\t-----------------------------------------------------------------------\n";												
					}
					System.out.println("\n-------------------------------------------------------------------------------------------");
					superString += "\n\n-------------------------------------------------------------------------------------------\n";
					System.out.print("Total Deposit : " + tD + " Rupees");
					superString += "Total Deposit : " + tD + " Rupees";
					System.out.println("\t\tTotal Withdrawal : " + tW + " Rupees");
					superString +="\t\tTotal Withdrawal : " + tW + " Rupees\n";
					System.out.println("\n\t\t\tCLOSING BALANCE FOR TODAY : " + (tD - tW));
					superString += "\n\t\t\tCLOSING BALANCE FOR TODAY : " + (tD - tW);
				}
				else {
					System.out.println("There is no TRANSACTION today!");
					superString += "There is no TRANSACTION today!\n";
				}
			}
			else {
				System.out.println("There is no TRANSACTION today!");
				superString += "There is no TRANSACTION today!\n";			
			}
		}
		else {
			System.out.println("There is no TRANSACTION today!");
			superString += "There is no TRANSACTION today!\n";						
		}
		Scanner input = new Scanner (System.in);
		char ans = '\0';
		while (ans!='y' && ans!='Y' && ans!='N' && ans!='n') {
			System.out.print("Do you want to save it on File [Y/N] : ");
			ans = input.next().charAt(0);
		}
		if (ans == 'y' || ans == 'y') {
			myFile = new File("BMS/files/DailyReport_"+fileName+".txt");
			writeFile(myFile,superString,false);
		}
	}
	
	// view a particular customer 
	public static void findCustomerByAccNo(String accNo) throws Exception{
		File myFile = null;
		File RF = null;
		String code = accNo.substring(0,2);
		if (code.equals("01")) {
			myFile = new File("BMS/AccountInfo/Basic_Banking_Account.txt");
			RF = new File("BMS/Profiles/Basic_Banking_Account.txt");
		}
		else if (code.equals("02")) {
			myFile = new File("BMS/AccountInfo/Saving_Account.txt");
			RF = new File("BMS/Profiles/Saving_Account.txt");
		}
		else if (code.equals("03")) {
			myFile = new File("BMS/AccountInfo/Current_Account.txt");
			RF = new File("BMS/Profiles/Current_Account.txt");
		}
		else {
			throw(new InsufficientFundsException("There is no such Account exist!"));
		}
		String re = readFile(myFile,null);
		re = filter(re,"\n",accNo);
		String cusID = (re.split(","))[0];
		String re1 = readFile(RF,cusID);
		System.out.println("First Name\t Last Name\t Address\t\t CNIC\t DOB\t Gender\t Occupation\t Password\t Account No\t Current Balance\t Currency\t Created ON\t Annual Interset Rate");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");			
		printFileContents(re1,re);					
	}
		
	// Find a particular customer base on the CNIC of the user input
	public static void findCustomerByCNIC(String cnic) throws Exception{
		String [] filename = {"BMS/Profiles/Basic_Banking_Account.txt", "BMS/Profiles/Saving_Account.txt", "BMS/Profiles/Current_Account.txt"};
		String [] filename0 = {"BMS/AccountInfo/Basic_Banking_Account.txt", "BMS/AccountInfo/Saving_Account.txt", "BMS/AccountInfo/Current_Account.txt"};
		System.out.println("First Name\t Last Name\t Address\t\t CNIC\t DOB\t Gender\t Occupation\t Password\t Account No\t Current Balance\t Currency\t Created ON\t Annual Interset Rate");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");								
		for (int i=0; i<filename.length; i++) {
			File myFile = new File(filename[i]);
			String R0 = readFile(myFile,null);
			String [] parts = R0.split("\n");
			for (int j=0; j<parts.length; j++) {
				if (parts[j].contains(cnic)) {
					String cusID = (parts[j].split(","))[0];
					String R1 = readFile(new File(filename0[i]),null);
					String [] piece = R1.split("\n");
					for (int k=0; k<piece.length; k++) {
						if (piece[k].contains(cusID)) {
							printString(parts[j]);
							printString(piece[k]);
							System.out.print("\n");
						}
					}
					break;
				}
			}
		}
	}	
	
	// view all customer of the BANK
	public static void viewAllCustomer() {
		File myFile0 = new File("BMS/Profiles/Basic_Banking_Account.txt");
		File myFile1 = new File("BMS/AccountInfo/Basic_Banking_Account.txt");
		File myFile2 = new File("BMS/Profiles/Saving_Account.txt");
		File myFile3 = new File("BMS/AccountInfo/Saving_Account.txt");
		File myFile4 = new File("BMS/Profiles/Current_Account.txt");
		File myFile5 = new File("BMS/AccountInfo/Current_Account.txt");
		
		String profiles0 = readFile(myFile0,null);
		String accInfo0 = readFile(myFile1,null);
		String profiles1 = readFile(myFile2,null);
		String accInfo1 = readFile(myFile3,null);
		String profiles2 = readFile(myFile4,null);
		String accInfo2 = readFile(myFile5,null);
		
		System.out.println("\n\nFirst Name\t Last Name\t Address\t\t CNIC\t DOB\t Gender\t Occupation\t Account No\t Current Balance\t Currency\t Created ON\t Annual Interset Rate\n");
		System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");												
		printFileContents(profiles0,accInfo0);
		printFileContents(profiles1,accInfo1);
		printFileContents(profiles2,accInfo2);

	}
	
	// view the list of all closed Account
	public static void viewClosedAccount() throws Exception{
		File myFile = new File("BMS/files/closedAccountList.txt");
		if (myFile.exists()) {
			String temp = readFile(myFile,null);
			if (temp != null) {
				System.out.println("\n\nCustomer ID\t First Name \t Last Name \t Address \t CNIC \t DOB \t Gender \t Occupation");
				System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------\n");					
				String [] lines = temp.split("\n");
				for( int i=0; i<lines.length; i++) {
					printString(lines[i]);
					System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------");					
				}
			}
			else 
				System.out.println("There is no Closed Account yet");
		}
		else
			throw(new Exception("Error in Reading File " + myFile.getName() + "!"));
	}

	// view all customer of a particular type of account
	public static void findCustomerOfType(String accType) {
		File cp = null;
		File ai = null;
		if ((accType.toLowerCase()).equals("basic banking account")) {
			cp = new File("BMS/Profiles/Basic_Banking_Account.txt");
			ai = new File("BMS/AccountInfo/Basic_Banking_Account.txt");
		}
		else if ((accType.toLowerCase()).equals("current account")) {
			cp = new File("BMS/Profiles/Current_Account.txt");
			ai = new File("BMS/AccountInfo/Current_Account.txt");
		}
		else if ((accType.toLowerCase()).equals("saving account")) {
			cp = new File("BMS/Profiles/Saving_Account.txt");
			ai = new File("BMS/AccountInfo/Saving_Account.txt");
		}	
		
		String profiles = readFile(cp,null);
		String accInfo = readFile(ai,null);
		
		String Plines [] = profiles.split("\n");
		String Alines [] = accInfo.split("\n");
		
		System.out.println("\n\nFirst Name\t Last Name\t Address\t\t CNIC\t DOB\t Gender\t Occupation\t Account No\t Current Balance\t Currency\t Created ON\t Annual Interset Rate");
		System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");										
		for (int i=0; i<Plines.length; i++) {
			printString(Plines[i]);
			printString(Alines[i]);
			System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");								
		}		
	}

	// print word by word of a String with coma separator 
	private static void printString(String Aline) {
		if (Aline != null) {
			String parts [] = Aline.split(",");
			for (int k=1; k<parts.length; k++) {
				System.out.print(parts[k]+" \t");
			}
		}
	}

	// print all content of file in a specfic format
	private static void printFileContents(String profiles, String accInfo) {
		String Plines [] = profiles.split("\n");
		String Alines [] = accInfo.split("\n");		
		for (int i=0; i<Plines.length; i++) {
			printString(Plines[i]);
			printString(Alines[i]);
			System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");								
			//System.out.println("\n");
		}		
	}
	
	
	/*
						READ/WRITE FUNCTIONS
	*/	
	
	// Given a Larger String
	// using delimeters , split that given string
	// search the desire String from the Array of Strings
	public static String filter(String temp, String del, String par) throws InsufficientFundsException{
		if (par.length() > 0) {
			String [] parts = temp.split(del);
			for (int i=0; i<parts.length; i++) {
				if (parts[i].contains(par)) {
					return parts[i];
				}
			}
		}	
		throw(new InsufficientFundsException("No Record exist against given parameters"));
	}
	
	// Read any Given File and extrace the specific Record/line From the file
	// can return NULL if no such record/line found
	public static String readFile(File myFile, String par) {
		DataInputStream din = null;
		byte [] readByte = null;
		String temp = null;
		try {
			din = new DataInputStream (new FileInputStream(myFile));
			int count = (new FileInputStream(myFile)).available();
			readByte = new byte[count];
			din.read(readByte);
			if (readByte.length > 0) {
				temp = makeReadable(readByte);
				// extracting the Desire String
				if (par != null)
					return (filter(temp,"\n",par));
			}
			din.close();
		}
		catch (IOException ex) { 
			ex.printStackTrace(); 
		}
		finally {
			try { 
				din.close(); 
			}
			catch (IOException ex) {
				ex.printStackTrace(); 
			}
		}
		return temp;
	}	
	
	// first convert char array into bytes
	// Write any Given File
	// if append is true then it append the file otherwise it overwrite the file
	public static void writeFile(File myFile, String par, boolean append) {
		byte [] data = makeWriteable(par);
		DataOutputStream dout = null;
		try {
			dout = new DataOutputStream(new FileOutputStream(myFile,append));
			if (data.length > 0) {
				dout.write(data,0,data.length);
			}
			dout.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				dout.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	} 
	
	// update the given File with new Given Record
	public static void updateFile(File myFile, String [] updateRecord) {
		
		String contents = readFile(myFile,null);					// get the contents of entire file
		String par = makeString(updateRecord,",","");				// updated record ---> make single String
		String [] lines = contents.split("\n");						// separate the line
			
		for (int i=0; i<lines.length; i++) {						// make updation in the content of file
			if (lines[i].contains(updateRecord[1])) {
				lines[i] = par;					
				break;
			}
		}
		
		par = makeString(lines,"\n","");						// convert the whole array into single array
		byte [] data = makeWriteable(par);						// convert the char array into the byte stream
		DataOutputStream dout = null;
		try {
			dout = new DataOutputStream(new FileOutputStream(myFile));
			if (data.length > 0) {
				dout.write(data,0,data.length);
			}
			dout.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				dout.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}		
	}
		
	// Convert the Bytes array into char array 
	public static String makeReadable(byte [] par) {
		String temp = "";
		for (byte c:par) {
			temp += (char)c;
		}
		return temp;
	}
	
	// convery any char array into byte array
	public static byte[] makeWriteable(String par) {
		int size = par.length();
		byte [] arr = new byte[size];
		for (int i=0; i<size; i++) {
			arr[i] = (byte) (par.charAt(i));
		}
		return arr;
	}

	// make a single string of array of string with , as separator
	public static String makeString(String [] temp, String del, String end) {
		String a = "";
		for (int i=0; i<temp.length; i++) {
			if (temp[i] != null) {
				a += temp[i];
				a += del;
			}
		}
		a = a.substring(0,a.length()-1);
		a = a+end;
		return a;
	}
	
}