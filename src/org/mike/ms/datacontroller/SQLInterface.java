/**
 * 
 */
package org.mike.ms.datacontroller;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author c
 *
 */
// SQL@<>
public class SQLInterface<T> implements DataInterface<T>,Closeable {
    private Connection con = null ;
    private Statement stmt = null ;
    private String url = "jdbc:mysql://localhost/test ";
    private String user = "root ";
    private String pwd = "0429 ";
    private static final String type="SQL";
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
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
	private HashMap<String, String> search(){
		
	}
	private void init(){
        try {        
          Class.forName("com.mysql.jdbc.Driver ").newInstance();
          con = DriverManager.getConnection(url ,user ,pwd );
          stmt = con .createStatement();
        } catch (Exception e){
          // your installation of JDBC Driver Failed 
          e.printStackTrace();
        }
    }
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try {
            if (con != null ) con .close();
            if (stmt != null ) stmt .close();
        }catch (Exception e){
            e.printStackTrace();
        }
	}

}
