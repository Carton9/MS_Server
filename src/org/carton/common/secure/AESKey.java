package org.carton.common.secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * 
 * @author mike
 *
 */
public class AESKey extends KeyUnit {
	private static String testKey="1234567812345678";
	private String key;
	/**
	 * Create a AESKey base on key string
	 * @param key
	 */
	public AESKey(String key) {
		this.key=SHA128(key);
	}
	/**
	 * Methed return a key that use default setting, only for testing purpose
	 * @return 
	 */
	public static AESKey getdefultKey() {
		return new AESKey(testKey);
	}
	@Override
	public int decryptSize(int originSize) {
		// TODO Auto-generated method stub
		return (originSize/8+1)*8;
	}

	@Override
	public int encryptSize(int encryptionSize) {
		// TODO Auto-generated method stub
		return (encryptionSize/8-1)*8;
	}

	@Override
	public byte[] decrypt(byte[] in) {
		// TODO Auto-generated method stub
		return decrypt(in,key);
	}

	@Override
	public byte[] encrypt(byte[] in) {
		// TODO Auto-generated method stub
		return encrypt(in,key);
	}
	 private static byte[] encrypt(byte[] content, String key) {  
	        try {  
	            SecureRandom random = new SecureRandom();  
	            SecretKeySpec aesKey = new SecretKeySpec(key.getBytes(),"AES");
	            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  
	            cipher.init(Cipher.ENCRYPT_MODE, aesKey, random);  
	            byte[] result = cipher.doFinal(content);  
	            return result;  
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
	 private static byte[] decrypt(byte[] content, String key) {  
	        try {  
	            SecureRandom random = new SecureRandom();  
	            SecretKeySpec aesKey = new SecretKeySpec(key.getBytes(),"AES");
	            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  
	            cipher.init(Cipher.DECRYPT_MODE, aesKey, random);  
	            byte[] result = cipher.doFinal(content);  
	            return result;  
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }
	 private String SHA128(String strText)  
	  {  
	    return SHA(strText, "MD5");  
	  }  
	private String SHA(final String strText, final String strType)  
	  {  
	    String strResult = null;  
	    if (strText != null && strText.length() > 0)  
	    {  
	      try  
	      {  
	        MessageDigest messageDigest = MessageDigest.getInstance(strType);  
	        messageDigest.update(strText.getBytes());  
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
	@Override
	public String getCypherType() {
		// TODO Auto-generated method stub
		return "AES";
	}
	@Override
	public String getHash() {
		// TODO Auto-generated method stub
		return MD5(SHA512(this.key));
	}  
}

