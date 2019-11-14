/**
 * 
 */
package org.mike.ms.datacontroller;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author c
 *
 */
public interface DataInterface<T> {
	public String getName();
	public T getData(Class source,String key);
	public StatusCode saveData(String key,T obj);
	public void getUpperLevel(DataInterface<T> upper);
}
