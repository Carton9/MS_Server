/**
 * 
 */
package org.mike.ms.httpcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mike.ms.httpcore.HTTPCode;
import org.mike.ms.udp.KeyValue;

/**
 * @author c
 *
 */
public class HTTPCase {
	
	private String caseNumber;
	private HTTPCode code=null;
	/*********************///BY HTTPIO receive
	private byte[] rawDataReceive;
	/*********************///BY HTTPParser
	private String method;
	private String URL;
	private String version;
	private HashMap<String, String> cookieField=new HashMap<String, String>();
	private HashMap<String, String> headers=new HashMap<String, String>();
	private String Host;
	private byte[] body;
	/*********************///BY HTTPAnalysister
	private HashMap<String, String> sessionDatas=new HashMap<String, String>();
	private HashMap<String, String> responsesHeader=new HashMap<String, String>();
	private byte[] responsesBody;
	/*********************///BY HTTPIO send
	private byte[] rawDataSend;
	public String getResponsesHeader(String key) {
		return responsesHeader.get(key);
	}
	public void addResponsesHeader(String key,String value) {
		responsesHeader.put(key, value);
	}
	public ArrayList<KeyValue<String, String>> getCookieField(){
		Iterator<String> it=cookieField.keySet().iterator();
		ArrayList<KeyValue<String, String>> result=new ArrayList<KeyValue<String,String>>();
		while(it.hasNext()) {
			String key=it.next();
			String value=cookieField.get(key);
			result.add(new KeyValue<String, String>(key, value));
		}
		return result;
	}
	public ArrayList<KeyValue<String, String>> getResponsesHeader(){
		Iterator<String> it=responsesHeader.keySet().iterator();
		ArrayList<KeyValue<String, String>> result=new ArrayList<KeyValue<String,String>>();
		while(it.hasNext()) {
			String key=it.next();
			String value=responsesHeader.get(key);
			result.add(new KeyValue<String, String>(key, value));
		}
		return result;
	}
	public void addCookie(String key,String value) {
		cookieField.put(key, value);
	}
	public void addHeader(String key,String value) {
		headers.put(key, value);
	}
	public void addSessionDatas(String key,String value) {
		sessionDatas.put(key, value);
	}
	public String getHeader(String key) {
		return headers.get(key);
	}
	public String getCookie(String key) {
		return cookieField.get(key);
	}
	public String getSessionDatas(String key) {
		return sessionDatas.get(key);
	}
	public void removeHeader(String key) {
		headers.remove(key);
	}
	public void removeSessionDatas(String key) {
		sessionDatas.remove(key);
	}
	/**
	 * @return the caseNumber
	 */
	public String getCaseNumber() {
		return caseNumber;
	}
	/**
	 * @param caseNumber the caseNumber to set
	 */
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	/**
	 * @return the rawDataReceive
	 */
	public byte[] getRawDataReceive() {
		return rawDataReceive;
	}
	/**
	 * @param rawDataReceive the rawDataReceive to set
	 */
	public void setRawDataReceive(byte[] rawDataReceive) {
		this.rawDataReceive = rawDataReceive;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}
	/**
	 * @param uRL the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}
	/**
	 * @return the rawDataSend
	 */
	public byte[] getRawDataSend() {
		return rawDataSend;
	}
	/**
	 * @param rawDataSend the rawDataSend to set
	 */
	public void setRawDataSend(byte[] rawDataSend) {
		this.rawDataSend = rawDataSend;
	}
	/**
	 * @return the code
	 */
	public HTTPCode getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(HTTPCode code) {
		this.code = code;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return Host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		Host = host;
	}
	public String toString() {
		return new String(this.rawDataReceive)+"\n---"+headers.toString();
	}
	/**
	 * @return the responsesBody
	 */
	public byte[] getResponsesBody() {
		return responsesBody;
	}
	/**
	 * @param responsesBody the responsesBody to set
	 */
	public void setResponsesBody(byte[] responsesBody) {
		this.responsesBody = responsesBody;
	}
}
