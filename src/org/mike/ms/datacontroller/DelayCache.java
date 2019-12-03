/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.mike.ms.httpcore.HTTPCase;

/**
 * @author c
 *
 */
public class DelayCache<T> implements DataInterface<T> {
	ConcurrentLinkedQueue<Object> queryList=new ConcurrentLinkedQueue<Object>();
	int tryCount=200;
	DataInterface<T> gCache;
	/**
	 * @param type
	 */
	public DelayCache(DataInterface<T> gCache) {
		this.gCache=gCache;
	}
	public DelayCache(DataInterface<T> gCache,int delayCount) {
		this.gCache=gCache;
		tryCount=delayCount;
	}
	public T getData(Class source,String key) {
		
		if(gCache==null)return null;
		int count=0;
		T result=null;
		Object sleep=new Object();
		while(result==null) {
			result=gCache.getData(source, key);
			if(result!=null) {
				queryList.remove(sleep);
				return result;
			}
			count++;
			if(count>tryCount)break;
			if(!queryList.contains(sleep))queryList.add(sleep);
			try {
				synchronized(sleep) {
					sleep.wait(500);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		queryList.remove(sleep);
		return result;
	}
	public StatusCode saveData(String key, T obj) {
		StatusCode sc=gCache.saveData(key, obj);
		for(Object i:queryList) {
			synchronized(i) {
				i.notifyAll();
			}
		}
		return sc;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return gCache.getName();
	}
	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		gCache.getUpperLevel(upper);
	}
}
