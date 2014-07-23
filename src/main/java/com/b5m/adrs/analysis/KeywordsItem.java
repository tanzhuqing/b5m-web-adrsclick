package com.b5m.adrs.analysis;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *<font style="font-weight:bold">Description: </font> <br/>
 * 进行两层查找，首字符查找，再第二个字符进行查找，接下来就很容易定位了
 * @author echo.weng
 * @email wuming@b5m.com
 * @since 2014年5月11日 下午8:12:04
 *
 */
public class KeywordsItem implements Item, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5229173421207063857L;

	//单个字符的hashcode
	protected Integer hashCode;
	
	protected AVLTree children;//子
	
	protected boolean isEnd;//主要第二层应用，是否结束
	
	protected List<SearchKeywords> keywordList;
	
	private KeywordsItem parent;
	
	@Override
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(Integer hashCode) {
		this.hashCode = hashCode;
	}

	public AVLTree getChildren() {
		return children;
	}

	public void setChildren(AVLTree children) {
		this.children = children;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public List<SearchKeywords> getKeywordList() {
		if(keywordList == null){
			keywordList = new ArrayList<SearchKeywords>();
		}
		return keywordList;
	}

	public void setKeywordList(List<SearchKeywords> keywordList) {
		this.keywordList = keywordList;
	}

	public KeywordsItem getParent() {
		return parent;
	}

	public void setParent(KeywordsItem parent) {
		this.parent = parent;
	}

}