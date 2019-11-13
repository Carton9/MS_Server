/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author c
 *
 */
public class DataPool<T> implements DataInterface<T> {
	static int DataPoolCount=0;
	int id=0;
	CopyOnWriteArrayList<DataInterface<T>> interfaces=new CopyOnWriteArrayList<DataInterface<T>>();
	public DataPool() {
		id=DataPoolCount++;
	}
	public void addInterface(DataInterface<T> newInterface) {
		interfaces.add(newInterface);
	}
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		try {
			return getDataFromOtherInterface(key);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean saveData(String key, T obj) {
		// TODO Auto-generated method stub
		try {
			return saveDataToInterface(key, obj);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "DATAPOOL-"+id;
	}
	private boolean saveDataToInterface(String key, T obj) throws InterruptedException{
		Thread threadList[]=new Thread[interfaces.size()];
		boolean result[]=new boolean[interfaces.size()];
		for(int i=0;i<interfaces.size();i++) {
			int pointer=i;
			Runnable query=new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					interfaces.get(pointer).saveData(key,obj);
				}
				
			};
			threadList[i]=new Thread(query);
			threadList[i].start();	
		}
		int pointer=0;
		int finish=0;
		while(true) {
			if(!threadList[pointer].isAlive()) {
				if(result[pointer])return result[pointer];
				else finish++;
			}
			pointer++;
			if(pointer==interfaces.size())Thread.sleep(20);
			pointer%=interfaces.size();
			if(finish==interfaces.size())return false;
		}
	}
	private T getDataFromOtherInterface(String key) throws InterruptedException {
		Object result[]=new Object[interfaces.size()];
		Thread threadList[]=new Thread[interfaces.size()];
		for(int i=0;i<interfaces.size();i++) {
			int pointer=i;
			Runnable query=new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					result[pointer]=interfaces.get(pointer).getData(DataPool.class,key);
				}
				
			};
			threadList[i]=new Thread(query);
			threadList[i].start();
		}
		int pointer=0;
		int finish=0;
		while(true) {
			if(!threadList[pointer].isAlive()) {
				if(result[pointer]!=null)return (T)result[pointer];
				else finish++;
			}
			pointer++;
			if(pointer==interfaces.size())Thread.sleep(20);
			pointer%=interfaces.size();
			if(finish==interfaces.size())return null;
		}
	}
	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		
	}
}
