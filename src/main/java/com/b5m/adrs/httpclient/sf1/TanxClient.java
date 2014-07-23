package com.b5m.adrs.httpclient.sf1;

import com.alibaba.fastjson.JSONArray;

/**
 * @Company B5M.com
 * @description
 * 
 * @author echo
 * @since 2013-11-22
 * @email wuming@b5m.com
 */
public interface TanxClient {
	
	JSONArray queryData(Integer limit, String uid);
	
}
