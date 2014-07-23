package com.b5m.adrs.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.b5m.adrs.cache.exception.CachedException;

/**
 * <p>此类封装了缓存客户端实例。对外开放了通用的缓存的操作方法，由于各个厂商的缓存的标准不一致，
 * 如果需要使用到特殊的方法，应该要调用getProxy获取厂商的缓存客户端的相应方法。
 * @author Jacky
 *
 */
public interface ICachedProxy {

	/**
	 * 获取缓存客户端的名称。
	 * @return
	 */
	String getClientName();
	
	/**
	 * 获取厂商的缓存客户端对象。
	 * @param type 
	 * @return
	 */
	<T> T getProxy(Class<T> type);
	
	/**
	 * 检索数据的超时时间
	 * @return
	 */
	long getRetriveTimeout();
	
	/**
	 * 写入数据的超时时间
	 * @return
	 */
	long getWriteTimeout();
	
	/**
	 * 停止代理
	 */
	void stop() throws IOException;
	
	/**
	 * 代理客户端是否开启。
	 * @return
	 */
	boolean isStart();
	
	/**
	 * @param key
	 * @return
	 * @throws CachedException
	 */
	Object getMaybeNull(String key) throws CachedException;

	/**
	 * @param key
	 * @param type
	 * @return
	 * @throws CachedException
	 */
	<T> T getMaybeNull(String key, Class<T> type) throws CachedException;
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	Object get(String key) throws CachedException;
	
	
	/**
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	<T> T get(String key, Class<T> type) throws CachedException;
	
	/**
	 * 删除缓存
	 * @param key
	 * @throws CachedException
	 */
	void remove(String key) throws CachedException;
	
	/**
	 * 获取数据的计数器
	 * @param key
	 * @return
	 */
	long getCounter(String key) throws CachedException;
	
	/**
	 * 缓存计数加
	 * @param key
	 * @param delta
	 * @param initValue
	 * @return
	 * @throws CachedException
	 */
	long increase(String key, long delta, long initValue) throws CachedException;
	
	/**
	 * 缓存计数减
	 * @param key
	 * @param delta
	 * @param initValue
	 * @return
	 * @throws CachedException
	 */
	long decrease(String key, long delta, long initValue) throws CachedException;
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param expiredTime
	 * @throws CachedException
	 */
	void add(String key, Object value, long expiredTime) throws CachedException;
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param expiredTime
	 * @throws CachedException
	 */
	void put(String key, Object value, long expiredTime) throws CachedException;
	
	/**
	 * 
	 * @param key
	 * @throws CachedException
	 */
	void delete(String key) throws CachedException;
	
	/**
	 * @description
	 * 多个key进行查询
	 * @param keyCollections
	 * @param type
	 * @param timeout
	 * @return
	 * @throws CachedException
	 * @author echo
	 * @since 2013-7-25
	 * @email echo.weng@b5m.com
	 */
	<T> Map<String, T> gets(List<String> keyCollections, Class<T> type) throws CachedException;
	
	/**
	 * @description
	 * 多个key进行查询
	 * @param keyCollections
	 * @param timeout
	 * @return
	 * @throws CachedException
	 * @author echo
	 * @since 2013-7-25
	 * @email echo.weng@b5m.com
	 */
	Map<String, Object> gets(List<String> keyCollections) throws CachedException;
}
