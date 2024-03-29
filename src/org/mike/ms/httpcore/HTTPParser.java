/**
 * 
 */
package org.mike.ms.httpcore;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.httpcore.HTTPCode;
import org.mike.ms.threadmanager.Service;


/**
 * require work with HTTPWEBSITE
 * @author c
 *
 */
public class HTTPParser implements HTTPCoreDIPInterface,Service{
	HTTPResourceCentor rc;
	boolean isClose=false;
	DataInterface<HTTPCase> dataLink;
	private void parseCookie(String rawInfo,HTTPCase data) {
		String keyValueSet[]=rawInfo.split(", ");
		for(String i:keyValueSet) {
			String cookie[]=i.split("=");
			if(cookie.length<1)continue;
			data.addCookie(cookie[0], cookie[1]);
		}
	}
	private void parser(HTTPCase data) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getRawDataReceive()), Charset.forName("utf8")));
		ArrayList<String> DataByLine= new ArrayList<String>();
		
		while(true) {
			String line=br.readLine();
			if(line==null||line.equals(""))break;
			DataByLine.add(line);
		}
		if(DataByLine.isEmpty()) {
			data.setCode(HTTPCode.BAD_REQUEST);
			throw new IOException(HTTPCode.BAD_REQUEST.getNote());
		}
		String[] header=DataByLine.get(0).split(" ");
		if(header.length<3) {
			data.setCode(HTTPCode.BAD_REQUEST);
			throw new IOException(HTTPCode.BAD_REQUEST.getNote());
		}
		data.setMethod(header[0]);
		data.setURL(header[1]);
		data.setVersion(header[2]);
		ByteArrayOutputStream baos=new ByteArrayOutputStream(); 
		for(int i=1;i<DataByLine.size();i++) {
			String headerValue[]=DataByLine.get(i).split(": ");
			if(headerValue.length>1) {
				if(headerValue[0].equals("Host"))data.setHost(headerValue[1]);
				if(headerValue[0].equals("Cookie"))parseCookie(headerValue[1],data);
				data.addHeader(headerValue[0], headerValue[1]);
			}
			else{
				data.setCode(HTTPCode.BAD_REQUEST);
				throw new IOException(HTTPCode.BAD_REQUEST.getNote());
			}
		}
		while(true) {
			int info=br.read();
			if(info==-1)break;
			baos.write(info);
		}
		data.setBody(baos.toByteArray());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isClose) {
			HTTPCase newCase=dataLink.getData(this.getClass(), "*@RAW");
			if(newCase==null)continue;
			try {
				parser(newCase);
				newCase.setCaseNumber(newCase.getCaseNumber().replace("RAW", "CASE"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				newCase.setCaseNumber(newCase.getCaseNumber().replace("RAW", "PASS"));
			}
			dataLink.saveData(newCase.getCaseNumber(), newCase);
		}
	}

	@Override
	public void SetHTTPResourceCentor(HTTPResourceCentor rc) {
		// TODO Auto-generated method stub
		this.rc=rc;
		dataLink=rc.getParserDataInterface();
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		isClose=true;
	}
}
