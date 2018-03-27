package BMS;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class AccTest {
	Basic_Banking_Account bba= new Basic_Banking_Account();

	@Test
	public void testDate() {
		Date date=null;
		date = new Date(); 
		SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yy");
		dt1.format(date);
		
		Date result = bba.getCurrentDate();
		assertEquals(date,result);
		
		
	}

	@Test
	public void generateUniqueIDTest() {
		
		String result= bba.generateAccID("17");
		System.out.println(result);
		 assertFalse("17-0000052".equals(result));
		
	
		
		
	}
	 // basic  account
	@Test
	public void WithdrawTest() throws Exception {
	Account.withdraw("01-00000013", "password", 1000.0);
	System.out.println(bba.getcurrbalance());
	double curr= bba.getcurrbalance();
	assertFalse(15000.0==curr);
    
		
		
	}
	
	// saving account
	@Test
	public void WithdrawTest2() throws Exception {
		Account.withdraw("02-00000003", "tiger", 1000.0);
	   double curr= bba.getcurrbalance();
	   assertFalse(10000==curr);
	}
	// current account
	@Test
	public void WithdrawTest3() throws Exception {
		Account.withdraw("03-00000006", "batting", 1000.0);
	   double curr= bba.getcurrbalance();
	   assertFalse(10000==curr);
	}
	
	//ccheck balance in negative
	public void WithdrawnegativeTest() throws Exception {
		Account.withdraw("01-00000001", "Pakistan", 1000.0);
		System.out.println(bba.getcurrbalance());
		double curr= bba.getcurrbalance();
		assertEquals(-1,curr);}
}
