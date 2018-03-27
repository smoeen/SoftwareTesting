package BMS;
import java.lang.*;
import java.io.*;
import java.util.*;

public class Basic_Banking_Account extends Account {
	
	public Basic_Banking_Account() {
		accID = generateAccID("01");	
		ann_int_rate = 500;							
	}
	
	// constructor for the BASIC BANKING ACCOUNT
	public Basic_Banking_Account(double cb,String c) throws InsufficientFundsException {	
		if (cb < BBA_min_limit)
			throw(new InsufficientFundsException("Basic Banking Account can only be opened with a mimimum deposit of "+BBA_min_limit+"!"));
		currBalance = cb;
		creaDate = getCurrentDate();
		accID = generateAccID("01");
		currency = c;
		ann_int_rate = 500;					
	}	
	
	// specifices the format of the E Statment
	public static void printEStatment(String [] lines,int size) {
		double tW = 0;
		double tD = 0;
		String accNo = null;
		String update = null;
		System.out.println("Sno\t CREDIT \t DEBIT \t\tDATE & TIME");
		System.out.println("----------------------------------------------------------------------");
		update = "Sno\t CREDIT \t DEBIT \t\tDATE & TIME\n";
		update += "----------------------------------------------------------------------\n";
		for (int i=0; i<size; i++) {
			String [] parts = lines[i].split(",");
			accNo = parts[0];
			System.out.print((i+1) + "\t");
			update += (i+1)+"\t";
			if (parts[1].equals("Deposited")) {
				System.out.print(parts[2] + "\t\t\t\t");
				update += parts[2]+"\t\t\t\t\t";
				tD += Double.parseDouble(parts[2]);
			}
			else {
				System.out.print("\t\t-"+parts[2] + "\t\t");
				update += "\t\t\t"+parts[2]+"\t\t";
				tW += Double.parseDouble(parts[2]);
			}
			System.out.println(parts[3]);
			update += parts[3] + "\n";
		}
		System.out.println("----------------------------------------------------------------------");		
		System.out.println("Total Deposit : " + tD + " Rupees");
		System.out.println("Total Withdrawal : " + tW + " Rupees");
		update += "----------------------------------------------------------------------\n";
		update += "\nTotal Deposit    : " + tD + " Rupees\n";
		update += "Total Withdrawal : " + tW + " Rupees";
		if (tD > 0 || tW > 0) {
			Scanner input = new Scanner (System.in);
			char ans = 'B';
			while (ans != 'y' && ans != 'Y' && ans != 'N' && ans != 'n') {
				System.out.print("Do you want to Save it in File [Y/N] : ");
				ans = input.next().charAt(0);				
			}
			if (ans == 'y' || ans == 'Y') {
				File myFile = new File("BMS/Transactions/E_Statments/"+accNo+".txt");
				BANK.writeFile(myFile,update,false);
				System.out.println("Data Write Successfully");
			}
		}
	}
}