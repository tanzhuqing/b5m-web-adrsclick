package com.b5m.adrs.cache.exception;

public class CachedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6993206201185014456L;

	public CachedException(Exception e){
		super(e);
	}
	
	public CachedException(String msg){
		super(msg);
	}
}
