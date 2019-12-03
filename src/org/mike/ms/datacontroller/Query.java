/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author c
 *
 */
public class Query {
	public ArrayList<String> path=new ArrayList<String>();
	public HashMap<String, HashMap<String, String>> valueFile=new HashMap<String, HashMap<String,String>>();
	private Query(){
	}
	public static Query initAQuery(String path,String type,HashMap<String, HashMap<String, String>> valueFile) {
		Query q=new Query();
		q.path.add(path);
		q.path.add(type);
		q.valueFile=valueFile;
		return q;
	}
	public static String formate(Query query) {
		String result="";
		for(int i=0;i<query.path.size();i++) {
			result+=query.path.get(i);
			if(i!=query.path.size()-1)result+="@";
		}
		String valueString="";
		ArrayList<String> fieldArrayList=new ArrayList<String>();
		if(query.valueFile==null)return result;
		fieldArrayList.addAll(query.valueFile.keySet());
		for(int i=0;i<fieldArrayList.size();i++) {
			HashMap<String, String> value=query.valueFile.get(fieldArrayList.get(i));
			valueString+=fieldArrayList.get(i)+"->";
			
			if(value.containsKey("VALUE")&&value.keySet().size()==1) {
				valueString+=value.get("VALUE");
			}else {
				for(String key:value.keySet()) {
					valueString+=key+"=>"+value.get(key)+";";
				}
				valueString=valueString.substring(0, valueString.length()-1);
			}
			if(i!=fieldArrayList.size()-1) {
				valueString+=", ";
			}
		}
		return result+">> "+valueString;
	}
	public static Query formate(String query) {
		String[] pathValue=query.split(">> ");
		String[] paths=pathValue[0].split("@");
		Query q=new Query();
		for(int i=0;i<paths.length;i++) {
			q.path.add(paths[i]);
		}
		if(pathValue.length==1)return q;
		String[] values=pathValue[1].split(", ");
		for(int i=0;i<values.length;i++) {
			HashMap<String, String> keySet=new HashMap<String, String>();
			String[] fieldKeys=values[i].split("->");
			if(fieldKeys.length<1)continue;
			if(!fieldKeys[1].contains(";")) {
				keySet.put("VALUE", fieldKeys[1]);
			}else {
				String[] keyValueSet=fieldKeys[1].split(";");
				for(String keyV:keyValueSet) {
					String keyVStrings[]=keyV.split("=>");
					keySet.put(keyVStrings[0], keyVStrings[1]);
				}
			}
			q.valueFile.put(fieldKeys[0],keySet);
		}
		return q;
	}
}
