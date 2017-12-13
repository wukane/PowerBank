package com.bluetoothlib.model.frame;


/**
 * 帧格式异常
 * */
public class FrameIllegalException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public FrameIllegalException(String message){
		super(message);
	}
	
	public FrameIllegalException(){
		super();
	}
	
}
