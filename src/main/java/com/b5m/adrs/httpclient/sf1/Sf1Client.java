package com.b5m.adrs.httpclient.sf1;

import com.alibaba.fastjson.JSONArray;
import com.b5m.adrs.domain.SF1SearchBean;

/**
 * @Company B5M.com
 * @description
 * 
 * @author echo
 * @since 2013-11-22
 * @email wuming@b5m.com
 */
public interface Sf1Client {
	
	JSONArray queryData(SF1SearchBean searchBean);
	
}
