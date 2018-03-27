package BMS;
import java.io.*;
import java.util.*;

public class Driven {

	public static String inputPassword() {        
		Console console = System.console();
		if (console == null) {
			System.out.println("Couldn't get Console instance");
			System.exit(0);
		}

		char passwordArray[] = console.readPassword("Enter your account password : ");
		return new String (passwordArray);
	}	

	private static void clearScreen() {
		System.out.print("\033[H\033[2J");  
		System.out.flush();  		
	}
	
	private static void author() {
		clearScreen();
		
		System.out.println("Start");
	}
	
	private static void accountMenu() {
		author();
		System.out.println("\n\t\t*************** ACCOUNT MENU******************");
		System.out.println("\t\t*    1 - BASIC BANKING ACCOUNT               *");
		System.out.println("\t\t*    2 - SAVING ACCOUNT     	             *");
		System.out.println("\t\t*    3 - CURRENT ACCOUNT                     *");
		System.out.println("\t\t***********************************************");
	}	
	
	private static void AccountTypeMenu() {
		author();
		System.out.println("\n\t\t***********SELECT ACCOUNT TYPE****************");
		System.out.println("\t\t*    1 - BASIC BANKING ACCOUNT               *");
		System.out.println("\t\t*    2 - SAVING ACCOUNT     	             *");
		System.out.println("\t\t*    3 - CURRENT ACCOUNT                     *");
		System.out.println("\t\t***********************************************");		
	}
	
	private static void mainMenu() {
		author();
		System.out.println("\n\t\t********************* MENU*********************");
		System.out.println("\t\t*    01 - Open an Account                      *");
		System.out.println("\t\t*    02 - Re-open an Account                   *");
		System.out.println("\t\t*    03 - Deposit     	                       *");
		System.out.println("\t\t*    04 - Withdrawal                           *");
		System.out.println("\t\t*    05 - View Account Details                 *");
		System.out.println("\t\t*    06 - View Customer Details                *");
		System.out.println("\t\t*    07 - View E Statment                      *");
		System.out.println("\t\t*    08 - View All Customer Details            *");
		System.out.println("\t\t*    09 - View Customer By Account Type        *");
		System.out.println("\t\t*    10 - Find Customer By CNIC                *");
		System.out.println("\t\t*    11 - Close an Account                     *");
		System.out.println("\t\t*    12 - View Closed Account                  *");
		System.out.println("\t\t*    13 - View Today Summary                   *");
		System.out.println("\t\t*    14 - Exit                                 *");
		System.out.println("\t\t***********************************************");
	}	
	
	public static void main (String [] args) {
		Scanner input = new Scanner (System.in);
		char cont = 'y';
		int choice = 0;
		try {
			while ((choice!=14) && (cont=='y' || cont=='y')) {
				mainMenu();
				System.out.print("Waiting for Your Choice : ");
				choice = input.nextInt();
				input.nextLine();
				switch (choice) {
					case 1:
						accountMenu();
						System.out.print("Waiting for Your Choice : ");
						int accT = input.nextInt();
						input.nextLine();
						Customer cus = new Customer ();
						Account acc = null;
						if (accT == 1) 
							acc = new Basic_Banking_Account();
						else if (accT == 2)
							acc = new Saving_Account();
						else if (accT == 3)
							acc = new Current_Account();
						else
							System.out.println("\n\t\tNo a Valid Selection!");
						cus.Input();
						acc.Input();
						BANK.openAccount(cus,acc);
						break;
					case 2:
						System.out.print("Enter the CNIC : ");
						String accNo = input.nextLine();
						String pwd = inputPassword();
						accountMenu();
						System.out.print("Waiting for Your Choice : ");
						accT = input.nextInt();
						input.nextLine();
						acc = null;
						if (accT == 1) 
							acc = new Basic_Banking_Account();
						else if (accT == 2)
							acc = new Saving_Account();
						else if (accT == 3)
							acc = new Current_Account();
						else
							System.out.println("\n\t\tNo a Valid Selection!");
						acc.Input();						
						BANK.reopenAccount(accNo, pwd, acc);
						break;
					case 3:
						System.out.print("Enter the A/C : ");
						accNo = input.nextLine();
						System.out.print("Enter the Amount you want to deposit : ");
						double amount = input.nextDouble();
						input.nextLine();
						System.out.print("Enter the Currency in which you are depositing [Rupees / Dollars]: ");
						String curr = input.nextLine();
						Account.deposit(accNo,amount,curr);
						break;				
					case 4:
						System.out.print("Enter the A/C : ");
						accNo = input.nextLine();
						String pass = inputPassword();
						System.out.print("Enter the Amount for withdrawal : ");
						amount = input.nextDouble();
						input.nextLine();
						Account.withdraw(accNo,pass,amount);						
						break;				
					case 5:
						System.out.print("Enter the A/C : ");
						accNo = input.nextLine();
						Account.displayAccountInfo(accNo);
						break;				
					case 6:
						System.out.print("Enter the A/C : ");
						accNo = input.nextLine();
						BANK.findCustomerByAccNo(accNo);
						break;	
					case 7:
						System.out.print("Enter the A/C : ");
						accNo = input.nextLine();
						Account.printStatment(accNo);
						break;
					case 8:
						BANK.viewAllCustomer();
						break;				
					case 9:
						AccountTypeMenu();
						System.out.print("Waiting for Your Choice : ");
						accT = input.nextInt();
						input.nextLine();
						if (accT == 1)
							BANK.findCustomerOfType("Basic Banking Account");
						else if (accT == 2) 
							BANK.findCustomerOfType("Saving Account");
						else if (accT == 3) 
							BANK.findCustomerOfType("Current Account");
						else 
							System.out.println("\n\t\tNo a Valid Selection!");							
						break;
					case 10:
						System.out.print("Enter the CNIC : ");
						accNo = input.nextLine();
						BANK.findCustomerByCNIC(accNo);
						break;
					case 11:
						System.out.print("Enter the A/C : ");
						accNo = input.nextLine();
						BANK.closeAccount(accNo);
						break;
					case 12:
						BANK.viewClosedAccount();
						break;
					case 13:
						BANK.printTodayDetails();
						break;
					case 14:
						System.out.println("EXITED FROM THE MAIN MENU");
						break;
					default:
						System.out.println("Invalid Choice");
				}
				if (choice != 14) {
					System.out.print("\n\nDo you want to perform any other operation [Y/N] : ");
					cont = input.next().charAt(0);
					input.nextLine();
				}			
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}