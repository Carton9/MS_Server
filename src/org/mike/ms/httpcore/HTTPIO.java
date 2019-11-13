/**
 * 
 */
package org.mike.ms.httpcore;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.datacontroller.DataInterface;

/**
 * @author c
 *
 */
public class HTTPIO implements Runnable, Closeable{
	ServerSocket ss=null;
	Socket cs=null;
	String socketKey="";
	DataInterface<HTTPIO> clientCache;
	DataInterface<HTTPCase> dataLink;
	boolean run=true;
	public HTTPIO(int port) throws IOException {
		ss=new ServerSocket(port);
	}
	HTTPIO(Socket cs,String key) throws IOException {
		this.cs=cs;
		socketKey=key;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(ss==null) {
			clientProcess();
		}else {
			listenerProcess();
		}
	}
	private void clientProcess() {
		String iDString="";
		while(run) {
			try {
				BufferedInputStream bis = new BufferedInputStream(cs.getInputStream());
				String remoteAddr=cs.getRemoteSocketAddress().toString();
				ArrayList<byte[]> data=new ArrayList<byte[]>();
				int size=0;
				while(true) {
					int avaliable=bis.available();
					if(avaliable<=0)break;
					byte[] dataBuff=new byte[4096];
					int length=bis.read(dataBuff);
					data.add(Arrays.copyOf(dataBuff, length));
					size+=length;
				}
				byte[] finalData=new byte[size];
				int pointer=0;
				for(int i=0;i<data.size();i++) {
					System.arraycopy(data.get(i), 0, finalData, pointer, data.get(i).length);
					pointer+=data.get(i).length;
				}
				String time=LocalDate.now().toString()+" "+LocalTime.now().toString();
				HTTPCase newCase=new HTTPCase();
				iDString=socketKey+"@RAW:"+"TIME$"+time+",ADDR$"+remoteAddr;
				newCase.setCaseNumber(iDString);
				newCase.setRawDataReceive(finalData);
				dataLink.saveData(iDString,newCase);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(cs.isClosed())break;
				else continue;
			}
			
			try {
				HTTPCase replayCase=dataLink.getData(this.getClass(),iDString.replace("RAW", "PASS"));
				BufferedOutputStream bos = new BufferedOutputStream(cs.getOutputStream());
				if(replayCase!=null)
					bos.write(replayCase.getRawDataSend());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(cs.isClosed())break;
			}
			
		}
	}
	private void listenerProcess() {
		while(run) {
			Socket client;
			try {
				client = ss.accept();
				String key=KeyUnit.SHA512(System.currentTimeMillis()+" "+client.getRemoteSocketAddress().hashCode());
				clientCache.saveData(key,new HTTPIO(client,key) );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		run=false;
		ss.close();
	}
	
}
