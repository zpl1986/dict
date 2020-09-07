package cn.membership.common.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Des3Util {

    // 密码向量  
    private final static String iv = "01234567";
    
    private static final String CHARSET_UTF8 = "UTF-8";
    
    // 加解密统一使用的编码方式  
    private final static String encoding = CHARSET_UTF8;
    
    public static String genSecretKey(final String srcKey) {
    	
    	StringBuffer secretKeyStrBuf = new StringBuffer();
    	
    	if (null != srcKey && 0 != srcKey.trim().length()) {
    		String trimedSrcKey = srcKey.trim();
    		int length = trimedSrcKey.length();
    		if (24 == length) {
    			secretKeyStrBuf.append(trimedSrcKey);
    		} else if (length < DESedeKeySpec.DES_EDE_KEY_LEN) {
    			int m = DESedeKeySpec.DES_EDE_KEY_LEN / length;
    			int n = DESedeKeySpec.DES_EDE_KEY_LEN % length;
    			
    			secretKeyStrBuf.append(trimedSrcKey);
    			for (int i = 1; i < m; i++) {
    				secretKeyStrBuf.append(trimedSrcKey);
    			}
    			secretKeyStrBuf.append(trimedSrcKey.substring(0, n));
    		} else if (length > DESedeKeySpec.DES_EDE_KEY_LEN) {
    			secretKeyStrBuf.append(trimedSrcKey.substring(0, DESedeKeySpec.DES_EDE_KEY_LEN));
    		} 
    	} // if (null != srcKey || 0 != srcKey.trim().length())
    	
		return secretKeyStrBuf.toString();
    }
    
	
    /** 
     * 3DES加密 
     *  
     * @param plainText 普通文本 
     * @return 
     * @throws Exception  
     */  
    public static String encrypt(final String plainText, final String secretKey) throws Exception {  
    	
    	String encryptedText = plainText;
    	String key = genSecretKey(secretKey);
    	if (null != key && DESedeKeySpec.DES_EDE_KEY_LEN == key.trim().length()) {
            Key deskey = null;  
            DESedeKeySpec spec = new DESedeKeySpec(genSecretKey(secretKey).getBytes());  
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
            deskey = keyfactory.generateSecret(spec);  
      
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);  
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(encoding));  
            encryptedText = Base64.encode(encryptedData);     
            encryptedText = URLEncoder.encode(encryptedText, CHARSET_UTF8);
    	} // if (null != key && DESedeKeySpec.DES_EDE_KEY_LEN == key.trim().length())
    	
        return encryptedText;
    }  
    
    /** 
     * 3DES解密 
     *  
     * @param encryptedText 加密文本 
     * @return 
     * @throws Exception 
     */  
    public static String decrypt(final String plainText, final String secretKey) throws Exception {  
    	String decryptedText = plainText;
    	String key = genSecretKey(secretKey);
    	if (null != key && DESedeKeySpec.DES_EDE_KEY_LEN == key.trim().length()) {
            Key deskey = null;  
            DESedeKeySpec spec = new DESedeKeySpec(genSecretKey(secretKey).getBytes());  
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
            deskey = keyfactory.generateSecret(spec);  
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);  
      
            decryptedText = URLDecoder.decode(decryptedText, CHARSET_UTF8);
            byte[] decryptedData = cipher.doFinal(Base64.decode(decryptedText));  
      
            decryptedText = new String(decryptedData, encoding);  
    	} // if (null != key && DESedeKeySpec.DES_EDE_KEY_LEN == key.trim().length())

    	return decryptedText;
    }  
    
}
