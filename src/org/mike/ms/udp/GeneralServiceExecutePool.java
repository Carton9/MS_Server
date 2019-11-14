package org.mike.ms.udp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 
 * @author mike
 *
 */
public class GeneralServiceExecutePool {
	ConcurrentHashMap<String,PoolUnit> list;
	ExecutorService pool;
	int MAXSIZE=2000;
	AtomicBoolean isClose;
	class PoolUnit implements Runnable{
		GeneralService service;
		public PoolUnit(GeneralService service) {
			this.service=service;
		}
		public GeneralService getService() {
			return service;
		}
		@Override
		public void run() {
			service.execute();
		}
	}
	/**
	 * Create a execute pool for any class that extends from {@code GeneralService} 
	 * will automticly initialize and  added in to an Executor.
	 */
	public GeneralServiceExecutePool() {
		pool=Executors.newCachedThreadPool();
		list=new ConcurrentHashMap<String,PoolUnit>();
		isClose=new AtomicBoolean(false);
	}
	/**
	 * try to initialize and execute service in an execute pool
	 * @param service
	 * @return
	 */
	public boolean lunchUnit(GeneralService service) {
		if(isClose.get())
			return false;
		for(int i=0;i<3;i++) {
			if(!service.isInitialized())
				service.initialize();
		}
		if(!service.isInitialized()) 
			return false;
		PoolUnit unit=new PoolUnit(service);
		String name=service.getName();
		String finalName=name;
		int count=0;
		while(list.containsKey(finalName)) {
			finalName=name+" "+count;
			count++;
		}
		list.put(finalName, unit);
		pool.submit(unit);
		return true;
	}
	/**
	 * get service in the pool by hte service name
	 * @param key
	 * @return
	 */
	public GeneralService getService(String key) {
		return list.get(key).getService();
	}
	/**
	 * close all the service in the execute pool
	 */
	public void closePool() {
		isClose.set(true);
		for(PoolUnit i:list.values()) {
			i.getService().finish();
		}
		pool.shutdown();
		pool.shutdownNow();
	}
}
