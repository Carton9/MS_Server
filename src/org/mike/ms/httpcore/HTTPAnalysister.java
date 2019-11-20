/**
 * 
 */
package org.mike.ms.httpcore;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mike.ms.datacontroller.DataInterface;

/**
 * @author c
 *
 */
public abstract class HTTPAnalysister implements HTTPCoreDIPInterface, Runnable,Closeable{
	DataInterface<String> backEndDataLink;
	DataInterface<HTTPCase> dataLink;
	ConcurrentLinkedQueue<HTTPCase> inQueue=new ConcurrentLinkedQueue<HTTPCase>();
	boolean isClose=false;
	String hostName;
	@Override
	public void run() {
		while(!isClose) {
			HTTPCase newCase=dataLink.getData(this.getClass(), "*@CASE");
			processCase(newCase);
			dataLink.saveData(newCase.getCaseNumber().replace("CASE", "PASS"), newCase);
		}
	}
	@Override
	public void close() {
		isClose=true;
		AnalysisterClose();
	}
	public int addCase(HTTPCase newCase) {
		inQueue.add(newCase);
		return inQueue.size();
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName=hostName;
	}
	public abstract void AnalysisterClose();
	public abstract void processCase(HTTPCase newCase);
	@Override
	public void SetHTTPResourceCentor(HTTPResourceCentor rc) {
		dataLink=rc.getAnalysisterDataInterface();
		backEndDataLink=rc.getBackEndDataInterface();
	}
}
