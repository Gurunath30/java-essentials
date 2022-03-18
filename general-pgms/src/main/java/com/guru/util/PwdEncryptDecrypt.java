package com.guru.util;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class PwdEncryptDecrypt {

	private static Logger log = Logger.getLogger(PwdEncryptDecrypt.class.toString());

	private static Cipher enCipher;
	private static Cipher deCipher;
	private static final String SECRET = "49836FBE42C27CDA49836FBE";
	private static int encryptDecryptRetry;

	public static void initEnDe(int retry) {
		setEncryptDecryptRetry(retry);
		initEnDe();
	}
	private static void initEnDe() {
		initEn();
		initDe();
	}

	// Secret Key Of Size 24 Bits
	private static void initEn() {
		try {
			enCipher = Cipher.getInstance("DESede");
			enCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "DESede"));
		} catch (Exception e) {
			log.info(e.toString());
		}
	}

	private static void initDe() {
		try {
			deCipher = Cipher.getInstance("DESede");
			deCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "DESede"));
		} catch (Exception e) {
			log.info(e.toString());
		}
	}
	
	public static void setEncryptDecryptRetry(int encryptDecryptRetry) {
		PwdEncryptDecrypt.encryptDecryptRetry=encryptDecryptRetry;
	}

	/**
	 * Decrypts the given string
	 * 
	 * @param data
	 * @return Decrypted String
	 * @throws Exception
	 */
	private static String decrypt(String data, int retry) {
		if (Utility.isBlank(data))
			return data;
		try {
			return new String(deCipher.doFinal(DatatypeConverter.parseBase64Binary(data)));
		}catch (IllegalBlockSizeException e) {
			log.info("IllegalBlockSizeException...:"+data);
			return data;
		}catch (Exception e) {
			//log.show("DECRYPT RETRY FOR \"" + data + "\"......:" + retry + "\t" + e.getMessage() + "\t"
			//		+ e.getClass().getName());
			initDe();
			if (retry < 0) {
				log.info(e.toString());
				return data;
			}
			return decrypt(data, retry - 1);
		}
	}
	public static String decrypt(String data) {
		return decrypt(data,encryptDecryptRetry);
	}
	/**
	 * Encrypts the given string
	 * 
	 * @param data
	 * @return Encrypted String
	 * @throws Exception
	 */
	private static String encrypt(String data, int retry) {
		if (Utility.isBlank(data))
			return data;
		try {
			return DatatypeConverter.printBase64Binary(enCipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			//log.show("ENCRYPT RETRY FOR \"" + data + "\"......:" + retry + "\t" + e.getMessage() + "\t"
			//		+ e.getClass().getName());
			initEn();
			if (retry < 0) {
				log.info(e.toString());
				return data;
			}
			return encrypt(data, retry - 1);
		}
	}
	public static String encrypt(String data) {
		return encrypt(data,encryptDecryptRetry);
	}
	
	public static void main(String[] args) {
		System.out.println(decrypt("x5/ip64m6e0="));
	}
}
