/**
 * 
 */
package pending;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.mike.ms.datacontroller.GeneralCache;
import org.mike.ms.datacontroller.StatusCode;
import org.mike.ms.httpcore.HTTPCase;

/**
 * @author c
 *
 */
public class DelayCache2<T extends HTTPCase> extends GeneralCache<T> {
	ConcurrentLinkedQueue<Object> queryList=new ConcurrentLinkedQueue<Object>();
	static int tryCount=200;
	/**
	 * @param type
	 */
	public DelayCache2(String type) {
		super(type);
		// TODO Auto-generated constructor stub
	}
	public DelayCache2(String type,boolean isRemove,boolean isAutoName){
		super(type,isRemove,isAutoName);
	}
	public T getData(Class source,String key) {
		int count=0;
		T result=null;
		Object sleep=new Object();
		while(result==null) {
			System.out.print(System.currentTimeMillis()+" ");
			result=super.getData(source, key);
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
		StatusCode sc=super.saveData(key, obj);
		for(Object i:queryList) {
		
			synchronized(i) {
				i.notifyAll();
			}
		}
		return sc;
	}
}
