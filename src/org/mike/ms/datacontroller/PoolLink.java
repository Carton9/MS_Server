/**
 * 
 */
package org.mike.ms.datacontroller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Remote;
import java.util.Arrays;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.udp.GeneralUDPSocket;
import org.mike.ms.udp.ReceiveListener;

/**
 * @author c
 *
 */
public class PoolLink<T> implements DataInterface<T> {
	ConcurrentHashMap<String,RemoteInfo> RemoteCase=new ConcurrentHashMap<String,RemoteInfo>();
	GeneralUDPSocket generalUDPSocket;
	DataInterface<T> upper;
	class RemoteInfo{
		String query;
		String type;
		boolean isReplay;
		StatusCode statusCode;
		Object obj;
		public RemoteInfo clone(RemoteInfo info) {
			query=info.query;
			type=info.type;
			isReplay=info.isReplay;
			statusCode=info.statusCode;
			obj=info.obj;
			return this;
		}
	}
	public PoolLink(){
		while(true) {
			try {
				generalUDPSocket=new GeneralUDPSocket(9090);
				generalUDPSocket.addRecevieListener(new ReceiveListener() {
					
					@Override
					public boolean verify(byte[] data, InetAddress ip, int port) {
						// TODO Auto-generated method stub
						RemoteInfo rm;
						try {
							rm = formateData(data);
						} catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						}
						if(rm!=null&&!RemoteCase.contains(rm.query))return true;
						return false;
					}
					
					@Override
					public void process(byte[] data, InetAddress ip, int port) {
						// TODO Auto-generated method stub
						RemoteInfo rm;
						try {
							rm = formateData(data);
						} catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						RemoteInfo rp=new RemoteInfo();
						if(rm.isReplay)return;
						rp.isReplay=true;
						rp.query=rm.query;
						rp.type=rm.type;
						if (rm.type.equals("READ")) {
							Object obj=upper.getData(this.getClass(), rm.query);
							if(obj==null) {
								rp.statusCode=StatusCode.ACCESS_FAILURE;
							}else {
								rp.statusCode=StatusCode.SECCESS;
								rp.obj=obj;
							}
						}else if(rm.type.equals("WRITE")) {
							upper.saveData(rm.query, (T) rm.obj);
							rp.statusCode=StatusCode.SECCESS;
							
						}
						try {
							generalUDPSocket.send(formateData(rp), port, ip);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
					}

					@Override
					public boolean isFinish() {
						// TODO Auto-generated method stub
						return false;
					}
				});
				return;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PoolLink";
	}

	@Override
	public T getData(Class source, String key) {
		// TODO Auto-generated method stub
		String secret=KeyUnit.SHA512(UUID.randomUUID().toString());
		String query="REMOTE_R_"+secret+"@"+key;
		RemoteInfo remoteInfo=new RemoteInfo();
		remoteInfo.query=query;
		remoteInfo.type="READ";
		remoteInfo.isReplay=false;
		RemoteCase.put(query, remoteInfo);
		
		generalUDPSocket.addRecevieListener(new ReceiveListener() {
			boolean finish=false;
			@Override
			public boolean verify(byte[] data, InetAddress ip, int port) {
				RemoteInfo rm;
				try {
					rm = formateData(data);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				if(query.equals(rm.query))return true;
				return false;
			}
			
			@Override
			public void process(byte[] data, InetAddress ip, int port) {
				// TODO Auto-generated method stub
				RemoteInfo rm;
				try {
					rm = formateData(data);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				RemoteCase.get(query).clone(rm).notifyAll();
				finish=true;
			}

			@Override
			public boolean isFinish() {
				// TODO Auto-generated method stu
				return finish;
			}
		});
		
		try {
			generalUDPSocket.send(formateData(remoteInfo), 9090, InetAddress.getByName("255.255.255.255"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		for(int i=0;i<100;i++) {
			try {
				RemoteCase.get(query).wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RemoteInfo rm=RemoteCase.get(query);
				if(rm.statusCode==StatusCode.SECCESS&&rm.isReplay&&rm.obj!=null) {
					return (T) rm.obj;
				}
			}
			
		}
		return null;
	}

	@Override
	public StatusCode saveData(String key, T obj) {
		// TODO Auto-generated method stub
		boolean stop=false;
		String secret=KeyUnit.SHA512(UUID.randomUUID().toString());
		String query="REMOTE_W_"+secret+"@"+key;
		RemoteInfo remoteInfo=new RemoteInfo();
		remoteInfo.query=query;
		remoteInfo.type="WRITE";
		remoteInfo.isReplay=false;
		remoteInfo.obj=obj;
		RemoteCase.put(query, remoteInfo);
		
		generalUDPSocket.addRecevieListener(new ReceiveListener() {
			boolean finish=false;
			@Override
			public boolean verify(byte[] data, InetAddress ip, int port) {
				RemoteInfo rm;
				try {
					rm = formateData(data);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				if(query.equals(rm.query))return true;
				return false;
			}
			int count=0;
			@Override
			public void process(byte[] data, InetAddress ip, int port) {
				// TODO Auto-generated method stub
				RemoteInfo rm;
				try {
					rm = formateData(data);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					finish=true;
					return;
				}
				RemoteCase.get(query).clone(rm).notifyAll();
				finish=(rm.statusCode==StatusCode.SECCESS);
			}

			@Override
			public boolean isFinish() {
				// TODO Auto-generated method stu
				if(RemoteCase.contains(query))
					return finish;
				return false;
			}
		});
		try {
			generalUDPSocket.send(formateData(remoteInfo), 9090, InetAddress.getByName("255.255.255.255"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return StatusCode.DATA_SAVE_FAILURE;
		}
		for(int i=0;i<100;i++) {
			try {
				RemoteCase.get(query).wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RemoteInfo rm=RemoteCase.get(query);
				if(rm.statusCode==StatusCode.SECCESS&&rm.isReplay) {
					return rm.statusCode;
				}
			}
			
		}
		RemoteCase.remove(query);
		return StatusCode.ENTRY_NOT_FOUND;
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub;
		this.upper=upper;
	}
	private RemoteInfo formateData(byte[] data) throws ClassNotFoundException, IOException {
		ByteArrayInputStream bais=new ByteArrayInputStream(data);
		DataInputStream dis=new DataInputStream(bais);
		ObjectInputStream ois=new ObjectInputStream(dis);
		int ack=ois.read();
		int length=ois.readInt();
		byte[] id=new byte[length];
		ois.read(id);
		String query=new String(id);
		RemoteInfo remoteInfo=new RemoteInfo();
		if(!query.contains("REMOTE"))return null;
		remoteInfo.query=query;
		if(query.contains("REMOTE_R"))remoteInfo.type="READ";
		else if(query.contains("REMOTE_W"))remoteInfo.type="WRITE";
		if(ack==000) {
			remoteInfo.isReplay=false;
		}else {
			remoteInfo.isReplay=true;
		}
		remoteInfo.statusCode=StatusCode.formateCode(ack-1);
		if(ack==StatusCode.SECCESS.getCode()+1) {
			remoteInfo.obj=ois.readObject();
		}
		return remoteInfo;
	}
	private byte[] formateData(RemoteInfo data) throws IOException {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(bos);
		if(!data.isReplay) {
			oos.write(000);
			oos.writeInt(data.query.length());
			oos.write(data.query.getBytes());
		}else {

			oos.write(data.statusCode.getCode()+1);
			if(data.statusCode==StatusCode.SECCESS) {
				oos.writeInt(data.query.length());
				oos.write(data.query.getBytes());
				oos.writeObject(data.obj);
			}else {
				oos.writeInt(data.query.length());
				oos.write(data.query.getBytes());
			}
		}
		return bos.toByteArray();
	}

}
