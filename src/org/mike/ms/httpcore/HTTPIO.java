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
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.Query;
import org.mike.ms.threadmanager.Service;

/**
 * @author c
 *
 */
public class HTTPIO implements Service,HTTPCoreDIPInterface{
	ServerSocket ss=null;
	Socket cs=null;
	String socketKey="";
	DataInterface<HTTPIO> clientCache;
	DataInterface<HTTPCase> dataLink;
	Logger log;
	HTTPResourceCentor rc;
	boolean run=true;
	public HTTPIO(int port) throws IOException {
		ss=new ServerSocket(port);
		log=Logger.getGlobal();
	}
	HTTPIO(Socket cs,String key) throws IOException {
		this.cs=cs;
		socketKey=key;
		log=Logger.getGlobal();
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
				if(size<4) {
					cs.close();
					run=false;
					return;
				}
				for(int i=0;i<data.size();i++) {
					System.arraycopy(data.get(i), 0, finalData, pointer, data.get(i).length);
					pointer+=data.get(i).length;
				}
				String time=LocalDate.now().toString()+"*"+LocalTime.now().toString();
				HTTPCase newCase=new HTTPCase();
				HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
				HashMap<String, String> timeMap=new HashMap<String, String>();
				timeMap.put("VALUE", time);
				HashMap<String, String> remoteAddrMap=new HashMap<String, String>();
				remoteAddrMap.put("VALUE", remoteAddr);
				valueFile.put("TIME", timeMap);
				valueFile.put("ADDR", remoteAddrMap);
				Query q=Query.initAQuery(socketKey, "RAW", valueFile);
				iDString=Query.formate(q);
				newCase.setCaseNumber(iDString);
				newCase.setRawDataReceive(finalData);
				dataLink.saveData
				(iDString,
						newCase);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(cs.isClosed())break;
				else continue;
			}
			
			try {
				
				HTTPCase replayCase=dataLink.getData(this.getClass(),iDString.replace("RAW", "PASS"));
				while(replayCase==null) {
					replayCase=dataLink.getData(this.getClass(),iDString.replace("RAW", "PASS"));
				}
				
				BufferedOutputStream bos = new BufferedOutputStream(cs.getOutputStream());
				if(replayCase!=null){
					
					if(replayCase.getRawDataSend()!=null&&replayCase.getRawDataSend().length>4){
//						Logger.getGlobal().info(new String(replayCase.getRawDataSend())+"");
						bos.write(replayCase.getRawDataSend());
						bos.flush();
					}
					else if(replayCase.getCode()!=null){
						String reply="HTTP/1.1 "+replayCase.getCode().getErrorCode()+" "+replayCase.getCode().getNote()+"\nContent-type: text/html\n\n";
						bos.write(reply.getBytes());
					}
					else {
						String reply="HTTP/1.1 "+HTTPCode.SERVICE_UNAVAILABLE.errorCode+" "+HTTPCode.SERVICE_UNAVAILABLE.getNote()+"\nContent-type: text/html\n\n";
						bos.write(reply.getBytes());
					}
				}
				bos.flush();
				cs.close();
				run=false;
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
				log.info("Get A Client at "+client.getRemoteSocketAddress());
				String key=KeyUnit.MD5(System.currentTimeMillis()+" "+client.getRemoteSocketAddress().hashCode());
				client.setKeepAlive(true);
				HTTPIO io=new HTTPIO(client,key);
				io.SetHTTPResourceCentor(rc);
				clientCache.saveData(key,io);
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
	@Override
	public void SetHTTPResourceCentor(HTTPResourceCentor rc) {
		// TODO Auto-generated method stub
		dataLink=rc.getIODataInterface();
		clientCache=rc.getHTTPIOInterface();
		this.rc=rc;
	}
	
}
