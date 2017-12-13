package com.bluetoothlib.model.bt;

public class BtData {
	
	private boolean isDataComplete;
	private byte[] data;
	private int l2Length;
	
	public BtData(){
		isDataComplete=true;
	}
	
	public int getL2Length() {
		return l2Length;
	}
	public void setL2Length(int l2Length) {
		this.l2Length = l2Length;
	}
	public boolean isDataComplete() {
		return isDataComplete;
	}
	public byte[] getData() {
		if(isDataComplete){
			return data;
		}
		return null;
	}
	
	/**
	 * 设置数据
	 * @param newData
	 * @param l2   如果数据包含L1，则l2为L2的长度，否则l2为0
	 * @return 如果接收的数据已经完整则返回true，否则返回false
	 */
	public boolean setData(byte[] newData,int l2) {
		isDataComplete=false;
		boolean b=false;
		int len=0;
		if(data!=null) {
			len=data.length;
		}
		int newLen=newData.length;
		byte[] tem=new byte[newLen+len];
		for(int i=0;i<len;i++){
			tem[i]=data[i];
		}
		for(int i=0;i<newLen;i++){
			tem[i+len]=newData[i];
		}
		this.data=tem;
		if(l2!=0)
			l2Length=l2;
		if(data.length==8+l2Length){
			b=true;
			isDataComplete=true;
		}
		return b;
	}
	
	/**
	 * 清除数据
	 */
	public void clearData(){
		isDataComplete=true;
		data=null;
		l2Length=0;
	}
	
}
