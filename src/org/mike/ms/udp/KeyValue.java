/**
 * 
 */
package org.mike.ms.udp;

/**
 * @author c
 *
 */
public class KeyValue<K,V> {
	public V t;
	public K k;
	public KeyValue(K k,V t) {
		this.t=t;
		this.k=k;
	}
}
