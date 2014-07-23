package com.b5m.adrs.httpclient.sf1.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.common.Constants;
import com.b5m.adrs.httpclient.AbstractClient;
import com.b5m.adrs.httpclient.HttpClientFactory;
import com.b5m.adrs.httpclient.sf1.TanxClient;

public class TanxClientImpl extends AbstractClient implements TanxClient{
	public static final Log LOG = LogFactory.getLog(TanxClientImpl.class);

	public TanxClientImpl(String url) {
		super(url);
	}

	@Override
	public JSONArray queryData(Integer limit, String uid) {
		String jsonString = buildJson(limit, uid);
		PostMethod postMethod = createPostMethod(url, jsonString);
		HttpClient httpClient = HttpClientFactory.getHttpClient();
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				String resultMsg = postMethod.getResponseBodyAsString().trim();
				if(StringUtils.isEmpty(resultMsg)) return new JSONArray();
				JSONObject jsonObject = JSONObject.parseObject(resultMsg);
				String error = jsonObject.getString("errors");
				if(!StringUtils.isEmpty(error)){
					LOG.error("query data from tanx error, error message is [" + error + "], uuid is[" + uid + "]");
					return new JSONArray();
				}
				return jsonObject.getJSONArray("resources");
			}
		} catch (HttpException e) {
			LOG.error("query data from tanx error, error message is [" + e.getMessage() + "]",  e);
		} catch (IOException e) {
			LOG.error("query data from tanx error, error message is [" + e.getMessage() + "]",  e);
		}
		return null;
	}
	
	public String buildJson(Integer limit, String uid){
		if(limit == null) limit = 10;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("collection", "tare");
		JSONObject search = new JSONObject();
		search.put("uuid", uid);
		search.put("topn", limit);
		jsonObject.put("recommend", search);
		return jsonObject.toJSONString();
	}

	private PostMethod createPostMethod(String URL, String content) {
		PostMethod method = new PostMethod(URL);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, Constants.DEFAULT_ENCODING);
		method.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 5242880);// 警报限制设置为5M
		try {
			method.setRequestEntity(new StringRequestEntity(content, Constants.STR_CONTENT_TYPE, Constants.DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
		}
		method.setRequestHeader("Connection", "Keep-Alive");
		return method;
	}
}
