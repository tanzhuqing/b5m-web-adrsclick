package com.b5m.adrs.cache.exception;


/**
 * 如果数据键在缓存中不存在，应该抛出此异常。
 * @author Jacky Liu
 *
 */
public class NonExistsKeyException extends CachedException {

	private final String key;
	
	public NonExistsKeyException(String key) {
		super(key + " isn't exist");
		this.key = key;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 623222299651910597L;

	public String getNonExistsKey() {
		return key;
	}
	
}
