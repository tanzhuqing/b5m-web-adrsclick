package com.b5m.adrs.cache;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.alibaba.fastjson.JSON;
import com.b5m.base.common.Lang;

@Aspect
public class CacheAop {
	private final Logger logger = Logger.getLogger(this.getClass());
	private static LocalCache localCache = new LocalCache();
	private ExecutorService executorService = Executors.newFixedThreadPool(2);
	
	@Pointcut("@annotation(com.b5m.adrs.cache.Cache)")
	public void pointcut(){
		
	}
	
	//com.b5m.service.www.SearchRecommendService
	@Around(value = "pointcut()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Method method = getMethod(pjp);
		Cache cache = method.getAnnotation(Cache.class);
		if(cache == null){
			return pjp.proceed();
		}
		String key = cache.key();
		Object result = null;
		if(StringUtils.isEmpty(key)){
			key = DigestUtils.md5Hex(createKey(pjp));
		}
		if(cache.localCache()){
			result = localCache.getAlwithExsist(key);
			if(result != null && localCache.get(key) == null){//总是能取到数据，如果数据过期，默认会取得到第一次，接下来会更新数据
				updateCache(pjp, cache);
			}
		}
		if(result == null){
			result = MemCachedUtils.getCache(key);
			if(result != null && cache.localCache()){//设置本地缓存
				localCache.put(key, result, cache.timeout() * 1000000);
			}
		}
		if(result == null){
			synchronized (key) {
				result = MemCachedUtils.getCache(key);
				if(result == null){
					result = pjp.proceed();
				}
				if(result == null && !cache.emptyCache()){
					return result;
				}
				if(Lang.isList(result) && ((List<?>) result).isEmpty() && !cache.emptyCache()){
					return result;
				}
				if(Lang.isMap(result) && ((Map) result).isEmpty() && !cache.emptyCache()){
					return result;
				}
				MemCachedUtils.setCache(key, result, cache.timeout());
				if(cache.localCache()){
					localCache.put(key, result, cache.timeout() * 1000000);
				}
			}
		}
        return result;  
    }
	
	private void updateCache(final ProceedingJoinPoint pjp, final Cache cache){
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				Object result = null;
				try {
					result = pjp.proceed();
					MemCachedUtils.setCache(cache.key(), result, cache.timeout());
					localCache.put(cache.key(), result, cache.timeout() * 1000000);
				} catch (Throwable e) {
				}
			}
		});
	}
	
	private String createKey(ProceedingJoinPoint pjp){
		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();
		Object[] args = pjp.getArgs();
		StringBuilder key = new StringBuilder();
		key.append(className);
		key.append("_");
		key.append(methodName);
		key.append("_");
		for(Object arg : args){
			if(arg instanceof HttpServletRequest){
				continue;
			}
			if(arg instanceof HttpServletResponse){
				continue;
			}
			key.append(JSON.toJSONString(arg));
			key.append("_");
		}
		return key.toString();
	}
	
	private Method getMethod(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		String methodName = pjp.getSignature().getName();
		Method[] methods = pjp.getTarget().getClass().getMethods();
		for(Method method : methods){
			if(method.getName().equals(methodName) && method.getParameterTypes().length == args.length) {
				return method;
			}
		}
		return null;
	}
	
	public static void clearCache(String key){
		localCache.remove(key);
		MemCachedUtils.cleanCache(key);
	}
	
	public static LocalCache getLocalCache(){
		return localCache;
	}
	
}
