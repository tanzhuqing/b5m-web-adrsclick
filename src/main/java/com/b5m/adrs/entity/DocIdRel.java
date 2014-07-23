package com.b5m.adrs.entity;

import com.b5m.adrs.analysis.Item;

/**
 * @author echo
 * 为了只查询需要的字段
 */
public class DocIdRel implements Item{
	
	private String docId;
	
	private String id;

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int getHashCode() {
		return docId.hashCode();
	}
	
}
