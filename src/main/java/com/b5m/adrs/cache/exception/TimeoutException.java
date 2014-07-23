package com.b5m.adrs.cache.exception;


/**
 * 当访问缓存服务发生超时，应该抛出此异常。
 * @author Jacky Liu
 *
 */
public class TimeoutException extends CachedException {
	private final String operation;
	private final String key;
	
	public TimeoutException(Exception e, String operation, String key) {
		super(e);
		this.operation = operation;
		this.key = key;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1465538565813433526L;

	public String getOperation() {
		return operation;
	}

	public String getKey() {
		return key;
	}

}
