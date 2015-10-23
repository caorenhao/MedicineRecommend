/*
 * @(#)EncryptUtil.java   3.8.1 2009/03/11
 *
 */
package com.witspring.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
    static final int key = 0xF3; // encrypt/decrypt key

    public static String MD5(String str) {
        return MD5(str.getBytes());
    }

    /**
     * MD5  encrypt method
     * @param msg original data
     * @return encrypt data
     */
    public static String MD5(byte[] msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(msg);

            byte[] digest = md.digest();
            return toHexString(digest);
        } catch (NoSuchAlgorithmException ne) {
            return null;
        }
    }

    /**
     * SHA-1 encrypt method
     * @param data original data
     * @return encrypt data
     */
    public static String SHA1(String data) {
    	if(data == null)
    		return null;
        MessageDigest md ;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md.update(data.getBytes());
        byte[] encdata = md.digest();
        return toHexString(encdata);
    }
    
    public static String toHexString(byte[] content) {
    	StringBuffer sb = new StringBuffer();
        for(int i=0; i<content.length; i++){
        	String str = Integer.toHexString( content[i] & 0xff ).toUpperCase();
        	if(str.length() == 1)
        		sb.append('0');
        	sb.append(str);
        }
        return sb.toString();
    }

    public static String encryptString(String str) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < str.length(); i++){
            int c = str.charAt(i);
            sb.append(Integer.toHexString(c ^ key)).append(" ");
        }
        return sb.toString();
    }

    public static String decryptString(String str){
        String[] arry = StringUtil.split(str, " ");
        StringBuffer sb = new StringBuffer();
        for(int n=0; n < arry.length; n++){
            String temp = arry[n];
            char ch = (char)(Integer.parseInt(temp, 16) ^ key);
            sb.append(ch);
        }
        return sb.toString();
    }

    public static boolean isHexa(String str){
        boolean isHexa = false;
        if (str.startsWith("0x00")) {
             isHexa = true;
        }

        return isHexa;
    }
}
