package com.bluetoothlib.util;

import java.util.Random;

public class StringUtil {
	public static final String bytesToHexString(byte[] bArray) {   
	    StringBuffer sb = new StringBuffer(bArray.length);   
	    String sTemp;   
	    for (int i = 0; i < bArray.length; i++) {   
	     sTemp = Integer.toHexString(0xFF & bArray[i]);   
	     if (sTemp.length() < 2)   
	      sb.append(0);   
	     sb.append(sTemp.toUpperCase());   
	    }   
	    return sb.toString();   
	}  
	
	public static byte[] hexStringToBytes(String src)
	  {
	    byte[] ret = new byte[src.length() / 2];
	    byte[] tmp = src.getBytes();
	    for (int i = 0; i < src.length() / 2; i++) {
	      ret[i] = uniteBytes(tmp[(i * 2)], tmp[(i * 2 + 1)]);
	    }
	    return ret;
	  }
	
	 private static byte uniteBytes(byte src0, byte src1)
	  {
	    byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
	      .byteValue();
	    _b0 = (byte)(_b0 << 4);
	    byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
	      .byteValue();
	    byte ret = (byte)(_b0 ^ _b1);
	    return ret;
	  }
	
	 public static String getRandomStringAccordingSystemtimeForNumberFlag(int len, int numberflag)
			    throws Exception
			  {
			    Random r = new Random();
			    String rad = DateUtil.getSystemDateTime("yyyyMMddHHmmssSSS");

			    for (int i = 17; i < len; i++) {
			      int l = r.nextInt(2);
			      if ((numberflag == 0) || ((numberflag == 1) && (l == 0))) {
			        int x = r.nextInt(9);
			        rad = rad + Integer.toString(x);
			      } else {
			        char d = (char)(int)(65.0D + Math.random() * 26.0D);
			        rad = rad + String.valueOf(d);
			      }
			    }
			    return rad;
			  }
}
