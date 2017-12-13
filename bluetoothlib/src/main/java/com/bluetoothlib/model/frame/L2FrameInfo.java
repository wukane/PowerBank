package com.bluetoothlib.model.frame;


import com.bluetoothlib.util.BytesUtil;

/**
 * L2内容对象
 * 
 * @author Administrator
 *
 */
public class L2FrameInfo {
	
	/**
	 * 协议变量
	 */
	private byte[] l2Header = null;
	private byte[] l2Key = null;
	private byte[] value = null;
	
	private byte[] l2Frame = null;
	
	/**
	 * 设置L2
	 * @param key 命令标识
	 * @param value 数据
	 * @throws FrameIllegalException
	 */
	public L2FrameInfo(byte commonId,byte version,byte key,byte[] value) throws FrameIllegalException {
		l2Header = new byte[2];
		l2Key = new byte[3];
		
		setCommonId(commonId);
		setVersion(version);
		setKey(key);
		if(value == null){
			setValueLen(0);
			setValue(null);
			this.l2Frame = new byte[5];
		}else {
			setValue(value);
			setValueLen(value.length);
			this.l2Frame = new byte[5+value.length];
		}

		//
		this.l2Frame[0] = l2Header[0];//command id
		this.l2Frame[1] = l2Header[1];//version
		//
		this.l2Frame[2] = l2Key[0];//key
		this.l2Frame[3] = l2Key[1];//key header
		this.l2Frame[4] = l2Key[2];//key header
		if(value!=null) {
			for (int i = 0; i < value.length; i++) {
				this.l2Frame[5 + i] = value[i];
			}
		}
	}
	
	//====================================GET/SET函数========================================
	//====================================GET/SET函数========================================
	//====================================GET/SET函数========================================
	public byte[] getL2Frame() {
		return l2Frame;
	}

	
	public byte[] getL2Header() {
		return l2Header;
	}

	public byte[] getL2Key() {
		return l2Key;
	}

	public byte[] getValue() {
		return value;
	}

	private void setValue(byte[] value) {
		this.value = value;
	}
	
	
	/**
	 * 设置当前commonId
	 * @param commonId
	 */
	private void setCommonId(byte commonId){
		if(l2Header != null){
			l2Header[0] = commonId;
		}
	}
	
	/**
	 * 获得当前commonId
	 * @return
	 */
	public byte getCommonId(){
		byte commonId = 0;
		if(l2Header != null){
			commonId = l2Header[0];
		}
		return commonId;
	}
	
	/**
	 * 得到当前版本号
	 * @return
	 */
	public int getVersion(){
		byte tmp = 0;
		if(l2Header != null){
			tmp = l2Header[1];
			tmp = (byte) ((0xF0 & tmp)>>4);
		}
		return tmp;
	}
	
	/**
	 * 设置当前版本号
	 */
	public void setVersion(byte version){
		byte tmp = (byte) ((version & 0x0F)<<4);
		l2Header[1] = (byte) ((l2Header[1] & 0x0F)|tmp);
	}
	
	
	/**
	 * 获得当前Key
	 * @return
	 */
	public byte getKey(){
		if(l2Key != null)
			return l2Key[0];
		return 0;
	}
	
	/**
	 * 设置key,不允许外面设置
	 * @param key
	 */
	private void setKey(byte key){
		l2Key[0] = key;
	}
	
	/**
	 * 获得当前value的长度
	 */
	public int getValueLen(){
		byte[] len = BytesUtil.subBytes(l2Key, 1, 2);
		int lenInt = BytesUtil.bytesToInt(len);
		lenInt = (lenInt & 0x01FF);
		return lenInt;
	}
	
	/**
	 * 设置当前value长度
	 * @param len
	 */
	private void setValueLen(int len){
		if(len<512){
			this.l2Key[1] = (byte) ((len & 0x0100) >> 9);
			this.l2Key[2] = (byte) ((len & 0x00FF));
		}else{
			
		}
	}
	
	//====================================自定义使用函数========================================
	//====================================自定义使用函数========================================
	//====================================自定义使用函数========================================
//	private void refreshL2Frame(){
//		if(l2Frame != null){
//			//
//			this.l2Frame[0] = l2Header[0];
//			this.l2Frame[1] = l2Header[1];
//			//
//			this.l2Frame[2] = l2Key[0];
//			this.l2Frame[3] = l2Key[1];
//			this.l2Frame[4] = l2Key[2];
//			//
//			for(int i=0;i<value.length;i++){
//				this.l2Frame[5+i] = value[i];
//			}
//		}
//	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+ BytesUtil.bytesToHexString(this.l2Frame);
	}
}
