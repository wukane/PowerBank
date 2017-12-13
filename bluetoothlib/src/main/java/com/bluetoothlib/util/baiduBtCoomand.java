package com.bluetoothlib.util;


import com.bluetoothlib.model.frame.Frame;
import com.bluetoothlib.model.frame.FrameIllegalException;

public class baiduBtCoomand {
	
	/**
	 * 绑定
	 * @param value
	 * @return
	 * @throws FrameIllegalException
	 */
	public static Frame bindCommand(byte[] value) throws FrameIllegalException {
//		return new Frame((byte)0, (byte)0,(byte) 0,(byte)0x03, (byte)0, (byte)0x01, value);
		return new Frame((byte)0, (byte)0,(byte) 0,(byte)0x03, (byte)0, (byte)0x06, value);//超级绑定
//		return new Frame((byte)0, (byte)0,(byte) 0,(byte)0x06, (byte)0, (byte)0x10, value);
	}
	
	/**
	 * 手机端向设备写出apdu指令
	 * @param value
	 * @return
	 * @throws FrameIllegalException
	 */
	public static Frame writeAPDU(byte[] value) throws FrameIllegalException {
		if(value==null)
			return null;
		return new Frame((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0b, (byte)0x00, (byte)0x01, value);
	}


	
}
