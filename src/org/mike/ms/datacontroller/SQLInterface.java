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

import org.carton.common.secure.KeyUnit;

/**
 * @author c
 *
 */
// SQL@<>
public class SQLInterface<T> implements DataInterface<T>,Closeable {
    private Connection conn = null ;
    DataInterface<T> upper;
    private String url = "jdbc:mysql://localhost ";
    private String user = "root ";
    private String pwd = "0429 ";
    private static final String type="SQL";
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		if(conn==null)return null;
		
		Query q=Query.formate(key);
		boolean isSQL=q.path.contains("SQL");
		boolean haveAllInfo=q.valueFile.containsKey("SITE")&&q.valueFile.containsKey("TABLE")&&q.valueFile.containsKey("ACTION");
		if(!isSQL||!haveAllInfo)return null;
		String site=q.valueFile.get("SITE").get("VALUE");
		site=KeyUnit.MD5(site);
		String table=q.valueFile.get("TABLE").get("VALUE");
		String action=q.valueFile.get("ACTION").get("VALUE");
		String statement=q.valueFile.get("STMS").get("VALUE");
		HashMap<String, String> column=q.valueFile.get("COLUMN");
		try (Statement st = conn.createStatement();){
			checkDatabase(st,site);
			/**********************************************************/
			if(action.contains("DIRECT")&&statement!=null) {
				st.executeQuery(KeyUnit.BASE64decode(statement));
			}
			if(!checkTable(st,site))return null;
			/**********************************************************/
			T result=null;
			if(action.contains("SEARCH"))
				result= (T) search(table,column);
			st.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		/**********************************************************/
		
	}
	private void checkDatabase(Statement st,String site) throws SQLException {
		ResultSet rs=st.executeQuery("show databases");
		boolean isContain=false;
		do {
			if(rs.getString("Database").equals(site)) {
				isContain=true;
				st.executeQuery("use "+site);
				break;
			}
		}while(rs.next());
		if(isContain==false) {
			st.executeQuery("CREATE DATABASE "+site);
			st.executeQuery("use "+site);
		}
	}
	private boolean checkTable(Statement st,String site) throws SQLException {
		ResultSet rs=st.executeQuery("show tables");
		boolean isContain=false;
		do {
			if(rs.getString(1).equals(site)) {
				isContain=true;
				break;
			}
		}while(rs.next());
		return isContain;
	}
	@Override
	public boolean saveData(String key, T obj) {
		// TODO Auto-generated method stub
		if(conn==null)return false;
		
		Query q=Query.formate(key);
		boolean isSQL=q.path.contains("SQL");
		boolean haveAllInfo=q.valueFile.containsKey("SITE")&&q.valueFile.containsKey("TABLE")&&q.valueFile.containsKey("ACTION");
		if(!isSQL||!haveAllInfo)return false;
		String site=q.valueFile.get("SITE").get("VALUE");
		site=KeyUnit.MD5(site);
		String table=q.valueFile.get("TABLE").get("VALUE");
		String action=q.valueFile.get("ACTION").get("VALUE");
		String statement=q.valueFile.get("STMS").get("VALUE");
		HashMap<String, String> column=q.valueFile.get("COLUMN");
		HashMap<String, String> update=q.valueFile.get("UPDATE");
		try (Statement st = conn.createStatement();){
			checkDatabase(st,site);
			/**********************************************************/
			if(action.contains("DIRECT")&&statement!=null) {
				st.executeQuery(KeyUnit.BASE64decode(statement));
			}
			if(!checkTable(st,site))return false;
			/**********************************************************/
			boolean result=false;
			if(action.contains("UPDATE")||action.contains("INSERT"))
				result=update(table,column,update);
			st.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "SQL";
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		this.upper=upper;
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
