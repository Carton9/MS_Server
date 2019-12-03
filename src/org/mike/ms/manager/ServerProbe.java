/**
 * 
 */
package org.mike.ms.manager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mike.ms.threadmanager.Service;

/**
 * @author c
 *
 */
public class ServerProbe implements Service{
	static ServerProbe sp;
	private ConcurrentLinkedQueue<String> informations;
	private int correntConntection=0;
	private ArrayList<String> hostedWebsite=new ArrayList<String>();
	public static ServerProbe getDefaultProbe() {
		if(sp==null)
			sp=new ServerProbe();
		return sp;
	}
	private ServerProbe() {
		
	}
	public void addInfo(String source,String info) {
		informations.add(String.format("LOG FROM %7s: %10s", source,info));
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
