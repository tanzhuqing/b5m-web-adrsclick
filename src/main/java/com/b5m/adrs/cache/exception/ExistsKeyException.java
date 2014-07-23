package com.b5m.adrs.cache.exception;


/**
 * 当缓存添加数据的时候，发现Key已经存在，应该抛出此异常。
 * @author Jacky Liu
 *
 */
public class ExistsKeyException extends CachedException {
	private final String key;
	
	public ExistsKeyException(String key) {
		super(key + " has exist");
		this.key = key;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8028190562826388322L;

	public String getKey() {
		return key;
	}

}
