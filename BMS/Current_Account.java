package BMS;
import java.lang.*;
import java.io.*;
import java.util.Date;

public class Current_Account extends Account {
	
	public Current_Account() {
		accID = generateAccID("03");	
		ann_int_rate = 650;							
	}
	
	public Current_Account(double cb,String c) 
	{	
		Date cd = getCurrentDate();
		accID = generateAccID("03");
		currBalance = cb;
		currency = c;
		ann_int_rate = 650;					
	}
	
//	public String getAccID() { return accID; }		
}