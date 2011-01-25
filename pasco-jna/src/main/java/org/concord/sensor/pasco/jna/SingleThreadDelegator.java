package org.concord.sensor.pasco.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

// FIXME This class is the same as a class in the labquest-jna project. It should probably be refactored so they use the same class!
public class SingleThreadDelegator <T> extends Thread 
{
	private static final Logger logger = Logger.getLogger(SingleThreadDelegator.class.getCanonicalName());
	private Throwable throwable;
	private Object taskReturn;
	private Class<? extends Throwable> exceptionWrapper;
	private Object instance;
	private Method method;
	private Object[] args;
	private Method terminateMethod;
	private boolean quiting = false;
	private ArrayBlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<Runnable>(5);

	public SingleThreadDelegator()
	{
	}
	
	public T instantiate(T instance, Class<T> interface_,
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
			if(method == null || runnableQueue.size() == 0){
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
			
			if (method != null) {
				try {
					try {
//						logger.info(Thread.currentThread().getName() + ": " + method.getName());
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
			} else if (runnableQueue.size() > 0) {
				Runnable runnable = runnableQueue.poll();
				runnable.run();
				notify();
			}
		}
		
		if(!quiting){
			// If we are here then we have been interrupted
			logger.info("Runner Thread got interrupted");
		}
	}
	
	public synchronized void quit()
	{
		quiting = true;
		notify();
	}
	
	/**
	 * Adds a new runnable to the queue to be run. Note that if the queue is full, this call will BLOCK until space in the queue is made.
	 * @param runnable
	 */
	public synchronized void runOnThread(Runnable runnable) {
		runnableQueue.add(runnable);
		notify();
	}
}
