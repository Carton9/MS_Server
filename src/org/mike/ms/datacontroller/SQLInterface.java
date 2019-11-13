/**
 * 
 */
package org.mike.ms.datacontroller;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author c
 *
 */
// SQL@<>
public class SQLInterface<T> implements DataInterface<T>,Closeable {
    private Connection conn = null ;
    private String url = "jdbc:mysql://localhost/test ";
    private String user = "root ";
    private String pwd = "0429 ";
    private static final String type="SQL";
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		Query q=Query.formate(key);
		if(!q.path.contains("SQL"))return null;
		return null;
	}

	@Override
	public boolean saveData(String key, T obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		
	}
	private ArrayList<HashMap<String, String>> search(String table,HashMap<String, String> keyValues){
		if(table==null||table.equals("")||keyValues==null)return null;
		String query="select * from "+table+" where ";
		ArrayList<String> keys=new ArrayList<String>();
		keys.addAll(keyValues.keySet());
		for(int i=0;i<keys.size();i++) {
			query+=keys.get(i)+"=?";
			if(i!=keys.size()-1) {
				query+=" and ";
			}
		}
		try(PreparedStatement ps=conn.prepareStatement(query)){
			for(int i=0;i<keys.size();i++) {
				ps.setString(i+1,keyValues.get(keys.get(i)));
			}
	        ResultSet rs=ps.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();
			int length=rsmd.getColumnCount();
			ArrayList<HashMap<String, String>> result=new ArrayList<HashMap<String,String>>();
	        do {
	        	HashMap<String, String> data=new HashMap<String, String>();
	        	for(int i=0;i<length;i++) {
	        		data.put(rsmd.getColumnLabel(i), rs.getString(i));
	        	}
	        	result.add(data);
	        }while(rs.next());
	        return result;
		}catch (SQLException e ) {
	        return null;
	    }
	}
	private boolean update(String table,HashMap<String, String> keyValues,HashMap<String, String> updateValue) {
		if(table==null||table.equals("")||keyValues==null||updateValue==null)return false;
		ArrayList<HashMap<String, String>> result=search(table,keyValues);
		ArrayList<String> keys=new ArrayList<String>();
		ArrayList<String> updates=new ArrayList<String>();
		keys.addAll(keyValues.keySet());
		updates.addAll(updateValue.keySet());
		String insert="insert into Goods (Gname,Gprice,Gamount,Gdate,Gperson) values (?,?,?,?,?)";

		if(result==null) {
			String updateString="";
			String itemString="";
			for(int i=0;i<updates.size();i++) {
				itemString+=updateValue.get(updates.get(i));
				updateString+="?";
				if(i!=keys.size()-1) {
					updateString+=",";
					itemString+=",";
				}
			}
			String query=String.format("insert into %s (%s) values (%s)", table,itemString,updateString);
            try(PreparedStatement ps=conn.prepareStatement(query)) {
            	for(int i=0;i<updates.size();i++) {
    				ps.setString(i+1,updateValue.get(keys.get(i)));
    			}
				ps.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else {
			String condic="";
			String itemString="";
			for(int i=0;i<keys.size();i++) {
				condic+=keys.get(i)+"=?";
				if(i!=keys.size()-1) {
					condic+=" and ";
				}
			}
			for(int i=0;i<updates.size();i++) {
				itemString+=updateValue.get(updates.get(i))+"=?";
				if(i!=keys.size()-1) {
					itemString+=",";
				}
			}
			String query=String.format("update %s set %s where %s", table,itemString,condic);
            try(PreparedStatement ps=conn.prepareStatement(query)){
            	int keySize=keys.size();
    			int updateSize=updates.size();
    			for(int i=0;i<keySize;i++) {
    				ps.setString(i+1,keyValues.get(keys.get(i)));
    			}
                for(int i=0;i<updateSize;i++) {
    				ps.setString(i+1+keySize,updateValue.get(updates.get(i)));
    			}
				ps.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	private void init(){
        try {        
          Class.forName("com.mysql.jdbc.Driver").newInstance();
          conn = DriverManager.getConnection(url ,user ,pwd );
        } catch (Exception e){
          // your installation of JDBC Driver Failed 
          e.printStackTrace();
        }
    }
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try {
            if (conn != null ) conn .close();
        }catch (Exception e){
            e.printStackTrace();
        }
	}

}
