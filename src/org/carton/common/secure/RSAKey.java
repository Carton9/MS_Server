package org.carton.common.secure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 
 * @author mike
 *
 */
class RSAKey extends KeyUnit {
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;
	int mode;
	boolean success;
	RSAKey(RSAPrivateKey privateKey) {
			this(null,privateKey);
	}
	RSAKey(RSAPublicKey publicKey) {
		this(publicKey, null);
	}
	RSAKey(RSAPublicKey publicKey,RSAPrivateKey privateKey) {
		mode=0;
		success=true;
		this.privateKey=privateKey;
		this.publicKey=publicKey;
		if(publicKey!=null)
			mode=-1;
		if(privateKey!=null)
			mode=1;
		if(publicKey==null&&privateKey==null)
			success=false;
	}
	@Override
	public int decryptSize(int originSize) {
		// TODO Auto-generated method stub
		return ((originSize/128)+1)*128;
	}

	@Override
	public int encryptSize(int encryptionSize) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public byte[] decrypt(byte[] in) {
		// TODO Auto-generated method stub
		ByteArrayInputStream bIn=new ByteArrayInputStream(in);
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		byte[] buff=new byte[128];
		try {
			while(bIn.read(buff)!=-1){
				System.out.println(buff);
				byte data[]=decryptUnit(buff);
				System.out.println(data);
				bOut.write(data);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return bOut.toByteArray();
	}

	@Override
	public byte[] encrypt(byte[] in) {
		// TODO Auto-generated method stub
		int pointer=0;
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		while(true) {
			byte[] buff=new byte[117];
			for(int i=0;i<117;i++) {
				if(pointer<in.length) {
					buff[i]=in[pointer];
					pointer++;
				}else
					break;
			}
			try {
				bOut.write(encryptUnit(buff));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if(pointer>=in.length)
				break;
		}
		return bOut.toByteArray();
	}
	private byte[] encryptUnit(byte[] in) throws Exception {
		if(!this.success)
			return null;
		try {
			 Cipher cipher = Cipher.getInstance("RSA");
			if (mode == -1)
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			else if(mode==1)
				cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			else
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			byte[] output = cipher.doFinal(in);
			System.out.println(in.length);
			System.out.println(output.length);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("NoSuchAlgorithmException");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("InvalidKeyException");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			throw new Exception("BadPaddingException");
		}
	}
	private byte[] decryptUnit(byte[] in) throws Exception {
		
		if(!this.success)
			return null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA");
			System.out.println("size: "+in.length);
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			if (mode == -1)
				cipher.init(Cipher.DECRYPT_MODE, publicKey);
			else if(mode==1)
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
			else
				cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(in);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("�޴˽����㷨");
		} catch (NoSuchPaddingException e) {
			throw new Exception(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new Exception("����˽Կ�Ƿ�,����");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("���ĳ��ȷǷ�");
		} catch (BadPaddingException e) {
			throw new Exception(e.getMessage());
		}
	}
	@Override
	public String getCypherType() {
		// TODO Auto-generated method stub
		return "RSA";
	}
	/**
	 * 
	 * @return a hash map that contain a public key in "public", and a private key in "private"
	 */
	public static HashMap<String,RSAKey> getKeyPair(){
       KeyPairGenerator keyPairGen = null;  
       try {  
           keyPairGen = KeyPairGenerator.getInstance("RSA");  
       } catch (NoSuchAlgorithmException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
       keyPairGen.initialize(1024,new SecureRandom());  
       KeyPair keyPair = keyPairGen.generateKeyPair();  
       RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
       RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
//       //BiUnit<RSAPublicKey,RSAPrivateKey> result=new BiUnit<RSAPublicKey,RSAPrivateKey>();
//       result.setK(publicKey);
//       result.setO(privateKey);
       HashMap<String,RSAKey> result=new HashMap<String,RSAKey>();
       result.put("public", new RSAKey(publicKey));
       result.put("private", new RSAKey(privateKey));
       return result;
	}
	@Override
	public String getHash() {
		// TODO Auto-generated method stub
		if(publicKey!=null){
//			publicKey.
		}
		if(privateKey!=null)
			mode=1;
		return null;
	}

}
