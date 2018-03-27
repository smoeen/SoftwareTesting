package BMS;
import java.lang.*;
import java.io.*;
import java.util.*;

public class Customer extends BANK {
	String cusID;
	String Fname;
	String Lname;
	String add;
	String CNIC;
	String DOB;
	char gender;
	String occupation;
	String pass;
//	String AccType;			// can identify from the account number	01 -> BBA :: 02->Current Account :: 03->Saving Account
	
	// return customer ID
	public String getCustomerID() {
		return cusID;
	}
	
	// return the customer password
	public String getPassword() {
		return pass;
	}
	
	// make a customer ID for new Customer 
	public String getCusID(File myFile) {
		String id = BANK.readFile(myFile,null);	
		int ID = Integer.parseInt(id);
		if (id.length() > 0) {	
			while (id.length() <= 7) {
				id = '0' + id;
			}
			ID++;
			String Id = String.valueOf(ID);
			BANK.writeFile(myFile,Id,false);
			return id;
		}	
		return null;
	}
	
	// default constructor
	public Customer () { 

	}
	
	// a contructor for the customer class
	public Customer (String Fn, String ln, String a, String cnic, String dob, char g, String occ, String pa) {
		File myFile = new File("BMS/files/CusID.txt");				
		cusID = getCusID(myFile);
		Fname = Fn;
		Lname = ln;
		add = a;
		CNIC = cnic;
		DOB = dob;
		gender = g;
		occupation = occ;
		pass = pa;
	}
	
	// conver the whole customer object into Single String
	public String toString() {
		String temp = Fname+","+Lname+","+add+","+CNIC+","+DOB+","+gender+","+occupation+"\n";
		return temp;
	}

	
	public String inputPassword() {        
		Console console = System.console();
		if (console == null) {
			System.out.println("Couldn't get Console instance");
			System.exit(0);
		}

		char passwordArray[] = console.readPassword("Enter your secret password: ");
		return new String (passwordArray);
	}	
	
	// taking input the details of customer from the user
	public void Input() {
		Scanner input = new Scanner (System.in);
		System.out.print("Enter the First Name : ");
		Fname = input.nextLine();
		System.out.print("Enter the Last Name : ");
		Lname = input.nextLine();
		System.out.print("Enter the address : ");
		add = input.nextLine();
		System.out.print("Enter the CNIC : ");
		CNIC = input.nextLine();
		System.out.print("Enter the Gender : ");
		gender = input.next().charAt(0);
		input.nextLine();
		System.out.print("Enter the Occupation : ");
		occupation = input.nextLine();
		pass = inputPassword();
		System.out.println("\tDOB  (Must be in integers)");
		System.out.print("Day : ");
		int d = input.nextInt();
		input.nextLine();
		System.out.print("Month : ");
		int m = input.nextInt();
		input.nextLine();
		System.out.print("Year : ");
		int y = input.nextInt();
		input.nextLine();
		DOB = d + "\\" + m + "\\" + y;
	}	
}