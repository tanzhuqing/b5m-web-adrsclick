package com.b5m.adrs.rule;

import java.util.HashMap;
import java.util.Map;

public class RuleBean {
	//用户id
	private String uid;
	//b5t id
	private String mid;
	
	private String docId;
	
	private String ip;
	
	private long nowtime;
	
	private Map<String, String> values = new HashMap<String, String>();
	
	public RuleBean(String uid, String mid, String docId, String ip){
		this.uid = uid;
		this.mid = mid;
		this.docId = docId;
		this.ip = ip;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void put(String key, String value){
		values.put(key, value);
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public long getNowtime() {
		return nowtime;
	}

	public void setNowtime(long nowtime) {
		this.nowtime = nowtime;
	}
	
}
