package BMS;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class CustomerTest1 {

	
	Customer cus = new Customer();

	@Test
	public void CustomerIDtest() {
		String result = cus.getCustomerID();
		assertFalse("-1".equals(result));
	}
	
	@Test
	public void getPasswordTest() {
		String result = cus.getPassword();
		assertFalse("-1".equals(result));
	}
	
	@Test
	public void getCusIDTest() {
		File myfile = new File("BMS/files/CusID.txt");
		String result = cus.getCusID(myfile);
		assertFalse("90".equals(result));
	}
}