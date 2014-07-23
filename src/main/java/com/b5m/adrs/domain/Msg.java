package com.b5m.adrs.domain;

public class Msg {
	public static final int SUCCESS_CODE = 1;
	public static final int FAILED_CODE = -1;
	public static final String SUCCESS_STATUS = "success";
	public static final String FAILED_STATUS = "failed";

	private Object message;
	private int code;//1-success, -1-failed
	private String status;//success,failed
	
	public Msg(int code, String status, Object message) {
		super();
		this.message = message;
		this.code = code;
		this.status = status;
	}
	
	public static Msg newInstance(int code, String status, Object message){
		return new Msg(code, status, message);
	}
	
	public static Msg newSuccInstance(Object message){
		return new Msg(SUCCESS_CODE, SUCCESS_STATUS, message);
	}
	
	public static Msg newFailedInstance(Object message){
		return new Msg(FAILED_CODE, FAILED_STATUS, message);
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}