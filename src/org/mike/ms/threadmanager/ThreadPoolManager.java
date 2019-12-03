/**
 * 
 */
package org.mike.ms.threadmanager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.StatusCode;

/**
 * @author c
 *
 */
public class ThreadPoolManager {
	ConcurrentHashMap<String, Service> threadMap=new ConcurrentHashMap<String, Service>();
	Executor executor=Executors.newCachedThreadPool();
	static ThreadPoolManager threadManager;
	public static ThreadPoolManager getThreadPoolManager() {
		if(threadManager==null)
			threadManager=new ThreadPoolManager();
		return threadManager;
	}
	private ThreadPoolManager(){
		
	}
	public<T extends Service> ThreadInterface<T> getThreadInterface() {
		return new ThreadInterface<T>();
	}
	public class ThreadInterface<T extends Service> implements DataInterface<T>{

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "RUN";
		}

		@Override
		public T getData(Class source, String key) {
			// TODO Auto-generated method stub
			if(threadMap.contains(key))
				return (T) threadMap.get(key);
			return null;
		}

		@Override
		public StatusCode saveData(String key, T obj) {
			// TODO Auto-generated method stub
			if(threadMap.contains(key))
				return StatusCode.DATA_SAVE_FAILURE;
			threadMap.put(key, obj);
			execute(obj);
			return StatusCode.SECCESS;
			
		}

		@Override
		public void getUpperLevel(DataInterface<T> upper) {}
		
		public boolean endProcess(String key) {
			if(threadMap.contains(key)) {
				try {
					threadMap.remove(key).close();
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			return false;
		}
		
	}
	private boolean execute(Runnable r) {
		executor.execute(r);
		return false;
		
	}
}
