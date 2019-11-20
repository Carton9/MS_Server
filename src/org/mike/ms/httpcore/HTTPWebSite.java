/**
 * 
 */
package org.mike.ms.httpcore;

import java.util.ArrayList;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.DataPool;
import org.mike.ms.datacontroller.Query;
import org.mike.ms.datacontroller.StatusCode;
import org.mike.ms.threadmanager.ThreadPoolManager;

/**
 * @author c
 *
 */
public class HTTPWebSite {
	ArrayList<HTTPAnalysister> HTTPAnalysisterList;
	ArrayList<HTTPParser> HTTPParserList;
	ArrayList<HTTPIO> HTTPIOList;
	HTTPIO listeningPort;
	String hostName;
	HTTPResourceCentor websiteResourceCentor;
	DataPool pool;
	ThreadPoolManager tpm;
	private int[] sizeScale= {1,2,2};
	public HTTPWebSite(String hostName,DataPool pool,ThreadPoolManager tpm) {
		this.hostName=hostName;
		this.pool=pool;
		this.tpm=tpm;
		constructWebsiteResourceCentor();
	}
	private void constructWebsiteResourceCentor() {
		DataInterface<HTTPCase> mainDif=pool.getInterface();
		DataInterface<String> backEnd=pool.getInterface();
		DataInterface<HTTPCase> iodif=mainDif;
		DataInterface<HTTPCase> padif=new DataInterface<HTTPCase>() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return mainDif.getName();
			}

			@Override
			public HTTPCase getData(Class source, String key) {
				// TODO Auto-generated method stub
				return mainDif.getData(source, key);
			}

			@Override
			public StatusCode saveData(String key, HTTPCase obj) {
				// TODO Auto-generated method stub
				return mainDif.saveData(key.replace("CASE", "CASE_"+KeyUnit.MD5(hostName)), obj);
			}

			@Override
			public void getUpperLevel(DataInterface<HTTPCase> upper) {
				// TODO Auto-generated method stub
				mainDif.getUpperLevel(upper);
			}
			
		};
		DataInterface<HTTPCase> andif=new DataInterface<HTTPCase>() {
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return mainDif.getName();
			}

			@Override
			public HTTPCase getData(Class source, String key) {
				// TODO Auto-generated method stub
				return mainDif.getData(source,key.replace("CASE", "CASE_"+KeyUnit.MD5(hostName)));
			}

			@Override
			public StatusCode saveData(String key, HTTPCase obj) {
				// TODO Auto-generated method stub
				return mainDif.saveData(key, obj);
			}

			@Override
			public void getUpperLevel(DataInterface<HTTPCase> upper) {
				// TODO Auto-generated method stub
				mainDif.getUpperLevel(upper);
			}
		};
		DataInterface<String> enddif=new DataInterface<String>() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return mainDif.getName();
			}

			@Override
			public String getData(Class source, String key) {
				// TODO Auto-generated method stub
				Query q=Query.formate(key);
				q.path.set(q.path.size()-1, q.path.get(q.path.size()-1)+"_"+KeyUnit.MD5(hostName));
				return backEnd.getData(source, Query.formate(q));
			}

			@Override
			public StatusCode saveData(String key, String obj) {
				// TODO Auto-generated method stub
				Query q=Query.formate(key);
				q.path.set(q.path.size()-1, q.path.get(q.path.size()-1)+"_"+KeyUnit.MD5(hostName));
				return backEnd.saveData(Query.formate(q), obj);
			}

			@Override
			public void getUpperLevel(DataInterface<String> upper) {
				// TODO Auto-generated method stub
				backEnd.getUpperLevel(upper);
			}
			
		};
		websiteResourceCentor=new HTTPResourceCentor() {

			@Override
			public DataInterface<HTTPCase> getIODataInterface() {
				// TODO Auto-generated method stub
				return iodif;
			}

			@Override
			public DataInterface<HTTPCase> getParserDataInterface() {
				// TODO Auto-generated method stub
				return padif;
			}

			@Override
			public DataInterface<HTTPCase> getAnalysisterDataInterface() {
				// TODO Auto-generated method stub
				return andif;
			}

			@Override
			public DataInterface<String> getBackEndDataInterface() {
				// TODO Auto-generated method stub
				return enddif;
			}
			
		};
	}
	public HTTPWebSite setWorkSize(int workSize) {
		int HTTPAnalysisterListSize=HTTPAnalysisterList.size();
		int HTTPParserListSize=HTTPParserList.size();
		if(HTTPAnalysisterListSize*sizeScale[2]<workSize) {
			
		}
		return this;
	}
	private boolean luanchHTTParser() {
		HTTPParser paser=new HTTPParser();
		paser.SetHTTPResourceCentor(websiteResourceCentor);
	}
}
