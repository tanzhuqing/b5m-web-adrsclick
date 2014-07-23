package com.b5m.adrs.cache;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.cache.exception.CachedException;
import com.b5m.dao.utils.LogUtils;

/**
 * MemCached 工具类 备注： 还需优化配置文件
 */
public class MemCachedUtils {

	// 默认设置缓存有效时间 1 天 单位s
	public final static int DEFAULT_CACHE_TIME = 86400;
	public final static int ONE_MONTH_TIME = 2592000;

	private static ICachedProxy proxy;

	/**
	 * 默认设置memcache 缓存
	 * 
	 * @param key 缓存的key
	 * @param value 缓存的value
	 * @return
	 */
	public static void setCache(String key, Object value) {
		setCache(key, value, DEFAULT_CACHE_TIME);
	}

	/**
	 * 设置memcache 缓存包括有效时间
	 * 
	 * @param key 缓存的key
	 * @param value 缓存的value
	 * @param exp 缓存的有效时间
	 * @return
	 */
	public static void setCache(String key, Object value, int exp) {
		if (StringUtils.isNotBlank(key)) {
			try {
				MemCachedUtils.proxy.put(key, value, exp);
			} catch (Exception e) {
				LogUtils.error(MemCachedUtils.class, e);
			}
		}
	}

	/**
	 * 获取缓存对象
	 * 
	 * @param key 缓存的key
	 * @return value
	 */
	public static Object getCache(String key) {
		Object result = null;
		if (StringUtils.isNotBlank(key)) {
			try {
				result = MemCachedUtils.proxy.get(key);
			} catch (Exception e) {
				LogUtils.error(MemCachedUtils.class, e);
			}
		}
		return result;
	}
	
	public static Map<String, Object> query(List<String> keys) {
		Map<String, Object> map = null;
		try {
			map = MemCachedUtils.proxy.gets(keys);
		} catch (CachedException e) {
			LogUtils.error(MemCachedUtils.class, e);
		}
		return map;
	}
	
	public static <T> Map<String, T> query(List<String> keys, Class<T> ts) {
		Map<String, T> map = null;
		try {
			map = MemCachedUtils.proxy.gets(keys, ts);
		} catch (CachedException e) {
			LogUtils.error(MemCachedUtils.class, e);
		}
		return map;
	}

	/**
	 * 清空某缓存对象
	 * 
	 * @param key 缓存的key
	 * @return
	 */
	public static void cleanCache(String key) {
		try {
			MemCachedUtils.proxy.delete(key);
		} catch (Exception e) {
			LogUtils.error(MemCachedUtils.class, e);
		}
	}

	
	public static ICachedProxy getProxy() {
		return proxy;
	}
 
	
	public void setProxy(ICachedProxy proxy) {
		MemCachedUtils.proxy = proxy;
	}
	
}
