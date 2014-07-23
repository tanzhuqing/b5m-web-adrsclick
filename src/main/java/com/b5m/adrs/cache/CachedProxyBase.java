package com.b5m.adrs.cache;

import java.util.List;
import java.util.Map;

import com.b5m.adrs.cache.exception.CachedException;

public abstract class CachedProxyBase implements ICachedProxy {
	protected long retriveTimeout = 300;
	
	protected long writeTimeout = 300;
	
	protected volatile boolean start = true;
	
	protected int defaultCountValue = 0;
	
	public void setRetriveTimeout(long retriveTimeout){
		this.retriveTimeout = retriveTimeout;
	}
	
	@Override
	public long getRetriveTimeout() {
		return this.retriveTimeout;
	}

	public void setWriteTimeout(long writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	@Override
	public long getWriteTimeout() {
		return writeTimeout;
	}

	@Override
	public boolean isStart() {
		return start;
	}
	
	@Override
	public Object getMaybeNull(String key) throws CachedException {
		Object obj = getMaybeNull(key, Object.class);
		return obj;
	}

	@Override
	public <T> T getMaybeNull(String key, Class<T> type) throws CachedException {
		T obj = getMaybeNull(key, type, retriveTimeout);
		return obj;
	}

	@Override
	public Object get(String key) throws CachedException {
		Object obj = get(key, Object.class);
		return obj;
	}

	@Override
	public <T> T get(String key, Class<T> type) throws CachedException {
		T t = get(key, type, retriveTimeout);
		return t;
	}
	
	public void add(String key, Object value, long expiredTime) throws CachedException{
		this.add(key, value, expiredTime, writeTimeout);
	}
	
	public int getDefaultCountValue() {
		return defaultCountValue;
	}

	public void setDefaultCountValue(int defaultCountValue) {
		this.defaultCountValue = defaultCountValue;
	}
	
	@Override
	public <T> Map<String, T> gets(List<String> keyCollections, Class<T> type) throws CachedException {
		Map<String, T> map = gets(keyCollections, type, retriveTimeout);
		return map;
	}

	@Override
	public Map<String, Object> gets(List<String> keyCollections) throws CachedException {
		Map<String, Object> map = gets(keyCollections, retriveTimeout);
		return map;
	}

	protected abstract <T> T get(String key, Class<T> type, long retriveTimeout) throws CachedException;
	
	protected abstract <T> T getMaybeNull(String key, Class<T> type, long retriveTimeout) throws CachedException;

	protected abstract void add(String key, Object value, long expiredTime, long writeTimeout) throws CachedException;
	
	protected abstract Map<String, Object> gets(List<String> keyCollections, long timeout) throws CachedException;
	
	protected abstract <T> Map<String, T> gets(List<String> keyCollections, Class<T> type, long timeout) throws CachedException;
}
