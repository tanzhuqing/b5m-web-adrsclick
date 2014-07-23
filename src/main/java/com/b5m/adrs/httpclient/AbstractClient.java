package com.b5m.adrs.httpclient;

import org.apache.log4j.Logger;
/**
 * @Company B5M.com
 * @description
 * client 的 父类
 * @author echo
 * @since 2013-8-29
 * @email wuming@b5m.com
 */
public abstract class AbstractClient {
	protected String url;
    
	protected Logger logger = Logger.getLogger(this.getClass());
	
	public AbstractClient(String url){
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
