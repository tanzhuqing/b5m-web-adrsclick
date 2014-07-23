package com.b5m.adrs.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class WebUtils {
	
	public static String referer(HttpServletRequest req){
		String referer = req.getHeader("referer");
		if(StringUtils.isEmpty(referer)){
			referer = req.getHeader("Referer");
		}
		return referer;
	}
	
}
