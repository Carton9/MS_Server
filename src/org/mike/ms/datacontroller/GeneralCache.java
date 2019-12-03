/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * @author c
 *
 */
public class GeneralCache<T> implements DataInterface<T>{
	ConcurrentHashMap<String, T> cacheMap=new ConcurrentHashMap<String, T>();
	DataInterface<T> upper=null;
	String type;
	boolean isRemove,isAutoName;
	public GeneralCache(String type){
		this(type,false,true);
	}
	public GeneralCache(String type,boolean isRemove,boolean isAutoName){
		this.type=type;
		this.isAutoName=isAutoName;
		this.isRemove=isRemove;
	}
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return null;
		Query q=Query.formate(key);
		if(!q.path.contains(type))return null;
		synchronized(cacheMap) {
		if(cacheMap.isEmpty())return null;
		
		if(q.path.contains("*")) {
			if(isRemove)return cacheMap.remove(cacheMap.keys().nextElement());
			return cacheMap.get(cacheMap.keys().nextElement());
			
		}
		for(String i:q.path) {
			if(cacheMap.containsKey(i)) {
				//Logger.getGlobal().warning(source.getName()+" "+key);
				if(isRemove)return cacheMap.remove(i);
					return cacheMap.get(i);
			}
		}
		return null;
		}
	}
	@Override
	public StatusCode saveData(String key, T obj) {
		// TODO Auto-generated method stub
		
		if(key==null||key.equals(""))return StatusCode.ENTRY_NOT_FOUND;
		//Logger.getGlobal().warning(key);
		Query q=Query.formate(key);
		if(!q.path.contains(type))return StatusCode.INTERFACE_NOT_FOUND;
		cacheMap.put(q.path.get(q.path.size()-2), obj);
		return StatusCode.ENTRY_NOT_FOUND;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		if(isAutoName)return "GeneralCache";
		return type;
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		
	}
}
