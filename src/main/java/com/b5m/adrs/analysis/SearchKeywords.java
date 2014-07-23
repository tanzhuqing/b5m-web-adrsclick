package com.b5m.adrs.analysis;

import java.io.Serializable;

import com.b5m.dao.annotation.Column;
import com.b5m.dao.annotation.Id;
import com.b5m.dao.annotation.Table;
/**
 * @description
 * 为了分词查找更加快速，所以放在该文件夹下
 * @author echo
 * @time 2014年5月19日
 * @mail wuming@b5m.com
 */
@Table("t_keyword")
public class SearchKeywords implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6334891000031957461L;
	@Id
	private long id;
	
	@Column(name = "keyword")
	private String keywords;
	
	@Column(name = "goods_id")
	private long goodsId;
	
	@Column
	private int flag;
	
	@Column
	private String price;
	//是否被拆分的 有空格的关键词会进行拆分 为了模糊查询
	protected boolean isHalf = false;
	//被拆分成几个
	protected int halfNum;
	//搜索到几个
	protected int halfFindNum;
	//查找的关键词组合，防止重复
	protected StringBuilder sb;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public boolean getIsHalf() {
		return isHalf;
	}

	public void setIsHalf(boolean isHalf) {
		this.isHalf = isHalf;
	}

	public int getHalfNum() {
		return halfNum;
	}

	public void setHalfNum(int halfNum) {
		this.halfNum = halfNum;
	}

	public int getHalfFindNum() {
		return halfFindNum;
	}

	public void setHalfFindNum(int halfFindNum) {
		this.halfFindNum = halfFindNum;
	}
	
	public boolean isContain(String key){
		if(sb == null) {
			sb = new StringBuilder(key);
			return false;
		}else{
			if(sb.indexOf(key) < 0){
				sb.append(key);
				return false;
			}
			return true;
		}
	}
	
	public SearchKeywords clone(){
		SearchKeywords searchKeywords = new SearchKeywords();
		searchKeywords.setFlag(this.flag);
		searchKeywords.setGoodsId(this.goodsId);
		searchKeywords.setId(this.id);
		searchKeywords.setKeywords(this.keywords);
		searchKeywords.setPrice(this.price);
		searchKeywords.setHalfFindNum(this.halfFindNum);
		searchKeywords.setIsHalf(this.isHalf);
		searchKeywords.setHalfNum(this.halfNum);
		return searchKeywords;
	}
	

	@Override
	public String toString() {
		return "SearchKeywords [id=" + id + ", keywords=" + keywords
				+ ", goodsId=" + goodsId + ", flag="
				+ flag + ", price=" + price + "]";
	}

}
