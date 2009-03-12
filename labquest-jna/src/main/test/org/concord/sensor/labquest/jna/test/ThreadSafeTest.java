package org.concord.sensor.labquest.jna.test;

import java.io.IOException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.concord.sensor.labquest.jna.SingleThreadDelegator;

public class ThreadSafeTest extends TestCase 
{
	static Method closeMethod = null;	 
	
	public void testPrintMethod()
	{
		BasicImpl basic = new BasicImpl();
		SingleThreadDelegator<Basic> runner = new SingleThreadDelegator<Basic>();
		runner.start();
		Basic basicTS = runner.instanciate(basic, 
				Basic.class, IOException.class, null);
				
		basicTS.print("Hello World");
		runner.quit();
	}

	public void testThrowExceptionMethod()
	{
		BasicImpl basic = new BasicImpl();
		SingleThreadDelegator<Basic> runner = new SingleThreadDelegator<Basic>();
		runner.start();
		Basic basicTS = runner.instanciate(basic, 
				Basic.class, IOException.class, null);
		
		try {
			basicTS.throwException();
		} catch (IOException e) {
			e.printStackTrace();
		}
		runner.quit();
	}

	
	public void testCloseMethod()
	{
		Method closeMethod = null;
		try {
			closeMethod = Basic.class.getMethod("close");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BasicImpl basic = new BasicImpl();
		SingleThreadDelegator<Basic> runner = new SingleThreadDelegator<Basic>();
		runner.start();
		Basic basicTS = runner.instanciate(basic, 
				Basic.class, IOException.class, closeMethod);

		// this should cause the thread to die
		basicTS.close();
				
		assertTrue("thread didn't die after close method", !runner.isAlive());
	}
	
	public void testSpeed()
	{
		BasicImpl basic = new BasicImpl();
		SingleThreadDelegator<Basic> runner = new SingleThreadDelegator<Basic>();
		runner.start();
		Basic basicTS = runner.instanciate(basic, 
				Basic.class, IOException.class, closeMethod);

		long startTime = System.currentTimeMillis();
		int numCalls = 1000000;
		for(int i=0; i<numCalls; i++){
			int ret = basicTS.getInt();
		}
		long total = System.currentTimeMillis() - startTime;
		System.out.println("total time: " + total);
		System.out.println("ms per call: " + ((double)total)/((double)numCalls));
		
		runner.quit();
	}
}
