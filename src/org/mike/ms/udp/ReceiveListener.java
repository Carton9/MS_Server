package org.mike.ms.udp;

import java.net.InetAddress;
import java.util.Map;
import java.util.Queue;

/**
 * 
 * @author mike
 *
 */
public abstract class ReceiveListener implements ServiceListener {

	public ReceiveListener() {
		
	}

	@Override
	public final void action(Map<String, Object> result) {
		// TODO Auto-generated method stub
		byte[] recevieQ=(byte[])result.get("data");
		InetAddress address=(InetAddress)result.get("address");
		int port=(int)result.get("port");
		if(verify(recevieQ,address,port)) {
			process(recevieQ,address,port);
		}
	}
	public abstract boolean verify(byte[] data,InetAddress ip,int port);
	public abstract void process(byte[] data,InetAddress ip,int port);
}
