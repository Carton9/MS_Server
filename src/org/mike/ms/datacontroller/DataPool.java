/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.mike.ms.threadmanager.Service;

/**
 * @author c
 *
 */
public class DataPool{
//	CopyOnWriteArrayList<DataInterface<T>> interfaces=new CopyOnWriteArrayList<DataInterface<T>>();
	@SuppressWarnings("rawtypes")
	ConcurrentHashMap<String, ArrayList<DataInterface>> interfaces=new ConcurrentHashMap<String, ArrayList<DataInterface>>();
	public DataPool() {
	}
	public <T> void addInterface(DataInterface<T> newInterface) {
		if(newInterface==null||newInterface.getName().equals(""))return;
		if(interfaces.containsKey(newInterface.getName()))
			interfaces.get(newInterface.getName()).add(newInterface);
		else {
			ArrayList<DataInterface> newList=new ArrayList<DataInterface>();
			newList.add(newInterface);
			interfaces.put(newInterface.getName(), newList);
		}
		newInterface.getUpperLevel(getInterface());
	}
	public <T> DataInterface<T> getInterface(){
		return new DataInterface<T>() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "DataPool";
			}

			@Override
			public T getData(Class source, String key) {
				// TODO Auto-generated method stub
				
				try {
					return getDataFromPool(source, key);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public StatusCode saveData(String key, T obj) {
				// TODO Auto-generated method stub
				try {
					return saveDataToPool(key,obj);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return StatusCode.DATA_SAVE_FAILURE;
				}
			}

			@Override
			public void getUpperLevel(DataInterface<T> upper) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

	
	private <T> T getDataFromPool(Class source, String key) throws InterruptedException {
		Query q=Query.formate(key);
		boolean isRemote=false;
		for(String p:q.path) {
			isRemote|=p.contains("REMOTE");
		}
		String type=q.path.get(q.path.size()-1);
		T obj=null;
		if(key.contains("PASS")||key.contains("CASE")||key.contains("RAW"))isRemote=true;
		if(!interfaces.containsKey(type)) {
			if(!isRemote){
				obj=getDataFromOtherInterface(key, source,interfaces.get("PoolLink"));
			}
			return obj;
		}else {
			obj=getDataFromOtherInterface(key,source,interfaces.get(type));
			if(obj==null&&!isRemote){
				Logger.getGlobal().info(key);
				obj=getDataFromOtherInterface(key,source,interfaces.get("PoolLink"));
			}
			return obj;
		}
		
	}
	private<T> StatusCode saveDataToPool(String key, T obj) throws InterruptedException{
		Query q=Query.formate(key);
		boolean isRemote=false;
		for(String p:q.path) {
			isRemote|=p.contains("REMOTE");
		}
		String type=q.path.get(q.path.size()-1);
		if(key.contains("PASS")||key.contains("CASE")||key.contains("RAW")||key.contains("RUN"))isRemote=true;
		if(!interfaces.containsKey(type)) {
			
			if(!isRemote) {
				return saveDataToInterface(key,obj,interfaces.get("PoolLink"));
			}
			return StatusCode.INTERFACE_NOT_FOUND;
		}else {
			StatusCode saved=saveDataToInterface(key,obj,interfaces.get(type));
			System.out.println("TP2 "+key+" "+interfaces.containsKey(type)+" "+saved);
			StatusCode savedRemote=null;
			if(!isRemote){
				getInterface().saveData("*@RUN", new Service() {
					
					@Override
					public void close() throws Exception {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							saveDataToInterface(key,obj,interfaces.get("PoolLink"));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
					}
				});
				
			}
			return saved;
		}
	}
//	@Override
//	public T getData(Class source,String key) {
//		// TODO Auto-generated method stub
//		try {
//			return getDataFromOtherInterface(key);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	public boolean saveData(String key, T obj) {
//		// TODO Auto-generated method stub
//		try {
//			return saveDataToInterface(key, obj);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return "DATAPOOL-"+id;
//	}
	private<T> StatusCode saveDataToInterface(String key, T obj,ArrayList<DataInterface> ifList) throws InterruptedException{
		if(ifList==null)return StatusCode.DATA_SAVE_FAILURE;
		Thread threadList[]=new Thread[ifList.size()];
		StatusCode result[]=new StatusCode[ifList.size()];
		for(int i=0;i<ifList.size();i++) {
			int pointer=i;
			Runnable query=new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					result[pointer]=ifList.get(pointer).saveData(key,obj);
				}
				
			};
			threadList[i]=new Thread(query);
			threadList[i].start();	
		}
		int pointer=0;
		int finish=0;
		int minCode=10;
		while(true) {
			if(!threadList[pointer].isAlive()) {
				if(result[pointer].getCode()==0)return result[pointer];
				else finish++;
				if(result[pointer].getCode()<minCode)minCode=result[pointer].getCode();
			}
			pointer++;
			if(pointer==ifList.size()) {
				Thread.sleep(20);
				minCode=10;
			}
			pointer%=ifList.size();
			if(finish==ifList.size())return StatusCode.formateCode(minCode);
		}
	}
	private<T> T getDataFromOtherInterface(String key,Class source,ArrayList<DataInterface> ifList) throws InterruptedException {
		if(ifList==null)return null;
		Object result[]=new Object[ifList.size()];
		Thread threadList[]=new Thread[ifList.size()];
		for(int i=0;i<ifList.size();i++) {
			int pointer=i;
			Runnable query=new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					result[pointer]=ifList.get(pointer).getData(source,key);
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
			if(pointer==ifList.size())Thread.sleep(20);
			pointer%=ifList.size();
			if(finish==ifList.size())return null;
		}
	}
}
