package com.tomcat.datasource.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author cyc1er
 *
 * @date 2019年5月7日
 * @category AES128位加密
 */
public class EncodeProperties {

	public static void validateProperties(String username, String password) throws Exception {
		if ((username.equals("") || username.equals(null))) {
			throw new Exception("检测到配置文件中username无效");
		}
		if ((password.equals("") || password.equals(null))) {
			throw new Exception("检测到配置文件中password无效");
		}
		if (username.startsWith("Encrypted:") || password.startsWith("Encrypted:")) {
			System.out.println("配置文件已经加密。程序退出，正常启动Tomcat");
			System.exit(0);
		}
	}

	public static SecretKeySpec secretKey;
	public static byte[] key;

	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return "Encrypted:" + Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.replace("Encrypted:", ""))));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

//	public static void main(String[] args) {
//		final String secretKey = "Cycle";
//
//		String originalString = "1qaz2wsx";
//		String encryptedString = EncodeProperties.encrypt(originalString, secretKey);
//		String decryptedString = EncodeProperties.decrypt(encryptedString, secretKey);
//
//		System.out.println(originalString);
//		System.out.println(encryptedString);
//		System.out.println(decryptedString);
//	}
}
