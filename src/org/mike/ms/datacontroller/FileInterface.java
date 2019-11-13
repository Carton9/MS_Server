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

/**
 * @author c
 *
 */
public class FileInterface<T> implements DataInterface<T> {
	DataInterface<T> upper=null;
	String type="FILE";
	T data;
	File f;
	public FileInterface(File f) throws IOException, ClassNotFoundException {
		this.f=f;
		FileInputStream fis=new FileInputStream(f);
		ObjectInputStream ois=new ObjectInputStream(fis);
		data=(T)ois.readObject();
		ois.close();
	}
	@Override
	public T getData(Class source,String key) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return null;
		String[] query=key.split("@");
		if(!query[query.length-1].contains(type))return null;
		for(int i=query.length-2;i<0;i--) {
			if(query[i].equals(getName())||query[i].equals("*"))return data;
		}
		return null;
	}
	@Override
	public boolean saveData(String key, T obj) {
		// TODO Auto-generated method stub
		if(key==null||key.equals(""))return false;
		String[] query=key.split("@");
		if(!query[query.length-1].contains(type))return false;
		for(int i=query.length-2;i<0;i--) {
			if(query[i].equals(getName())) {
				try {
					saveFile(obj);
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	void saveFile(T data) throws IOException {
		FileOutputStream fos=new FileOutputStream(f);
		ObjectOutputStream oos=new ObjectOutputStream(fos);
		oos.writeObject(data);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return f.getPath();
	}

	@Override
	public void getUpperLevel(DataInterface<T> upper) {
		// TODO Auto-generated method stub
		this.upper=upper;
	}

}
