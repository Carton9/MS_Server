/**
 * 
 */
package org.mike.ms.testsite_a;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.mike.ms.datacontroller.Query;
import org.mike.ms.httpcore.*;

/**
 * @author c
 *
 */
public class TestSiteAnalysis extends HTTPAnalysister {

	@Override
	public void AnalysisterClose() {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void processCase(HTTPCase newCase) {
		// TODO Auto-generated method stub
		//System.out.println(new String(newCase.getRawDataReceive()));
		//System.out.println(newCase.getHost());
		if(newCase.getURL()!=null) {
			String url=newCase.getURL().substring(1);
			if(url.equals(""))url="/index.html";
			Query q=Query.initAQuery(url, "FILE", null);
			String result=getData(q.formate(q));
			url="/index2.html";
			q=Query.initAQuery(url, "FILE", null);
			String result2=getData(q.formate(q));
			while(result2==null) {
				result2=getData(q.formate(q));
			}
			Logger.getGlobal().info(result2);
				if(result!=null) {
					newCase.setCode(HTTPCode.OK);
					newCase.addResponsesHeader("Content-Type", "text/html; charset=utf-8");
					newCase.addResponsesHeader("Server", "MS_SERVER");
					newCase.addResponsesHeader("Connection", "Keep-Alive");
//					
//				     String body = new StringBuilder("<HTML>\r\n")
//				             .append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
//				             .append("</HEAD>\r\n")
//				             .append("<BODY>")
//				             .append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
//				             .append("</BODY></HTML>\r\n").toString();
//				    newCase.setResponsesBody(body.getBytes());
					newCase.setResponsesBody(result2.getBytes());
				    //System.out.println(result);
				}else {
					newCase.setCode(HTTPCode.NOT_FOUND);
				}
		}
		
	}

}
