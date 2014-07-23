package com.b5m.adrs.common;

public interface CallBack {
	
	<T> T callback(Object data, Class<T> rtnType);
	
}
