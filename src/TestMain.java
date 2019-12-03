import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;

import org.carton.common.secure.KeyUnit;
import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.DataPool;
import org.mike.ms.datacontroller.DelayCache;
import org.mike.ms.datacontroller.FileInterface;
import org.mike.ms.datacontroller.GeneralCache;
import org.mike.ms.datacontroller.PoolLink;
import org.mike.ms.datacontroller.Query;
import org.mike.ms.httpcore.HTTPAnalysisterFactory;
import org.mike.ms.httpcore.HTTPCase;
import org.mike.ms.httpcore.HTTPCoreDIPInterface;
import org.mike.ms.httpcore.HTTPIO;
import org.mike.ms.httpcore.HTTPResourceCentor;
import org.mike.ms.httpcore.HTTPWebSite;
import org.mike.ms.testsite_a.AnalysisFactoryImp;
import org.mike.ms.threadmanager.ThreadPoolManager;

/**
 * 
 */

/**
 * @author c
 *
 */
public class TestMain {

	/**
	 * @param args
	 * @return 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
//		HTTPIO io=new HTTPIO(9090);
//		ThreadPoolManager tpm=ThreadPoolManager.getThreadPoolManager();
//		PoolLink poolLink1=new PoolLink(9091,9092);
//		PoolLink poolLink2=new PoolLink(9092,9091);
//		
//		DataPool dp=new DataPool();
//		DataPool dp2=new DataPool();
//		dp2.addInterface(tpm.getThreadInterface());
//		dp.addInterface(tpm.getThreadInterface());
//		dp.addInterface(poolLink1);
//		
//		dp2.addInterface(poolLink2);
//		
//		poolLink1.configLink();
//		poolLink2.configLink();
//		
//		FileInterface<String> fi=new FileInterface<String>(new File("index2.html"),new File("").getAbsolutePath(),"ss.localhost:9090",true);
//		dp2.addInterface(fi);
//		dp.addInterface(new GeneralCache<HTTPCase>("RAW",true,false));
//		dp.addInterface(new GeneralCache<HTTPCase>("PASS",true,false));
////		FileInterface<String> fi=new FileInterface<String>(new File("index.html"),"localhost:9090",true);
////		dp.addInterface(fi);
//		HTTPAnalysisterFactory haf=new AnalysisFactoryImp();
//		HTTPWebSite hws=new HTTPWebSite("ss.localhost:9090",new File("folder2"), dp, haf);
//		io.SetHTTPResourceCentor(hws.constructWebsiteResourceCentor());
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		hws.setWorkSize(1000);
//		tpm.getThreadInterface().saveData("HOST", io);
//		while(true);

//		File file=new File("");
//		file=new File(file.getAbsolutePath());
//		System.out.println(file.listFiles()[4].getPath().replace(file.getAbsolutePath(), ""));
		
		
//		SQLInterface<String> sql=new SQLInterface<String>("user", "cjz461834");
//		HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
//		HashMap<String, String> site=new HashMap<String, String>();
//		HashMap<String, String> table=new HashMap<String, String>();
//		HashMap<String, String> action=new HashMap<String, String>();
//		HashMap<String, String> stms=new HashMap<String, String>();
//		site.put("VALUE", "sitea_a");
//		table.put("VALUE", "sitea_a");
//		action.put("VALUE", "sitea_a");
//		stms.put("VALUE", KeyUnit.BASE64Encode(in));
//		valueFile.put("SITE", site);
//		valueFile.put("TABLE", table);;
//		valueFile.put("ACTION", action);
//		valueFile.put("STMS", stms);
//		Query q=Query.initAQuery("*", "SQL", valueFile);
//		sql.getData(Object.class, "");
//		System.out.println(KeyUnit.MD5("ss.localhost:9090"));
//		Process p=Runtime.getRuntime().exec("echo 0");
		
		System.out.println(getNetWorkSpeed());
	}
    public static String getNetWorkSpeed() {
    	boolean result = false;
    	String detailInfo = "";
    	DecimalFormat df = new DecimalFormat("0.00");
    	String dl = "";
    	String ul = "";
        System.out.println("开始收集网络带宽使用率");
        Process pro1,pro2;  
        Runtime r = Runtime.getRuntime();  
        try {  
            String command = "cat /proc/net/dev";  
            //第一次采集流量数据  
            long startTime = System.currentTimeMillis();  
            pro1 = r.exec(command);  
            BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));  
            String line = null;  
            long inSize1 = 0, outSize1 = 0;  
            while((line=in1.readLine()) != null){     
                line = line.trim();  
                if(line.startsWith("eth0")){  
                    System.out.println(line);  
                    String[] temp = line.split("\\s+");   
                    inSize1 = Long.parseLong(temp[1]); //Receive bytes,
                    outSize1 = Long.parseLong(temp[9]);             //Transmit bytes,
                    break;  
                }
            }     
            in1.close();  
            pro1.destroy();  
            try {
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                StringWriter sw = new StringWriter();  
                e.printStackTrace(new PrintWriter(sw));  
                System.out.println("NetUsage InterruptedException. " + e.getMessage());  
                System.out.println(sw.toString());  
            }  
            //第二次采集流量数据  
            long endTime = System.currentTimeMillis();  
            pro2 = r.exec(command);  
            BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));  
            long inSize2 = 0 ,outSize2 = 0;  
            while((line=in2.readLine()) != null){     
                line = line.trim();  
                if(line.startsWith("eth0")){  
                    System.out.println(line);  
                    String[] temp = line.split("\\s+");   
                    inSize2 = Long.parseLong(temp[1]);  
                    outSize2 = Long.parseLong(temp[9]);  
                    break;  
                }
            }
            
            //cal dl speed
            float interval = (float)(endTime - startTime)/1000;
            float currentDlSpeed = (float) ((float)(inSize2 - inSize1)/1024/interval);
            float currentUlSpeed = (float) ((float)(outSize2 - outSize1)/1024/interval);
            
            if((float)(currentDlSpeed/1024) >= 1){
            	currentDlSpeed = (float)(currentDlSpeed/1024);
            	dl = df.format(currentDlSpeed) + "Mb/s";
            }else{
            	dl = df.format(currentDlSpeed) + "Kb/s";
            }
            
            if((float)(currentUlSpeed/1024) >= 1){
            	currentUlSpeed = (float)(currentUlSpeed/1024);
            	ul = df.format(currentUlSpeed) + "Mb/s";
            }else{
            	ul = df.format(currentUlSpeed) + "Kb/s";
            }
            result = true;
            in2.close();  
            pro2.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            detailInfo = e.getMessage();
        }
        return "{\"result\":\""+result+"\",\"detailInfo\":\""+detailInfo+"\",\"dl\":\""+dl+"\",\"ul\":\""+ul+"\"}";
    }
}

//System.out.println("socketKey@RAW >-< TIME#2019-11-28 01:38:22.943,ADDR#1.1.1.1".split(" >-< ")[0]);
//HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
//HashMap<String, String> timeMap=new HashMap<String, String>();
//timeMap.put("VALUE", new String(LocalDate.now().toString()+" "+LocalTime.now()));
//HashMap<String, String> remoteAddrMap=new HashMap<String, String>();
//remoteAddrMap.put("VALUE", "1.1.1.1");
//HashMap<String, String> SQL=new HashMap<String, String>();
//SQL.put("A", "ValueA");
//SQL.put("B", "ValueB");
//SQL.put("C", "ValueC");
//valueFile.put("TIME", timeMap);
//valueFile.put("ADDR", remoteAddrMap);
//valueFile.put("SQl", SQL);
//Query q=Query.initAQuery("socketKey", "RAW", valueFile);
//System.out.println();
//String a=Query.formate(q);
//System.out.println(a);
//Query q2=Query.formate(a.replace("RAW", "CASE"));
//System.out.println(Query.formate(q2));
//

/** test HTTPIO */
//DelayCache<HTTPCase> gc=new DelayCache<HTTPCase>(new GeneralCache<HTTPCase>("CASE"));
//HTTPResourceCentor hrc=new HTTPResourceCentor() {
//	
//	@Override
//	public DataInterface<HTTPCase> getParserDataInterface() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	@Override
//	public DataInterface<HTTPCase> getIODataInterface() {
//		// TODO Auto-generated method stub
//		return gc;
//	}
//	
//	@Override
//	public DataInterface<HTTPIO> getHTTPIOInterface() {
//		// TODO Auto-generated method stub
//		return tpm.getThreadInterface();
//	}
//	
//	@Override
//	public DataInterface<String> getBackEndDataInterface() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	@Override
//	public DataInterface<HTTPCase> getAnalysisterDataInterface() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//};


//String s="77b30b8c2aa053429e52ae0386fed178@CASE_0d7efe66727b631130930e29a8a2563a&>> TIME->2019-11-28*23:17:46.785, ADDR->/0:0:0:0:0:0:0:1:51060";
//System.out.println(s.split("&")[0]);
