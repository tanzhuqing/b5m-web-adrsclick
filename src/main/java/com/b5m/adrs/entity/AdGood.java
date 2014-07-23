package com.b5m.adrs.entity;

import java.io.Serializable;

import com.b5m.adrs.analysis.Item;
import com.b5m.dao.annotation.Column;
import com.b5m.dao.annotation.Id;
import com.b5m.dao.annotation.Table;

@Table("T_AD_GOOD")
public class AdGood implements Item, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4029139967567108054L;

	@Column(name = "PICTURE")
	private String picture;

	@Column(name = "PRICE")
	private String price;

	@Column(name = "SOURCE_SHOP")
	private String sourceShop;

	@Column(name = "DOC_ID")
	private String docId;

	@Id
	@Column(name = "ID")
	private long id;

	@Column(name = "URL")
	private String url;

	@Column(name = "AD_ID")
	private String adId;

	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "FLAG")
	private String flag;

	private String cpcPrice;
	
	private String keywords;
	
	private long keywordsId;
	
	public AdGood cloneSimple(){
		AdGood adGood = new AdGood();
		adGood.setUrl(this.url);
		adGood.setTitle(this.title);
		adGood.setDocId(this.docId);
		adGood.setPicture(this.picture);
		adGood.setPrice(this.price);
		adGood.setSourceShop(this.sourceShop);
		adGood.setId(this.id);
		adGood.setAdId(this.adId);
		return adGood;
	}
	
	@Override
	public int getHashCode() {
		return (int)id;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPicture() {
		return picture;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPrice() {
		return price;
	}

	public void setSourceShop(String sourceShop) {
		this.sourceShop = sourceShop;
	}

	public String getSourceShop() {
		return sourceShop;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocId() {
		return docId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getAdId() {
		return adId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getCpcPrice() {
		return cpcPrice;
	}

	public void setCpcPrice(String cpcPrice) {
		this.cpcPrice = cpcPrice;
	}
	
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public long getKeywordsId() {
		return keywordsId;
	}

	public void setKeywordsId(long keywordsId) {
		this.keywordsId = keywordsId;
	}

}