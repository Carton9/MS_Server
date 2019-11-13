package org.carton.common.secure;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
/**
 * 
 * @author mike
 *
 */
public abstract class KeyUnit implements Serializable{
	/**
	 * Method that will return a hash value that verify this key unit object
	 * @return a 
	 */
	public abstract String getHash();
	/**
	 * Method calculate the data length after decode base on the encoded data length
	 * @param originSize
	 * @return
	 */
	public abstract int decryptSize(int originSize);
	/**
	 * Method calculate the data length after encode base on the decoded data length
	 * @param encryptionSize
	 * @return
	 */
	public abstract int encryptSize(int encryptionSize);
	/**
	 * Decrypt data base on special algorithm
	 * @param in
	 * @return
	 */
	public abstract byte[] decrypt(byte[] in);
	/**
	 * Encrypt data base on special algorithm
	 * @param in
	 * @return
	 */
	public abstract byte[] encrypt(byte[] in);
	/**
	 * Get the type of Cypher
	 * @return algorithm name
	 */
	public abstract String getCypherType();
	/**
	 * Encrypt string base on special algorithm
	 * @param in 
	 * @return
	 */
	public String encryptString(String in) {
		return trans(encrypt(in.getBytes()));
	}
	/**
	 * Decrypt string base on special algorithm
	 * @param in
	 * @return
	 */
	public String decryptString(String in) {
		return new String(decrypt(trans(in)));
	}
	private static String trans(byte[] data) {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<data.length;i++) {
			String hex=Integer.toHexString((data[i]&0xFF));
			if(hex.length()==1) {
				hex="0"+hex;
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	private static byte[] trans(String data) {
		if(data==null||data.length()<1)
			return null;
		byte[] result=new byte[data.length()/2];
		for(int i=0;i<data.length()/2;i++) {
			int high=Integer.parseInt(data.substring(i*2,i*2+1),16);
			int low=Integer.parseInt(data.substring(i*2+1,i*2+2),16);
			result[i]=(byte)(high*16+low);
		}
		return result;
	}
	public static String SHA512(String strText){  
	    return SHA(strText.getBytes(), "SHA-512");  
	}  
	public static String SHA512(byte[] strText){  
	    return SHA(strText, "SHA-512");  
	} 
	public static String MD5(String strText){  
	    return SHA(strText.getBytes(), "MD5");  
	}  
	public static String MD5(byte[] strText){  
	    return SHA(strText, "MD5");  
	} 
	private static String SHA(final byte[] data, final String strType)  
	  {  
	    String strResult = null;  
	    if (data != null && data.length > 0)  
	    {  
	      try  
	      {  
	        MessageDigest messageDigest = MessageDigest.getInstance(strType);  
	        messageDigest.update(data);  
	        byte byteBuffer[] = messageDigest.digest();  
	        StringBuffer strHexString = new StringBuffer();  
	        for (int i = 0; i < byteBuffer.length; i++)  
	        {  
	          String hex = Integer.toHexString(0xff & byteBuffer[i]);  
	          if (hex.length() == 1)  
	          {  
	            strHexString.append('0');  
	          }  
	          strHexString.append(hex);  
	        }
	        strResult = strHexString.toString();  
	      }  
	      catch (NoSuchAlgorithmException e)  
	      {  
	        e.printStackTrace();  
	      }  
	    }  
	  
	    return strResult;  
	  }
	public static String BASE64Encode(String in) {
		return Base64.getEncoder().encodeToString(in.getBytes());
	}
	public static String BASE64decode(String in) {
		byte[] decodedBytes = Base64.getDecoder().decode(in);
		return new String(decodedBytes);
	}
}
