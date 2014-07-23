package com.b5m.adrs.domain;

/**
 * description 
 *
 * @Company b5m
 * @author echo
 * @since 2014年2月18日
 */
public class CondSearchBean {
	private String name;
	private String operator;
	private String[] params;
	
	public CondSearchBean(String name, String operator, String... params){
		this.name = name;
		this.operator = operator;
		this.params = params;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

}
