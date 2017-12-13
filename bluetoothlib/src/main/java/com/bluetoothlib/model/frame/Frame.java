package com.bluetoothlib.model.frame;

import android.util.Log;

import com.bluetoothlib.util.BytesUtil;


/**
 * 帧协议对象
 * 在这里处理所有和协议有关的内容，然后提供出函数
 * UI页面端直接调用具体的函数就可以获得相应的数据
 * */
public class Frame {
	
	
	/**
	 * 全局序列号
	 */
	private static int seqId = -1;
	
	private L1FrameInfo l1Info;
	private L2FrameInfo l2Info;
	
	private byte[] fullFrameInfo;
	

	/**
	 * 初始化命令
	 * @param errFlag   	错误码标志  1 错误，0正常
	 * @param ackFlag   	相应码标志  1 响应   0正常
	 * @param l1Version 	L1版本号    
	 * @param payloadLen	L2部分长度
	 * @param seqId		        序列号
	 * @param commonId      当前命令commonId (标示当前为什么命令)
	 * @param l2Version     L2版本号
	 * @param key           当前命令commonId下的子命令
	 * @param value         数据内容
	 * @throws FrameIllegalException
	 */
	private Frame(byte errFlag,byte ackFlag,short l1Version,int payloadLen,int seqId,byte commonId,byte l2Version,byte key,byte[] value) throws FrameIllegalException {
		
		l2Info = new L2FrameInfo(commonId, l2Version, key, value);
		int crc = L1FrameInfo.calculateCRC16(0, l2Info.getL2Frame());
		l1Info = new L1FrameInfo(errFlag, ackFlag, l1Version, l2Info.getL2Frame().length, BytesUtil.intToBytes(crc), seqId);
		fullFrameInfo = new byte[l1Info.getL1Frame().length+l2Info.getL2Frame().length];
		for(int i=0;i<l1Info.getL1Frame().length;i++){
			fullFrameInfo[i] = l1Info.getL1Frame()[i];
		}
		for(int i=0;i<l2Info.getL2Frame().length;i++){
			fullFrameInfo[8+i]=l2Info.getL2Frame()[i];
		}
	}
	
	/**
	 * 初始化命令
	 * @param errFlag   	错误码标志  1 错误，0正常
	 * @param ackFlag   	相应码标志  1 响应   0正常
	 * @param l1Version 	L1版本号    
	 * @param commonId      当前命令commonId (标示当前为什么命令)
	 * @param l2Version     L2版本号
	 * @param key           当前命令commonId下的子命令
	 * @param value         数据内容
	 * @throws FrameIllegalException
	 */
	public Frame(byte errFlag,byte ackFlag,short l1Version,byte commonId,byte l2Version,byte key,byte[] value) throws FrameIllegalException {
		
		addSeqId();
		l2Info = new L2FrameInfo(commonId, l2Version, key, value);
		int crc = L1FrameInfo.calculateCRC16(0, l2Info.getL2Frame());
		Log.e("ble","framcrc===== "+crc);
		l1Info = new L1FrameInfo(errFlag, ackFlag, l1Version, l2Info.getL2Frame().length, BytesUtil.intToBytes(crc), seqId);
		fullFrameInfo = new byte[l1Info.getL1Frame().length+l2Info.getL2Frame().length];
		for(int i=0;i<l1Info.getL1Frame().length;i++){
			fullFrameInfo[i] = l1Info.getL1Frame()[i];
		}
		for(int i=0;i<l2Info.getL2Frame().length;i++){
			fullFrameInfo[8+i]=l2Info.getL2Frame()[i];
		}
		
	}

	/**
	 * 只有L1的响应
	 * @throws FrameIllegalException
	 */
	private	Frame(byte errFlag,byte ackFlag,short l1Version,int seqId) throws FrameIllegalException {
		l1Info = new L1FrameInfo(errFlag, ackFlag, l1Version, 0, new byte[2], seqId);
		fullFrameInfo=new byte[8];
		for(int i=0;i<l1Info.getL1Frame().length;i++){
			fullFrameInfo[i]=l1Info.getL1Frame()[i];
		}
	}
	
	
	/**
	 * 响应Frame
	 * @param iserror 是否错误
	 * @param
	 * @return
	 * @throws FrameIllegalException
	 */
	public static Frame responseFrame(boolean iserror, short l1Version, int seqId) throws FrameIllegalException {
		byte errFlag=0x0;
		if(iserror){
			errFlag=0x01;
		}
		return new Frame(errFlag, (byte)0x01, l1Version,seqId);
	}
	
	/**
	 * 解析设备传送过来的数据
	 * @param data
	 * @return
	 * @throws FrameIllegalException
	 */
	public static Frame constructFram(byte[] data) throws FrameIllegalException {
		int length=data.length-13;
		if(length<0)
			return null;
		byte[] value= BytesUtil.subBytes(data, 13, data.length-13);
		byte ml2Version=(byte) (0x00|data[9]>>4);
		byte errFlag=(byte)(0x00|(data[1]>>5));
		byte ackFlag=(byte)(0x00|((data[1]<<3)>>7));
		byte ml1Version=(byte) (0x00|((data[1]<<4)>>4));
		int payloadLen = BytesUtil.bytesToInt(BytesUtil.subBytes(data, 1, 2));
		byte[] cRC16 = BytesUtil.subBytes(data, 4, 2);
		int seqlid = BytesUtil.bytesToInt(BytesUtil.subBytes(data, 6, 2));
		Frame mFrame=new Frame(errFlag, ackFlag, ml1Version, payloadLen, seqlid, data[8], ml2Version, data[10], value);
		do{
			if((errFlag|0x00)!=0){
				break;
			}
			if(!BytesUtil.matchbytes(cRC16, mFrame.getL1Info().getcRC16())){
				break;
			}
			if(!BytesUtil.matchbytes(BytesUtil.subBytes(data, 2, 2), mFrame.getL1Info().getPayloadLength())){
				
			}
		return mFrame;
		}while(false);
		return null;
	}
	
	//====================================GET/SET函数========================================
	//====================================GET/SET函数========================================
	//====================================GET/SET函数========================================
	public L1FrameInfo getL1Info() {
		return l1Info;
	}
	
	public void setL1Info(L1FrameInfo l1Info) {
		this.l1Info = l1Info;
	}
	public L2FrameInfo getL2Info() {
		return l2Info;
	}
	public void setL2Info(L2FrameInfo l2Info) {
		this.l2Info = l2Info;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return BytesUtil.bytesToHexString(fullFrameInfo);
	}

	public static int getSeqId() {
		return seqId;
	}

	public byte[] getFullFrameInfo() {
		return fullFrameInfo;
	}

	//====================================自定义函数========================================
	//====================================自定义函数========================================
	//====================================自定义函数========================================	
	/**
	 * 自增加
	 */
	public static void addSeqId(){
		seqId++;
	}
	
	/**
	 * 获得具体的分包的帧,就是获得具体需要分为多少个帧包发送，每个帧包内容等！
	 * @return
	 */
	public byte[][] getSpecialFrame(){
		byte[][] specialFrame = null;
		if(this.fullFrameInfo != null){
			int num = this.fullFrameInfo.length/20;
			int num2=this.fullFrameInfo.length%20;
			int other;
			if(num2==0){
			specialFrame=new byte[num][];
				other=20;
			}else {
				specialFrame = new byte[num + 1][];
				other = this.fullFrameInfo.length-num*20;
			}
			for(int i=0;i<specialFrame.length;i++){
				if(i == specialFrame.length-1){
					specialFrame[i] = BytesUtil.subBytes(fullFrameInfo, i*20, other);
				}else{
					specialFrame[i] = BytesUtil.subBytes(this.fullFrameInfo, i*20, 20);
				}
			}
		}
		return specialFrame;
	}

	
	
}
