package BMS;
import java.io.*;

// customer exception class for Banking Management System
public class InsufficientFundsException extends RuntimeException {
	
	// default constructor
	public InsufficientFundsException() {}
	
	// parameterized Constructor
	public InsufficientFundsException (String msg){ super(msg); }
	
	// return the message related to the exception either user/default
	public String getMessage() { return super.getMessage(); }
	
	// help to Trace Error
	public void traceError() { super.printStackTrace(); }
}
