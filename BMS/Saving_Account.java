package BMS;
import java.lang.*;
import java.io.*;
import java.util.Date;

public class Saving_Account extends Account {
	
	public Saving_Account() {
		accID = generateAccID("02");	
		ann_int_rate = 600;							
	}

	
	public Saving_Account(double cb,String c) 
	{	
		if (cb < SA_min_limit)
			throw(new InsufficientFundsException ("Saving Account can only be opened with a minimum desposit of "+SA_min_limit+"!"));
		currBalance = cb;
		Date cd = getCurrentDate();
		accID = generateAccID("02");
		currency = c;
		ann_int_rate = 600;					
	}
	
}