package com.b5m.adrs.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AnalysisLog {
	private static Log LOG = LogFactory.getLog(AnalysisLog.class);
	
	public static void info(String message, Class<?> cls){
		LOG.info("message info for class[" + cls + "]:" + message);
	}
	
	public static void error(String message, Class<?> cls){
		LOG.error("message error for class[" + cls + "]:" + message);
	}
	
	public static void error1(String message, Class<?> cls, Throwable throwable){
		LOG.error("message error for class[" + cls + "]:" + message, throwable);
	}
}
