/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author c
 *
 */
public class GeneralCache<T> implements DataInterface<T>{
	ConcurrentHashMap<String, T> cacheMap=new ConcurrentHashMap<String, T>();
	DataInterface<T> upper=null;
	String type;
	int id=0;
	static int count=0;
	public GeneralCache(String type){
		this.type=type;
		id=count++;
	}
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return null;
		String[] query=key.split("@");
		if(!query[query.length-1].contains(type))return null;
		for(int i=query.length-2;i<0;i--) {
			if((query[i].contains(getName())||query[i].equals("*")))return cacheMap.get(query[query.length-1]);
		}
		return null;
	}
	@Override
	public StatusCode saveData(String key, T obj) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return StatusCode.ENTRY_NOT_FOUND;
		String[] query=key.split("@");
		if(!query[query.length-1].contains(type))return StatusCode.INTERFACE_NOT_FOUND;
		for(int i=query.length-2;i<0;i--) {
			if(query[i].contains(getName())) {
				cacheMap.put(query[query.length-1],obj);
				return StatusCode.SECCESS;
			}
		}
		return StatusCode.ENTRY_NOT_FOUND;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GeneralCache-"+type+id;
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		
	}
}
