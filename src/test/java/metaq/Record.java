package metaq;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -33420482666472548L;

	private String keywords;
	
	private Integer goodsId;
	
	private Date createTime;
	
	private Integer keywordsId;
	
	private Integer type;

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getKeywordsId() {
		return keywordsId;
	}

	public void setKeywordsId(Integer keywordsId) {
		this.keywordsId = keywordsId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Record [keywords=" + keywords + ", goodsId=" + goodsId
				+ ", createTime=" + createTime + ", keywordsId=" + keywordsId
				+ ", type=" + type + "]";
	}

}
