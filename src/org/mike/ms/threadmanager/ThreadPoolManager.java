/**
 * 
 */
package org.mike.ms.threadmanager;

import java.util.concurrent.ConcurrentHashMap;

import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.StatusCode;

/**
 * @author c
 *
 */
public class ThreadPoolManager {
	ConcurrentHashMap<String, Runnable> threadMap=new ConcurrentHashMap<String, Runnable>();
	class ThreadInterface<T extends Runnable> implements DataInterface<T>{

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "ThreadPoolManager";
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
			return null;
		}

		@Override
		public void getUpperLevel(DataInterface<T> upper) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public boolean execute(Runnable r) {

		return false;
		
	}
}
