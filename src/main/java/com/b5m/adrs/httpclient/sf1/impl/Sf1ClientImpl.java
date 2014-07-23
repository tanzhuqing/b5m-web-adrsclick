package com.b5m.adrs.httpclient.sf1.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.common.Constants;
import com.b5m.adrs.domain.SF1SearchBean;
import com.b5m.adrs.httpclient.AbstractClient;
import com.b5m.adrs.httpclient.HttpClientFactory;
import com.b5m.adrs.httpclient.sf1.Sf1Client;
import com.b5m.adrs.utils.Sf1Helper;
import com.b5m.base.common.utils.StringTools;

/**
 * @Company B5M.com
 * @description
 * 
 * @author echo
 * @since 2013-11-22
 * @email wuming@b5m.com
 */
public class Sf1ClientImpl extends AbstractClient implements Sf1Client{
	public static final Log LOG = LogFactory.getLog(Sf1ClientImpl.class);

	public Sf1ClientImpl(String url) {
		super(url);
	}
	
	@Override
	public JSONArray queryData(SF1SearchBean searchBean){
		String jsonString = Sf1Helper.buildJson(searchBean);
		String message = doInternalSearch(url, jsonString);
		if(StringTools.isEmpty(message)) return new JSONArray();
		JSONObject jsonObject = JSONObject.parseObject(message);
		JSONArray jsonArray = jsonObject.getJSONArray("resources");
		boolean flag = false;
		if(jsonArray == null || jsonArray.size() < 1){
			JSONArray removedKeywords = jsonObject.getJSONArray("removed_keywords");
			if(removedKeywords != null && removedKeywords.size() > 0){
				jsonArray = removedKeywords.getJSONObject(0).getJSONArray("resources");
				flag = true;
			}
		}
		setValue(jsonArray, flag);
		return jsonArray;
	}
	
	public static void setValue(JSONArray jsonArray, boolean flag){
		int length = jsonArray.size();
		for(int i = 0; i < length; i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			jsonObject.put("isAd", flag);
		}
	}
	
	public static String doInternalSearch(String url, String jsonString) {
		PostMethod method = null;
		// 创建请求方法
		try {
			long start = System.currentTimeMillis();
			HttpClient httpClient = HttpClientFactory.getHttpClient();
			httpClient.getParams().setSoTimeout(500);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(500);
			method = createPostMethod(url, jsonString.toString());
			// 执行请求
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				// 获得返回串
				String resultMsg = method.getResponseBodyAsString().trim();
				LOG.debug(resultMsg.substring(resultMsg.indexOf("timers")) + "[" + (System.currentTimeMillis() - start) + "]");
//				LOG.debug(new StringBuilder("收到返回").append(resultMsg).toString());
				return resultMsg;
			} else {
				LOG.error("Connect fail, url:" + url + " code:" + statusCode);
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException Connect time out , url:" + url + ",");
			return null;
		} catch (HttpException e) {
			LOG.error("HttpException Connect time out , url:" + url + ",");
			return null;
		} catch (IOException e) {
			LOG.error("IOException Connect time out , url:" + url + ",");
			return null;
		} catch (Exception e) {
			LOG.error("Connect time out , url:" + url + ", exception class" + e.getClass());
			return null;
		}finally {
			if (null != method) {
				method.releaseConnection();
			}
		}
	}
	
	private static PostMethod createPostMethod(String URL, String content) throws UnsupportedEncodingException {
		PostMethod method = new PostMethod(URL);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, Constants.DEFAULT_ENCODING);
		method.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 5242880);// 警报限制设置为5M
		method.setRequestEntity(new StringRequestEntity(content, Constants.STR_CONTENT_TYPE, Constants.DEFAULT_ENCODING));
		method.setRequestHeader("Connection", "Keep-Alive");
		return method;
	}
	
}
