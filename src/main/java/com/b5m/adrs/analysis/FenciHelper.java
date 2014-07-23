package com.b5m.adrs.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.analysis.AVLTree.Entry;

public class FenciHelper {
	private static final int MAX_LEVEL = 3;
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 分词
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月15日 下午10:47:08
	 *
	 * @param words
	 * @param avlTree
	 * @return
	 */
	public static List<SearchKeywords> analysis(String words, AVLTree avlTree){
		if(StringUtils.isEmpty(words)) return new ArrayList<SearchKeywords>(0);
		List<SearchKeywords> resultList = new ArrayList<SearchKeywords>();
		Map<Long, SearchKeywords> keywordsMap = new HashMap<Long, SearchKeywords>();
		int length = words.length() - 1;
		if(length <= 4 && words.indexOf(" ") < 0){//搜索词小于4个的 则整个进行搜索
			find(keywordsMap, words, 0, 0, avlTree);
		}else{
			for(int i = 0 ; i < length; i++){
				try {
					find(keywordsMap, words, i, 0, avlTree);
				} catch (Exception e) {
				}
			}
		}
		for(SearchKeywords keywords : keywordsMap.values()){
			if(keywords.isHalf && keywords.halfNum == keywords.halfFindNum){
				//针对空格的 只需要 都找到即可
				resultList.add(keywords);
			}else if(keywords.getKeywords().length() <= MAX_LEVEL){
				resultList.add(keywords);
			} else if(words.indexOf(keywords.getKeywords()) >= 0){
				resultList.add(keywords);
			}
		}
		return resultList;
	}
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 建立二叉树
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月15日 下午10:47:16
	 *
	 * @param list
	 * @return
	 */
	public static AVLTree createTree(List<SearchKeywords> list){
		AVLTree avlTree = new AVLTree();
		for(SearchKeywords keywords : list){
			addToTree(keywords, avlTree);
		}
		return avlTree;
	}
	
	public static void find(Map<Long, SearchKeywords> keywordsMap, String words, int index, int level, AVLTree avlTree){
		if(avlTree == null || index + 1 > words.length()) return;
		String word = words.substring(index, index + 1);
		Entry entry = avlTree.getEntry(word.hashCode());
		if(entry == null) return;
		KeywordsItem item = (KeywordsItem) entry.element;
		if(item.isEnd() || level + 1 >= MAX_LEVEL){
			List<SearchKeywords> searchKeywordsList = item.getKeywordList();
			for(SearchKeywords searchKeywords : searchKeywordsList){
				//去掉重复的
				if(keywordsMap.keySet().contains(searchKeywords.getId())) continue;
				if(!searchKeywords.getIsHalf()){
					keywordsMap.put(searchKeywords.getId(), searchKeywords);
				}else{//只要找到一次就会+1
					SearchKeywords find = keywordsMap.get(searchKeywords.getId());
					if(find == null){//新建 主要为了不修改原来对象的值
						find = searchKeywords.clone();
						//第一次找到 赋值1
						find.setHalfFindNum(1);
						if(!find.isContain(words.substring(index - level, index + 1))){
							keywordsMap.put(find.getId(), find);
						}
					}else{//找到了 再+1
						//words.substring(index - level, index + 1) 真实查询的words
						if(!find.isContain(words.substring(index - level, index + 1))){
							find.setHalfFindNum(find.getHalfFindNum() + 1);
						}
					}
				}
			}
		}
		if(level + 1 >= MAX_LEVEL){
			return;
		}
		find(keywordsMap, words, index + 1, level + 1, item.getChildren());
	}
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 根据words 查找list 是个数据的引用，更改这个list就会更新索引里面的数据
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月20日 下午9:05:57
	 *
	 * @param words
	 * @param index
	 * @param level
	 * @param avlTree
	 * @return
	 */
	public static void remove(Long id, String words, int index, int level, AVLTree avlTree){
		if(avlTree == null || words.length() < index + 1) return;
		String word = words.substring(index, index + 1);
		Entry entry = avlTree.getEntry(word.hashCode());
		if(entry == null) return;
		KeywordsItem item = (KeywordsItem) entry.element;
		if(item.isEnd() || level + 1 >= MAX_LEVEL){
			List<SearchKeywords> searchKeywordsList = item.getKeywordList();
			for(SearchKeywords searchKeywords : searchKeywordsList){
				if(searchKeywords.getId() == id){
					searchKeywordsList.remove(searchKeywords);
					break;
				}
			}
		}
		if(level + 1 >= MAX_LEVEL){
			return;
		}
		remove(id, words, index + 1, level + 1, item.getChildren());
	}
	
	private static KeywordsItem createAndAddItem(String str, AVLTree avlTree){
		if(str.length() < 1) return null;
		String word = str.substring(0, 1);
		int hashCode = word.hashCode();
		Entry entry = avlTree.getEntry(hashCode);
		KeywordsItem item = null;
		if(entry == null){
			item = new KeywordsItem();
			item.setHashCode(hashCode);
			avlTree.add(item);
		}else{
			item = (KeywordsItem) entry.element;
		}
		return item;
	}
	
	private static boolean filter(SearchKeywords keywords){
		if(keywords == null) return true; 
		String name = keywords.getKeywords();
		if(StringUtils.isEmpty(name)) return true;
		if(name.length() < 2) return true;
		return false;
	}
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 添加子二叉树
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月15日 下午10:47:28
	 *
	 * @param keywords
	 * @param item
	 */
	private static void addChildren(SearchKeywords keywords, KeywordsItem item, int level){
		addChildren(keywords, null, item, level);
	}
	
	private static void addChildren(SearchKeywords keywords, String words, KeywordsItem item, int level){
		String str = keywords.getKeywords();
		if(!StringUtils.isEmpty(words)){
			str = words;
		}
		if(str.length() <= level) {
			item.setEnd(true);
			item.getKeywordList().add(keywords);
			return;
		}
		String word = str.substring(level, level + 1);
		AVLTree tree = item.getChildren();
		KeywordsItem _item = null;
		if(tree == null){
			tree = new AVLTree();
			item.setChildren(tree);
			_item = createItem(word, item);
			tree.add(_item);
		}else{
			Entry entry = tree.getEntry(word.hashCode());
			if(entry == null){
				_item = createItem(word, item);
				tree.add(_item);
			}else{
				_item = (KeywordsItem) entry.element;
			}
		}
		if(level + 1 >= MAX_LEVEL){
			_item.getKeywordList().add(keywords);
			return;
		}
		addChildren(keywords, words, _item, level + 1);
	}
	
	private static KeywordsItem createItem(String word, KeywordsItem item){
		KeywordsItem _item = new KeywordsItem();
		int hashCode = word.hashCode();
		_item.setHashCode(hashCode);
		_item.setParent(_item);
		return _item;
	}
	
	public static void addToTree(SearchKeywords keywords, AVLTree avlTree){
		boolean filter = filter(keywords);
		if(filter) return;
		String str = keywords.getKeywords();
		if(str.indexOf(" ") <= 0){
			KeywordsItem item = createAndAddItem(str, avlTree);
			if(item == null) return;
			addChildren(keywords, item, 1);
		}else{
			//将空格的词拆分保存到二叉树中
			String[] subKys = StringUtils.split(str, " ");
			keywords.setIsHalf(true);
			//初始值为0
			keywords.setHalfFindNum(0);
			keywords.setHalfNum(subKys.length);
			for(String subKy : subKys){
				KeywordsItem item = createAndAddItem(subKy, avlTree);
				if(item == null) continue;
				addChildren(keywords, subKy, item, 1);
			}
		}
	}
	
	public static void remove(SearchKeywords keywords, AVLTree avlTree){
		if(keywords == null) return;
		if(StringUtils.isEmpty(keywords.getKeywords())) return;
		if(keywords.getId() == 0){
			return;
		}
		remove(keywords.getId(), keywords.getKeywords(), 0, 0, avlTree);
	}
}
