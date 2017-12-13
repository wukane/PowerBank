package com.bluetoothlib.model.frame;

import com.bluetoothlib.util.BytesUtil;

/**
 * L1帧
 * 规则:
 * 1. errFlag 只有0和1两种标志
 * 2. ackFlag 也只有个0,1两种标志
 * 3. magicByte　必须为0xab开头
 * 4. 
 * @author Kenneth
 *
 */
public class L1FrameInfo {
	
	/**
	 * CRC16校验的数组
	 */
	private static int[] crc16Table = {
		    0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
		    0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
		    0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
		    0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
		    0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
		    0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
		    0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
		    0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
		    0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
		    0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
		    0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
		    0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
		    0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
		    0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
		    0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
		    0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
		    0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
		    0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
		    0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
		    0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
		    0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
		    0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
		    0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
		    0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
		    0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
		    0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
		    0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
		    0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
		    0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
		    0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
		    0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
		    0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040
		};
	
	/**
	 * 协议变量
	 */
	private static byte magicByte = (byte) 0xAB;
	private byte flagByte = 0x0;
	private byte[] payloadLength = null;
	private byte[] cRC16 = null;
	private byte[] seqId = null;
	
	private byte[] l1Frame = null;

	 /**
	  * 初始化L1针头
	  * @param errFlag
	  * @param ackFlag
	  * @param version
	  * @param payloadLen int 
	  * @param cRC16 byte[2]
	  * @param seqId byte[2]
	  * @throws FrameIllegalException
	  */
	public L1FrameInfo(byte errFlag,byte ackFlag,short version,int payloadLen,byte[] cRC16,int seqId) throws FrameIllegalException {
		if((payloadLen > 65535)||(payloadLen < 0)){
			throw new FrameIllegalException("payloadLength长度超出上限");
		}else if(cRC16.length > 2){
			throw new FrameIllegalException("CRC16长度错误");
		}else if((seqId > 65535)||(seqId < 0)){
			throw new FrameIllegalException("SeqId长度超出上限");
		}
		
		
		this.payloadLength = new byte[2];
		this.seqId = new byte[2];
		this.l1Frame = new byte[8];
		
		setErrFlag(errFlag);
		setAckFlag(ackFlag);
		setVersion(version);
		
		
		setPayloadLength(payloadLen);
		
		setcRC16(cRC16);
		setSeqId(seqId);
		refreshFrame();
	}





	//====================================GET/SET函数========================================
	//====================================GET/SET函数========================================
	//====================================GET/SET函数========================================
	public static byte getMagicByte() {
		return magicByte;
	}

	public byte getFlagByte() {
		return flagByte;
	}


	public byte[] getL1Frame() {
		refreshFrame();
		return this.l1Frame;
	}


	public byte[] getPayloadLength() {
		return payloadLength;
	}


	/**
	 * 设置payLoad长度
	 * @param payloadLength
	 */
	private void setPayloadLength(int payloadLength) {
		
		byte tmp1 = (byte) ((payloadLength & 0xFF00)>>8);
		byte tmp2 = (byte) (payloadLength & 0x00FF);
		
		this.payloadLength[0] = tmp1;
		this.payloadLength[1] = tmp2;
	}


	public byte[] getcRC16() {
		return cRC16;
	}


	private void setcRC16(byte[] cRC16) {
		this.cRC16 = cRC16;
	}


	public byte[] getSeqId() {
		return seqId;
	}


	/**
	 * 设置序号
	 * @param seqId
	 */
	private void setSeqId(int seqId) {
		byte tmp1 = (byte) ((seqId & 0xFF00)>>8);
		byte tmp2 = (byte) (seqId & 0x00FF);
		
		this.seqId[0] = tmp1;
		this.seqId[1] = tmp2;
	}
	
	/**
	 * 设置错误码
	 * @param errFlag
	 * @throws FrameIllegalException
	 */
	public void setErrFlag(byte errFlag) throws FrameIllegalException {
		if((errFlag != 0) && (errFlag != 1)){
			throw new FrameIllegalException("errFlag错误码有误,只有0,1两个数值");
		}
		byte flag = this.flagByte;
		flag = (byte) (flag | (errFlag<<5));
		this.flagByte = flag;
	}
	
	/**
	 * 设置应答码
	 * @param ackFlag
	 * @throws FrameIllegalException
	 */
	public void setAckFlag(byte ackFlag) throws FrameIllegalException {
		if((ackFlag != 0) && (ackFlag != 1)){
			throw new FrameIllegalException("AckFlag应答码有误,只有0,1两个数值");
		}
		byte flag = this.flagByte;
		flag = (byte) (flag | (ackFlag<<4));
		this.flagByte = flag;
	}
	
	/**
	 * 设置当前版本
	 * @param version  0~0xf
	 * @throws FrameIllegalException
	 */
	public void setVersion(short version) throws FrameIllegalException {
		if((version > 0xf) || (version < 0x0)){
			throw new FrameIllegalException("version版本号超出上限, 0x0~0xf");
		}
		byte flag = this.flagByte;
		flag = (byte) (flag | (byte)(version));
		this.flagByte = flag;
	}
	
	/**
	 * 获得当前错误码
	 * @return
	 */
	public byte getErrFlag(){
		byte res = 0;
		res = (byte)((this.flagByte & 0xdf)>>6);
		return res;
	}
	
	/**
	 * 获得当前应答码
	 * @return
	 */
	public byte getAckFlag(){
		byte res = 0;
		res = (byte)((this.flagByte & 0xef)>>5);
		return res;
	}
	
	/**
	 * 获得当前版本
	 * @return
	 */
	public byte getVersion(){
		byte res = 0;
		res = (byte)(this.flagByte & 0x0f);
		return res;
	}
	
	//====================================自定义使用函数========================================
	//====================================自定义使用函数========================================
	//====================================自定义使用函数========================================
	public static int calculateCRC16(int crc, byte[] l2) {
		for(int i=0;i<l2.length;i++){
			crc = crc16Byte(crc, l2[i]);
		}
	    return crc;
	}

	private static int crc16Byte(int crc, byte data) {
	    return (crc >> 8) ^ crc16Table[(crc ^ data) & 0xff];
	}
	
	/**
	 * 重新刷新frame
	 */
	private void refreshFrame(){
		if(this.l1Frame != null){
			this.l1Frame[0] = (byte)0xab;
			this.l1Frame[1] = getFlagByte();
			this.l1Frame[2] = getPayloadLength()[0];
			this.l1Frame[3] = getPayloadLength()[1];
			this.l1Frame[4] = getcRC16()[0];
			this.l1Frame[5] = getcRC16()[1];
			this.l1Frame[6] = getSeqId()[0];
			this.l1Frame[7] = getSeqId()[1];
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+ BytesUtil.bytesToHexString(this.l1Frame);
	}
}
