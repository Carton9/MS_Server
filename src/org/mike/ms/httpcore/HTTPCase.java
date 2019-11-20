/**
 * 
 */
package org.mike.ms.httpcore;

import java.util.HashMap;

import org.mike.ms.httpcore.HTTPCode;

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
	private HashMap<String, String> headers=new HashMap<String, String>();
	private String Host;
	private byte[] body;
	/*********************///BY HTTPAnalysister
	private HashMap<String, String> sessionDatas=new HashMap<String, String>();
	/*********************///BY HTTPIO send
	private byte[] rawDataSend;
	public void addHeader(String key,String value) {
		headers.put(key, value);
	}
	public void addSessionDatas(String key,String value) {
		sessionDatas.put(key, value);
	}
	public String getHeader(String key) {
		return headers.get(key);
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
}
