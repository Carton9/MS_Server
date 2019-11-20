import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import org.mike.ms.datacontroller.Query;

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
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
		HashMap<String, String> timeMap=new HashMap<String, String>();
		timeMap.put("VALUE", new String(LocalDate.now().toString()+"-"+LocalTime.now().toString()).replace(".", "-").replace(":", "-"));
		HashMap<String, String> remoteAddrMap=new HashMap<String, String>();
		remoteAddrMap.put("VALUE", "1.1.1.1");
		valueFile.put("TIME", timeMap);
		valueFile.put("ADDR", remoteAddrMap);
		Query q=Query.initAQuery("socketKey", "RAW", valueFile);
		System.out.println();
		String a=Query.formate(q);
		Query q2=Query.formate(a.replace("RAW", "CASE"));
		System.out.println(Query.formate(q2));
	}

}
