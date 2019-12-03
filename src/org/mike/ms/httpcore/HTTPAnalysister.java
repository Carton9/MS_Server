/**
 * 
 */
package org.mike.ms.httpcore;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.StatusCode;
import org.mike.ms.threadmanager.Service;

/**
 * @author c
 *
 */
public abstract class HTTPAnalysister implements HTTPCoreDIPInterface, Service{
	DataInterface<String> backEndDataLink;
	DataInterface<HTTPCase> dataLink;
	ConcurrentLinkedQueue<HTTPCase> inQueue=new ConcurrentLinkedQueue<HTTPCase>();
	HTTPContentParser hcp=new HTTPContentParser();
	boolean isClose=false;
	String hostName;
	@Override
	public void run() {
		while(!isClose) {
			HTTPCase newCase=dataLink.getData(this.getClass(), "*@CASE");
			if(newCase==null)continue;
			hcp.parse(newCase);
			processCase(newCase);
			hcp.formate(newCase);
			newCase.setCaseNumber(newCase.getCaseNumber().replace("CASE", "PASS"));
			dataLink.saveData(newCase.getCaseNumber(), newCase);
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
	protected String getData(String key) {
		return backEndDataLink.getData(this.getClass(), key);
	}
	protected StatusCode saveData(String key,String obj) {
		return backEndDataLink.saveData(key,obj);
	}
	@Override
	public void SetHTTPResourceCentor(HTTPResourceCentor rc) {
		dataLink=rc.getAnalysisterDataInterface();
		backEndDataLink=rc.getBackEndDataInterface();
	}
}
