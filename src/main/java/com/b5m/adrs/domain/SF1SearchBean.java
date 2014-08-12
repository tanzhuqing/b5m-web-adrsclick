package com.b5m.adrs.domain;

import java.util.ArrayList;
import java.util.List;

public class SF1SearchBean {
	private Integer limit;
	private Integer offset;
	private String collection;
	private String keywords;
	private String category;
	private String sources;//商家过滤
	private String price;
	private List<CondSearchBean> condLst = new ArrayList<CondSearchBean>();
	private List<SortSearchBean> sortList = new ArrayList<SortSearchBean>();
	private SearchMode searchMode;

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}
	
	public void addCondition(String name, String operator, Object... params){
		condLst.add(new CondSearchBean(name, operator, params));
	}

	public List<CondSearchBean> getCondLst() {
		return condLst;
	}

	public void setCondLst(List<CondSearchBean> condLst) {
		this.condLst = condLst;
	}

	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}

	public List<SortSearchBean> getSortList() {
		return sortList;
	}

	public void setSortList(List<SortSearchBean> sortList) {
		this.sortList = sortList;
	}
	
	public void addSort(String name, String type){
		sortList.add(new SortSearchBean(name, type));
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
}
