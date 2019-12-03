/**
 * 
 */
package org.mike.ms.datacontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.logging.Logger;

import org.carton.common.secure.KeyUnit;

/**
 * @author c
 *
 */
public class FileInterface<T> implements DataInterface<T> {
	DataInterface<T> upper=null;
	String type="FILE";
	String loc;
	T data;
	String dataString="";
	boolean isString;
	String hostName;
	File f;
	public FileInterface(File f,String rootPath,String hostName,boolean isString) throws IOException, ClassNotFoundException {
		this.f=f;
		type+="_"+KeyUnit.MD5(hostName);
		FileInputStream fis=new FileInputStream(f);
		loc=f.getAbsolutePath().replace(rootPath, "");
		this.isString=isString;
		if(isString) {
			int data=0;
			byte[] buffer=new byte[4096]; 
			while(data!=-1) {
				data=fis.read(buffer);
				if(data==-1)break;
				dataString+=new String(Arrays.copyOf(buffer, data));
			}
		}else {
			try {
				ObjectInputStream ois=new ObjectInputStream(fis);
				data=(T)ois.readObject();
				ois.close();
			}catch(Exception e) {
				int data=0;
				byte[] buffer=new byte[4096]; 
				while(data!=-1) {
					data=fis.read(buffer);
					if(data==-1)break;
					dataString+=new String(Arrays.copyOf(buffer, data));
				}
				isString=true;
			}
		}
		
		
	} 
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return null;
		Query q=Query.formate(key);
		if(!q.path.contains(type))return null;
		if(q.path.contains(loc)) {
			if(isString)return (T) dataString;
			return data;
		}
		return null;
	}
	@Override
	public StatusCode saveData(String key, T obj) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return StatusCode.ENTRY_NOT_FOUND;
		Query q=Query.formate(key);
		if(!q.path.contains(type))return StatusCode.INTERFACE_NOT_FOUND;
		if(q.path.contains(loc)) {
			try {
				saveFile(obj);
				return StatusCode.SECCESS;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return StatusCode.DATA_SAVE_FAILURE;
			}
		}
		return StatusCode.ENTRY_NOT_FOUND;
	}
	void saveFile(T data) throws IOException {
		FileOutputStream fos=new FileOutputStream(f);
		if(isString) {
			fos.write(((String)data).getBytes());
		}else {
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(data);
		}
		fos.close();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		this.upper=upper;
	}

}
