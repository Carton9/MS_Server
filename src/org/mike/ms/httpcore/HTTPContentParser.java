/**
 * 
 */
package org.mike.ms.httpcore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.mike.ms.udp.KeyValue;

/**
 * @author c
 *
 */
public class HTTPContentParser {

	/**
	 * 
	 */
	public HTTPContentParser() {
		// TODO Auto-generated constructor stub
	}
	public void parse(HTTPCase data) {
		if(data.getMethod().contains("GET")) {
			String urlList[]=data.getURL().split("\\?");
			if(urlList.length>1) {
				parseStandardData(urlList[1],data);
				data.setURL(urlList[0]);
			}
		}else if(data.getMethod().contains("POST")) {
			if(!data.getHeader("Content-Type").contains("json")) {
				parseStandardData(new String(data.getBody()),data);
			}
		}
	}
	public void formate(HTTPCase data) {
		if(data.getResponsesBody()==null)return;
		if(data.getResponsesBody().length==0)return;
		StringBuilder sb=new StringBuilder();
		sb.append(String.format("HTTP/1.1 %s %s\n", data.getCode().getErrorCode()+"", data.getCode().getNote()));
		ArrayList<KeyValue<String, String>> list=data.getResponsesHeader();
		list.add(new KeyValue<String, String>("Host", data.getHost()));
		if(!data.getCookieField().isEmpty())
			list.add(new KeyValue<String, String>("Set-Cookie", formateCookie(data)));
		list.add(new KeyValue<String, String>("Content-Length", (data.getResponsesBody().length)+""));
		for(KeyValue<String, String> i:list) {
			sb.append(i.k)
			.append(": ")
			.append(i.t)
			.append("\n");
		}
		sb.append("\n\n");
		String headerField=sb.toString();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			baos.write(headerField.getBytes());
			baos.write(data.getResponsesBody());
			data.setRawDataSend(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void parseStandardData(String info,HTTPCase data) {
		if(info==null)return;
		String KeyValueList[]=info.split("&");
		for(String i:KeyValueList) {
			if(!i.contains("="))continue;
			String datas[]=i.split("=");
			data.addSessionDatas(datas[0], datas[1]);
		}
	}
	private String formateCookie(HTTPCase data) {
		StringBuilder sb=new StringBuilder();
		ArrayList<KeyValue<String, String>> list=data.getCookieField();
		for(KeyValue<String, String> i:list) {
			sb.append(i.k)
			.append("=")
			.append(i.t)
			.append(", ");
		}
		return sb.substring(0, sb.length()-2);
	}
}
