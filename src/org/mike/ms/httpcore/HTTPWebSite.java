/**
 * 
 */
package org.mike.ms.httpcore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.datacontroller.*;
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
	HTTPResourceCentor websiteResourceCentor=null;
	DataPool pool;
	ThreadPoolManager tpm;
	HTTPAnalysisterFactory haf;
	GeneralCache<HTTPCase> gc;
	int correntPaserCount=0;
	int correntAnalysisterCount=0;
	public HTTPWebSite(String hostName,File websiteFolder,DataPool pool,HTTPAnalysisterFactory haf) {
		this.hostName=hostName;
		this.pool=pool;
		this.haf=haf;
		this.tpm=ThreadPoolManager.getThreadPoolManager();
		gc=new GeneralCache<HTTPCase>("CASE_"+KeyUnit.MD5(hostName),true,false);
		this.pool.addInterface(gc);
		constructWebsiteResourceCentor();
		loadAllFile(websiteFolder);
		for (int i = 0; i < 10; i++) {
			luanchHTTParser();
			luanchHTTPAnalysister();
		}
	}
	private boolean loadAllFile(File websiteFolder) {
		if(websiteFolder==null)return false;
		websiteFolder=websiteFolder.getAbsoluteFile();
		if(!websiteFolder.exists())return false;
		if(!websiteFolder.isDirectory())return false;
		
		ArrayList<File> fs=getAllFiles(websiteFolder);
		
		for(File i:fs) {
			String filename=i.getName().toLowerCase();
			FileInterface<String> fis=null;
			try {
				fis=new FileInterface<String>(i, websiteFolder.getAbsolutePath(), hostName, !filename.contains("bin"));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			if(fis!=null)pool.addInterface(fis);
		}
		return true;
	}
	private ArrayList<File> getAllFiles(File f){
		ArrayList<File> result=new ArrayList<File>();
		if(f.isFile())result.add(f);
		else {
			File[] fs=f.listFiles();
			for(File i:fs) {
				result.addAll(getAllFiles(i));
			}
		}
		return result;
	}
	public HTTPResourceCentor constructWebsiteResourceCentor() {
		if(websiteResourceCentor!=null)return websiteResourceCentor;
		DataInterface<HTTPCase> mainDif=pool.getInterface();
		DataInterface<String> backEnd=pool.getInterface();
		DataInterface<HTTPCase> iodif=new DelayCache(mainDif);
		DataInterface<HTTPCase> padif=new DataInterface<HTTPCase>() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return mainDif.getName();
			}

			@Override
			public HTTPCase getData(Class source, String key) {
				// TODO Auto-generated method stub
				if(key.contains("RUN"))
					return mainDif.getData(source, key);
				return mainDif.getData(source, key);
			}

			@Override
			public StatusCode saveData(String key, HTTPCase obj) {
				// TODO Auto-generated method stub
				if(key.contains("RUN"))
					return mainDif.saveData(key, obj);
				return mainDif.saveData(key.replace("CASE", "CASE_"+KeyUnit.MD5(obj.getHost())), obj);
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
				if(key.contains("RUN"))
					return mainDif.getData(source, key);
				return mainDif.getData(source,key.replace("CASE", "CASE_"+KeyUnit.MD5(hostName)));
			}

			@Override
			public StatusCode saveData(String key, HTTPCase obj) {
				// TODO Auto-generated method stub
				if(key.contains("RUN"))
					return mainDif.saveData(key, obj);
				Query q=Query.formate(key);
				String type=q.path.get(q.path.size()-1);
				if(type.indexOf('_')!=-1)
					q.path.set(q.path.size()-1, type.substring(0, type.indexOf('_')));
				return mainDif.saveData(Query.formate(q), obj);
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
				return new DelayCache<HTTPCase>(iodif);
			}

			@Override
			public DataInterface<HTTPCase> getParserDataInterface() {
				// TODO Auto-generated method stub
				return new DelayCache<HTTPCase>(padif);
			}

			@Override
			public DataInterface<HTTPCase> getAnalysisterDataInterface() {
				// TODO Auto-generated method stub
				return new DelayCache<HTTPCase>(andif);
			}

			@Override
			public DataInterface<String> getBackEndDataInterface() {
				// TODO Auto-generated method stub
				return enddif;
			}

			@Override
			public DataInterface<HTTPIO> getHTTPIOInterface() {
				// TODO Auto-generated method stub
				return tpm.getThreadInterface();
			}
			
		};
		return websiteResourceCentor;
	}
	// out of 1000
	public void setWorkSize(int workSize) {
		if(workSize>600) {
			luanchHTTParser();
			luanchHTTPAnalysister();
		}else if(workSize<400) {
			killAnalysisterProcess();
			killParserProcess();
		}
	}
	private void killAnalysisterProcess() {
		HashMap<String, String> valueKeySet=new HashMap<String, String>();
		valueKeySet.put("VALUE", (correntAnalysisterCount-1)+"");
		HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
		valueFile.put("ANALYSIS", valueKeySet);
		Query query=Query.initAQuery(hostName, "HTTPAnalysister", valueFile);
		if(tpm.getThreadInterface().endProcess(query.formate(query))) {
			correntAnalysisterCount--;
		}
	}
	private void killParserProcess() {
		HashMap<String, String> valueKeySet=new HashMap<String, String>();
		valueKeySet.put("VALUE", (correntPaserCount-1)+"");
		HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
		valueFile.put("PARSER", valueKeySet);
		Query query=Query.initAQuery(hostName, "HTTPParser", valueFile);
		if(tpm.getThreadInterface().endProcess(query.formate(query))) {
			correntPaserCount--;
		}
	}
	private boolean luanchHTTParser() {
		HTTPParser paser=new HTTPParser();
		paser.SetHTTPResourceCentor(websiteResourceCentor);
		HashMap<String, String> valueKeySet=new HashMap<String, String>();
		valueKeySet.put("VALUE", (correntPaserCount++)+"");
		HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
		valueFile.put("PARSER", valueKeySet);
		Query query=Query.initAQuery(hostName, "HTTPParser", valueFile);
		tpm.getThreadInterface().saveData(query.formate(query), paser);
		return true;
	}
	private boolean luanchHTTPAnalysister() {
		HTTPAnalysister paser=haf.getHttpAnalysister();
		paser.SetHTTPResourceCentor(websiteResourceCentor);
		HashMap<String, String> valueKeySet=new HashMap<String, String>();
		valueKeySet.put("VALUE", (correntAnalysisterCount++)+"");
		HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
		valueFile.put("ANALYSIS", valueKeySet);
		Query query=Query.initAQuery(hostName, "HTTPAnalysister", valueFile);
		tpm.getThreadInterface().saveData(query.formate(query), paser);
		return true;
	}
}
