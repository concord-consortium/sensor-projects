package org.concord.sensor.labquest.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class SingleThreadDelegator <T> extends Thread 
{
	private Throwable throwable;
	private Object taskReturn;
	private Class<? extends Throwable> exceptionWrapper;
	private Object instance;
	private Method method;
	private Object[] args;
	private Method terminateMethod;
	private boolean quiting = false;

	public SingleThreadDelegator()
	{
	}
	
	public T instanciate(T instance, Class<T> interface_,
			Class<? extends Throwable>throwableWrapper, Method terminateMethod){
		this.instance = instance;		
		this.exceptionWrapper = throwableWrapper;
		this.terminateMethod = terminateMethod;
		
    	InvocationHandler invocationHandler = new InvocationHandler(){

			public Object invoke(Object proxy, Method method,
					Object[] args) throws Throwable {
				return invokeMethod(method, args);
			};				
		};

		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), 
				new Class[] {interface_}, 
				invocationHandler);
	}		
	
	public synchronized Object invokeMethod(Method method, Object [] args) throws Throwable
	{
		if(!isAlive()){
			throw new IllegalStateException(
					"The thread has exited so no more methods can be called");
		}
		if(this.method != null){
			throw new IllegalStateException("this is bad if the stored method is null");
		}
		this.method = method;
		this.args = args;
		taskReturn = null;
		notify();
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		if(throwable != null){
			Throwable wrapper = exceptionWrapper.newInstance();
			wrapper.initCause(throwable);
			throwable = null;
			throw wrapper;
		}
		
		return taskReturn;
	}

	public synchronized void run()
	{
		while(!isInterrupted() && !quiting){
			if(method == null){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		
			if(quiting){
				break;
			}
			
			try {
				try {
					taskReturn =  method.invoke(instance, args);
				} catch (InvocationTargetException e) {
					throw e.getCause();
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			} catch (Throwable t){
				throwable = t;
			}
			if(method.equals(SingleThreadDelegator.this.terminateMethod)){
				method = null;
				quiting = true;
				notify();
				return;
			}

			method = null;
			notify();			
		}
		
		if(!quiting){
			// If we are here then we have been interrupted
			System.err.println("Runner Thread got interrupted");
		}
	}
	
	public synchronized void quit()
	{
		quiting = true;
		notify();
	}
}
