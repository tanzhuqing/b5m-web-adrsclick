package com.b5m.adrs.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
	
	String key() default "";
	
	//单位秒
	int timeout() default 7200;
	
	boolean localCache() default false;
	
	boolean emptyCache() default false;
}
