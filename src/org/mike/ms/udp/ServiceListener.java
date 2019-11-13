package org.mike.ms.udp;

import java.util.Map;
/**
 * this listener is use to handle different action in service
 * @author mike
 *
 */
public interface ServiceListener {
	/**
	 * return the listener type
	 * @return
	 */
	public default String listenerType() {
		return "default";
	}
	/**
	 * the method that will call when this listener been called.
	 * @param result
	 */
	public void action(Map<String,Object> result);
	public boolean isFinish();
}
