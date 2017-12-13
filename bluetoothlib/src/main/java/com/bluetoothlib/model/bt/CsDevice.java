package com.bluetoothlib.model.bt;

/**
 * 蓝牙必须信息实体类
 * */
public class CsDevice {

	private String btName;
	private String btMac;

	public void setBtName(String name) {
		btName = name;
	}

	public void setBtMac(String mac) {
		btMac = mac;
	}

	public String getBtName() {
		return btName;
	}

	public String getBtMac() {
		return btMac;
	}

	@Override
	public String toString() {
		return "CsDevice [btName=" + btName + ", btMac=" + btMac + "]";
	}
}
