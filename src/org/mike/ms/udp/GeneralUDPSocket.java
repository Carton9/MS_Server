package org.mike.ms.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
/**
 * 
 * @author mike
 *
 */
public class GeneralUDPSocket extends GeneralService{
	private static int resendTime=5;
	private static char LengthSign='#';
	private static char DataSign='!';
	private static char CompareSign='?';
	private static char MissingSign='~';
	private static char CompleteSign='$';
	int dataLength=500;
	DatagramSocket ds;
	boolean status[]=new boolean[3];
	Queue<DatagramPacket[]> sendingQueue;
	Queue<byte[]> recevieQueue;
	Queue<Integer> resendQueue;
	List<ServiceListener> listenerList;
	Thread sender=new Thread() {
		public void run() {
			System.out.println("Sender Start");
			while(status[1]) {
				System.out.println("Sender running");
				try {
					send();
				}catch(InterruptedException e) {
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				
			}
		}
	};
	public GeneralUDPSocket(int port) throws SocketException {
		 this(new DatagramSocket(port));
	}
	public GeneralUDPSocket(DatagramSocket ds) throws SocketException {
		 this.ds=ds;
		 ds.setSoTimeout(1000);
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "GS";
	}

	public void initialize() {
		// TODO Auto-generated method stub
		if(isInitialized())return;
		listenerList=new CopyOnWriteArrayList<ServiceListener>();
//				new ConcurrentLinkedQueue<ServiceListener>()
//				new ArrayList();
		status[0]=ds.isBound();
		resendQueue=new ConcurrentLinkedQueue<Integer>();
		recevieQueue=new ConcurrentLinkedQueue<byte[]>();
		sendingQueue=new ConcurrentLinkedQueue<DatagramPacket[]>();
		status[1]=ds.isBound();
		sender.start();
	}

	public void execute() {
		// TODO Auto-generated method stub
		HashMap<String,byte[][]> dataMap=new HashMap<String,byte[][]>();
		while(status[1]) {
			try {
				byte[] buffer=new byte[this.dataLength+6];
				DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
				ds.receive(packet);
				String path=packet.getAddress().getHostAddress()+"@"+packet.getPort();
				byte tempBuff[]=packet.getData();
//				byte tempBuff[]=this.trimInArray(packet.getData());
				ByteArrayInputStream bis=new ByteArrayInputStream(tempBuff);
				DataInputStream dis=new DataInputStream(bis);
				char cmd=dis.readChar();
				
				if(cmd==this.LengthSign) {
					int length=dis.readInt();
					byte[][] temp=new byte[length][];
					dataMap.put(path, temp);
				}else if(cmd==this.DataSign) {
					int loc=dis.readInt();
					int length=tempBuff.length-6;
					byte[] temp=new byte[length];
					dis.read(temp,0,length);
					byte[][] list=dataMap.remove(path);
					list[loc]=temp;
					dataMap.put(path,list);
				}else if(cmd==this.CompareSign) {
					byte[][] temp=dataMap.remove(path);
					ByteArrayOutputStream bos=new ByteArrayOutputStream();
					DataOutputStream dos=new DataOutputStream(bos);
					ArrayList<Integer> missList=new ArrayList<Integer>();
					for(int i=0;i<temp.length;i++) {
						if(temp[i]==null) {
							missList.add(i);
							
						}
					}
					if(missList.isEmpty()) {
						ByteArrayOutputStream resultBOS=new ByteArrayOutputStream();
						dos.writeChar(this.CompleteSign);
						byte[] buffer2=bos.toByteArray();
						for(int i=0;i<temp.length;i++) {
							resultBOS.write(temp[i]);
						}
						recevieQueue.add(this.trimInArray(resultBOS.toByteArray()));
						this.updateListener(packet.getAddress(),packet.getPort());
						ds.send(new DatagramPacket(buffer2,buffer2.length,packet.getAddress(),packet.getPort()));
					}else {
						dos.writeChar(this.MissingSign);
						for(Integer i:missList) {
							dos.writeInt(i);
						}
						byte[] buffer2=bos.toByteArray();
						dataMap.put(path, temp);
						ds.send(new DatagramPacket(buffer2,buffer2.length,packet.getAddress(),packet.getPort()));
					}
				}else if(cmd==this.MissingSign) {
					//missing hole
					synchronized (resendQueue) {
						if(dis.available()==0)
							this.resendQueue.notifyAll();
						else {
							while(dis.available()!=0) {
								resendQueue.add(dis.readInt());
							}
							this.resendQueue.notifyAll();
						}
					}
				}else if(cmd==this.CompleteSign) {
					synchronized (resendQueue) {
						if(!resendQueue.isEmpty())
							resendQueue=new ConcurrentLinkedQueue<Integer>();
						this.resendQueue.notifyAll();
					}
					
				}
			}catch(SocketTimeoutException e){
				
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	private  static byte[] trimInArray(byte[] raw) {
		int count=0;
		for(int i=raw.length-1;i>0;i--) {
			if(raw[i]==0)
				count++;
			else
				break;
		}
		return Arrays.copyOf(raw,raw.length- count);
	}
	public void finish() {
		// TODO Auto-generated method stub
		this.ds.close();
	}

	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return status[0];
	}

	public boolean isExecuted() {
		// TODO Auto-generated method stub
		return status[1];
	}

	public boolean isFinish() {
		// TODO Auto-generated method stub
		return status[2];
	}

	public void addListener(ServiceListener l) {
		if(listenerList==null)
			this.initialize();
		listenerList.add(l);
	}
	public void addRecevieListener(ReceiveListener l) {
		if(listenerList==null)
			this.initialize();
		listenerList.add(l);
	}
	protected void updateListener(InetAddress address,int port) {
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("data", this.recevieQueue.poll());
		map.put("address", address);
		map.put("port", port);
		synchronized (listenerList) {
			for(ServiceListener i:listenerList) {
				i.action(map);
				if(i.isFinish())listenerList.remove(i);
			}
		}
	}

	protected void callListener(HashMap<String, Object> map) {

	}
	public void send(byte[] data,int port,InetAddress laddr) {
		if(data.length<1)
			return;
		try {
			
			ByteArrayInputStream bis=new ByteArrayInputStream(data);
			ArrayList<DatagramPacket> dataGroup=new ArrayList<DatagramPacket>();
			byte[] buffer=new byte[this.dataLength];
			int count=0;
			while(bis.available()!=0) {
				int length=0;
				length = bis.read(buffer);
				buffer=Arrays.copyOf(buffer, length);
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				DataOutputStream dos=new DataOutputStream(bos);
				dos.writeChar(this.DataSign);
				dos.writeInt(count);
				dos.write(buffer,0,buffer.length);
				buffer=bos.toByteArray();
				dataGroup.add(new DatagramPacket(buffer, buffer.length,laddr, port));
				count++;
				buffer=new byte[this.dataLength];
			}
			
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			DataOutputStream dos=new DataOutputStream(bos);
			dos.writeChar(this.LengthSign);
			dos.writeInt(count);
			buffer=bos.toByteArray();
			dataGroup.add(0,new DatagramPacket(buffer, buffer.length,laddr, port));
			sendingQueue.add(dataGroup.toArray(new DatagramPacket[count+1]));
			synchronized (sendingQueue) {
				sendingQueue.notifyAll();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	private void send() throws IOException, InterruptedException {
		if(!sendingQueue.isEmpty()) {
			DatagramPacket[] datas=sendingQueue.poll();
			for(DatagramPacket i:datas) {
				ds.send(i);
				Thread.sleep(1);
			}
			int resend=0;
			while(true) {
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				DataOutputStream dos=new DataOutputStream(bos);
				dos.writeChar(this.CompareSign);
				byte[] buffer=bos.toByteArray();
				ds.send(new DatagramPacket(buffer, buffer.length,datas[0].getAddress(), datas[0].getPort()));
				synchronized (resendQueue) {
					resendQueue.wait();
				}
				if(!resendQueue.isEmpty()) {
					while(!resendQueue.isEmpty()) {
						ds.send(datas[resendQueue.poll()]);
					}
					resend++;
					if(resend>=this.resendTime)
						break;
				}else {
					break;
				}
			}
				
		}else {
			synchronized (sendingQueue) {
				sendingQueue.wait();
			}
		}
	}
//	ByteArrayInputStream bis=new ByteArrayInputStream(data);
//	ArrayList<UnitData> dataGroup=new ArrayList<UnitData>();
//	byte[] buffer=new byte[this.dataLength];
//	int count=0;
//	while(bis.available()!=0) {
//		int length=0;
//		try {
//			length = bis.read(buffer);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			HashMap<String,Object> errorMap=new HashMap<String,Object>();
//			errorMap.put("source", "send");
//			errorMap.put("error", "IOException");
//			errorMap.put("messege", e.getMessage());
//			this.callListener(errorMap);
//		}
//		UnitData newData=new UnitData(Arrays.copyOf(buffer, length), port, laddr,count,id);
//		dataGroup.add(newData);
//		count++;
//	}
//	sendingQueue.add(dataGroup.toArray(new UnitData[dataGroup.size()]));
	@Override
	protected void updateListener() {
		// TODO Auto-generated method stub
		
	}
}
