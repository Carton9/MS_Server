package org.mike.ms.udp;

import java.util.HashMap;
/**
 * 
 * @author mike
 *
 */
public abstract class GeneralService {
	/**
	 * The method will return a unique name as the key in save the {@code GeneralServiceExecutePool} 
	 * @return
	 */
	public abstract String getName();
	/**
	 * Initialize the service, any action that is nessary to run before can execute in pool
	 */
	public abstract void initialize();
	/**
	 * All program that need to run in the pool. {@literal}Note: this method will only call once.
	 */
	public abstract void execute();
	/**
	 * Any action need to run before can safly close this service.
	 */
	public abstract void finish();
	/**
	 * return a value that show this service has been initialized before
	 * @return
	 */
	public abstract boolean isInitialized();
	/**
	 * return a value that show this service has been executed before
	 * @return
	 */
	public abstract boolean isExecuted();
	/**
	 * return a value that show this service has been finish before
	 * @return
	 */
	public abstract boolean isFinish();
	/**
	 * add listener to the listener list
	 * @param l
	 */
	public abstract void addListener(ServiceListener l);
	/**
	 * the inner method to call the listener
	 */
	protected abstract void updateListener();
	/**
	 * call all listener with value
	 * @param map
	 */
	protected abstract void callListener(HashMap<String,Object> map);
}
