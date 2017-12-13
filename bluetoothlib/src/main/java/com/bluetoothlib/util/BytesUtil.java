package com.bluetoothlib.util;


/**
 * 字节工具包
 * @author kenneth
 * */
public class BytesUtil {

	/**
	 * 字节截取函数
	 * @param src  需要截取数据的原数组
	 * @param begin 截取的起始位置 从0开始
	 * @param count 需要截取的长度
	 * @return 截取的新数组
	 */
	public static byte[] subBytes(byte[] src, int begin, int count) {
		
		if( (count > 0) && (src.length >= (begin + count)) ){
			byte[] bs = new byte[count];
			for (int i = begin; i < begin + count; i++){
				bs[i - begin] = src[i];
			}
			return bs;
		}else{
			return null;
		}
	}

	/**
	 * 解析MAC串为 byte[]
	 * */
	public static byte[] getMacBytes(String mac) {
		byte[] macBytes = new byte[6];
		String[] strArr = mac.split(":");

		for (int i = 0; i < strArr.length; i++) {
			int value = Integer.parseInt(strArr[i], 16);
			macBytes[i] = (byte) value;
		}
		return macBytes;
	}

	/**
	 * 字节转换为字符串的转换工具函数
	 * */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	

	
	/**
	 * 解析字节转换为整形
	 * */
	public static int bytesToInt(byte[] src){
		String tmp = BytesUtil.bytesToHexString(src);
		int sum = 0;
		try{
			sum = Integer.parseInt(tmp, 16);
		}catch(NumberFormatException e){
			
		}
		return sum;		
	}
	
	public static int toInt(byte[] b, int s, int n) {
		int ret = 0;

		final int e = s + n;
		for (int i = s; i < e; ++i) {
			ret <<= 8;
			ret |= b[i] & 0xFF;
		}
		return ret;
	}
	
	/**
	 * 整形转换为byte[2]类型
	 * */
	public static byte[] intToBytes(int data){
		byte[] res = null;
		if(data > 65535){
			return res;
		}else{
			res = new byte[2];
			byte tmp1 = (byte) ((data & 0xFF00)>>8);
			byte tmp2 = (byte) (data & 0x00FF);
			res[0] = tmp1;
			res[1] = tmp2;
		}
		return res;
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
	 
	 /**
	  * 比较byte1和byte2是否相同
	  * @param bytes1
	  * @param bytes2
	  * @return
	  */
		public static boolean matchbytes(byte[] bytes1, byte[] bytes2) {
			if(bytes1==null||bytes2==null)
				return false;
			int len1=bytes1.length;
			int len2=bytes2.length;
			int start=0;
			if(len1!=len2)
				return false;
			for (byte v : bytes1) {
				if (v != bytes2[start++])
					return false;
			}
			return true;
		}
}
