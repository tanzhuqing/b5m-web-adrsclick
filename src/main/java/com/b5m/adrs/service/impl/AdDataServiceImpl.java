package com.b5m.adrs.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.b5m.adrs.analysis.AVLTree;
import com.b5m.adrs.analysis.AnalysisLog;
import com.b5m.adrs.analysis.FenciHelper;
import com.b5m.adrs.analysis.SearchKeywords;
import com.b5m.adrs.cache.Cache;
import com.b5m.adrs.cache.CacheAop;
import com.b5m.adrs.domain.Product;
import com.b5m.adrs.entity.AdGood;
import com.b5m.adrs.entity.DocIdRel;
import com.b5m.adrs.service.AdDataService;
import com.b5m.base.common.utils.BeanTools;
import com.b5m.base.common.utils.StringTools;
import com.b5m.base.common.utils.WebTools;
import com.b5m.dao.Dao;
import com.b5m.dao.domain.cnd.Cnd;
import com.b5m.dao.domain.cnd.Op;

/**
 * @author echo
 *
 */
@Service("adDataService")
public class AdDataServiceImpl implements AdDataService, InitializingBean{
	
	@Autowired
	private Dao dao;
	
	@Autowired
	@Qualifier("properties")
	private Properties properties;
	
	@Autowired
	@Qualifier("threadPool")
	private ThreadPoolExecutor threadPool;
	
	@Override
	@Cache(emptyCache = true, localCache = true, key = "all_search_keywords", timeout = 18000)
	public List<SearchKeywords> queryAllSearchKeywords(){
		return dao.queryBySql("select id, keyword as keywords, goods_id, price, flag from t_keyword where (flag = 1 or flag is null) and goods_id in (select ID from T_AD_GOOD where FLAG != -1)", new Object[]{}, SearchKeywords.class);
	}
	
	public List<SearchKeywords> queryAllSearchKeywordsNoCache(){
		return queryAllSearchKeywords();
	}
	
	@Override
	public SearchKeywords querySearchKeywordsById(Long id){
		return dao.get(SearchKeywords.class, id);
	}
	
	public List<AdGood> queryAdGoodsList(){
		Cnd cnd = Cnd.where("flag", Op.NEQ, -1);
		//排除不需要查询商家的商品
		String excludeShops = properties.getProperty("exclude.record.shop");
		String[] excludeShopsArray = StringTools.split(excludeShops, ",");
		for(String excludeShop : excludeShopsArray){
			cnd.add("sourceShop", Op.NEQ, excludeShop);
		}
		List<AdGood> adGoods = dao.query(AdGood.class, cnd);
		return adGoods;
	}
	
	public AdGood queryAdGood(Long id){
		AdGood adGood = dao.get(AdGood.class, id);
		return adGood;
	}

	/*@Override
	public List<AdGood> queryAdGoodByIndexKey(String keywords, List<SearchKeywords> searchKeywordsList, Map<Long, AdGood> adGoodsMap){
		if(StringTools.isEmpty(keywords)) return new ArrayList<AdGood>(0);
		//查询所有search keywords
		Map<Long, AdGoodsNum> idMap = CollectionTools.newMap();
		for(SearchKeywords searchKeywords : searchKeywordsList){
			if(isMatch(keywords, searchKeywords.getKeywords())){
				Long id = searchKeywords.getGoodsId();
				AdGoodsNum adGoodsNum = idMap.get(id);
				if(adGoodsNum == null){//聚合id
					adGoodsNum = new AdGoodsNum(id);
					//用第一个匹配上的关键词 就作为这次显示的pv所查询的关键词
					adGoodsNum.setKeywords(searchKeywords.getKeywords());
					adGoodsNum.setKeywordsId(searchKeywords.getId());
					adGoodsNum.setPrice(searchKeywords.getPrice());
					idMap.put(id, adGoodsNum);
				}
				adGoodsNum.setNum(adGoodsNum.getNum() + 1);
			}
		}
		if(idMap.isEmpty()) return CollectionTools.newList();
		List<Long> ids = null;
		if(keywords.length() < 5){//关键词长度小于5的 不进行排序
			ids = new ArrayList<Long>(idMap.keySet());
		}else{//根据id的num数进行排序
			ids = getSortId(idMap);
		}
		List<AdGood> adGoods = queryAdGoodsByIds(ids, adGoodsMap);
		if(keywords.length() >= 5){//关键词长度大于5的 进行排序
			adGoods = sortAdGoods(ids, adGoods);
		}
		//cpc price 设值 和 关键词设置
		setCpcPriceAndKeywords(adGoods, idMap);
		Collections.sort(adGoods, new Comparator<AdGood>(){

			@Override
			public int compare(AdGood o1, AdGood o2) {
				return o2.getCpcPrice().compareTo(o1.getCpcPrice());
			}
			
		});
		return adGoods;
	}*/
	
	public boolean contain(List<AdGood> adGoods, long id){
		for(AdGood adGood : adGoods){
			if(adGood.getId() == id) return true;
		}
		return false;
	}
	
	@Override
	public List<AdGood> queryAdGoodByIndexKey(String keywords, Integer limit){
		if(StringTools.isEmpty(keywords)) return new ArrayList<AdGood>(0);
		List<SearchKeywords> findResultList = FenciHelper.analysis(keywords, CacheAop.getLocalCache().getConstant("avlTree", AVLTree.class));
		AVLTree adGoodsAvlTree = CacheAop.getLocalCache().getConstant("adGoodsAvlTree", AVLTree.class);
		//返回最匹配长度的keywords
		findResultList = mastLengthAdKeywords(findResultList, limit);
		Collections.sort(findResultList, new Comparator<SearchKeywords>(){

			@Override
			public int compare(SearchKeywords o1, SearchKeywords o2) {
				int compare = o1.getPrice().compareTo(o2.getPrice());
				return compare > 0 ? -1 : (compare == 0 ? 0 : 1);
			}
			
		});
		//根据关键词长度判断匹配度
		Collections.sort(findResultList, new Comparator<SearchKeywords>(){

			@Override
			public int compare(SearchKeywords o1, SearchKeywords o2) {
				int compare = o1.getKeywords().length() - o2.getKeywords().length();
				return compare > 0 ? -1 : (compare == 0 ? 0 : 1);
			}
			
		});
		int size = 0;
		List<AdGood> adGoods = new ArrayList<AdGood>();
		for(SearchKeywords searchKeywords : findResultList){
			long id = searchKeywords.getGoodsId();
			if(contain(adGoods, id)) continue;
			AdGood adGood = (AdGood) adGoodsAvlTree.getItem((int)id);
			if(adGood != null){
				//克隆新对象，不改变原来的对象
				adGood = adGood.cloneSimple();
				adGood.setCpcPrice(searchKeywords.getPrice());
				adGood.setKeywords(searchKeywords.getKeywords());
				adGood.setKeywordsId(searchKeywords.getId());
				if(size >= limit && limit != 0){
					break;
				}
				size++;
				adGoods.add(adGood);
			}
		}
		return adGoods;
	}
	
	public List<SearchKeywords> mastLengthAdKeywords(List<SearchKeywords> findResultList, Integer limit){
		if(findResultList.size() <= limit) return findResultList;
		List<SearchKeywords> mastLengthAdKeywords = new ArrayList<SearchKeywords>();
		mastLengthAdKeywords(findResultList, mastLengthAdKeywords, limit);
		return mastLengthAdKeywords;
	}
	
	public void mastLengthAdKeywords(List<SearchKeywords> findResultList, List<SearchKeywords> mastLengthAdKeywords, Integer limit){
		findResultList.removeAll(mastLengthAdKeywords);
		List<SearchKeywords> _mastLengthAdKeywords = new ArrayList<SearchKeywords>();
		int maxLength = 0;
		for(SearchKeywords searchKeywords : findResultList){
			if(searchKeywords.getKeywords().length() > maxLength){
				maxLength = searchKeywords.getKeywords().length();
				_mastLengthAdKeywords.clear();
			}
			_mastLengthAdKeywords.add(searchKeywords);
		}
		mastLengthAdKeywords.addAll(_mastLengthAdKeywords);
		if(mastLengthAdKeywords.size() >= limit){
			return;
		}
		mastLengthAdKeywords(findResultList, mastLengthAdKeywords, limit);
	}
	
	protected boolean isMatch(String keywords, String keywordsdb){
		if(StringTools.isEmpty(keywordsdb)) return false;
		String[] keywordsdbs = StringTools.split(keywordsdb, " ");
		if(keywordsdbs.length == 1){
			return keywords.indexOf(keywordsdb) >= 0;
		}
		boolean flag = true;
		for(String s : keywordsdbs){
			if(keywords.indexOf(s) < 0){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	public AVLTree createAdGoodsTree(List<AdGood> adGoods){
		AVLTree avlTree = new AVLTree();
		for(AdGood adGood : adGoods){
			avlTree.add(adGood);
		}
		return avlTree;
	}
	
	public AVLTree createDocIdRelTree(List<DocIdRel> docIdRels){
		AVLTree avlTree = new AVLTree();
		for(DocIdRel docIdRel : docIdRels){
			avlTree.add(docIdRel);
		}
		return avlTree;
	}
	
	@Override
	public void indexKeywords(){
		Integer totalSize = dao.queryCount(SearchKeywords.class, Cnd.where("flag", Op.EQ, 1));
		final Integer pageSize = 10000;
		int totalPage = totalSize / pageSize;
		if (totalSize % pageSize != 0) {
			totalPage = totalPage + 1;
		}
		AVLTree avlTree = new AVLTree();
		CacheAop.getLocalCache().putConstant("avlTree", avlTree);
		for(int page = 0; page < totalPage; page++){
			final int pageNum = page; 
			threadPool.submit(new Runnable(){

				@Override
				public void run() {
					long start = System.currentTimeMillis();
					String sql = "select id, keyword as keywords, goods_id, price, flag from t_keyword where flag = 1 limit ?,?";
					List<SearchKeywords> searchKeywords = dao.queryBySql(sql, new Object[]{pageNum * pageSize, pageSize}, SearchKeywords.class);
					AnalysisLog.info("---->查询(SearchKeywords)(page[" + pageNum + "]):" + (System.currentTimeMillis() - start), AdDataServiceImpl.class);
					
					start = System.currentTimeMillis();
					AVLTree avlTree = CacheAop.getLocalCache().getConstant("avlTree", AVLTree.class);
					addTree(avlTree, searchKeywords);
					AnalysisLog.info("---->索引(SearchKeywords)pageNum:" + (System.currentTimeMillis() - start), AdDataServiceImpl.class);
				}
				
			});
		}
	}
	
	@Override
	public void indexAdGoods(){
		long start = System.currentTimeMillis();
		List<AdGood> adGoods = queryAdGoodsList();
		AnalysisLog.info("---->查询(AdGood):" + (System.currentTimeMillis() - start), AdDataServiceImpl.class);
		start = System.currentTimeMillis();
		CacheAop.getLocalCache().putConstant("adGoodsAvlTree", createAdGoodsTree(adGoods));
		AnalysisLog.info("---->索引(AdGood):" + (System.currentTimeMillis() - start), AdDataServiceImpl.class);
	}
	
	@Override
	public void indexDocIdRel(){
		long start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append(" select ID as id,DOC_ID as docId from T_AD_GOOD where FLAG != -1 ");
		List<DocIdRel> docIdRels = dao.queryBySql(sb.toString(), new Object[]{}, DocIdRel.class);
		AnalysisLog.info("---->查询(AdGood):" + (System.currentTimeMillis() - start), AdDataServiceImpl.class);
		start = System.currentTimeMillis();
		CacheAop.getLocalCache().putConstant("docIdRelsAvlTree", createDocIdRelTree(docIdRels));
		AnalysisLog.info("---->索引(AdGood):" + (System.currentTimeMillis() - start), AdDataServiceImpl.class);
	}
	
	public void dealWithDataForSystemStart(){
		indexKeywords();
		threadPool.submit(new Runnable(){
			@Override
			public void run() {
				indexAdGoods();
			}
			
		});
		threadPool.submit(new Runnable(){

			@Override
			public void run() {
				indexDocIdRel();
			}
			
		});
	}
	
	public void addTree(AVLTree avlTree, List<SearchKeywords> searchKeywordsList){
		for(SearchKeywords searchKeywords : searchKeywordsList){
			synchronized (this) {
				FenciHelper.addToTree(searchKeywords, avlTree);
			}
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		dealWithDataForSystemStart();
	}

	public void runMogujieShop(){
		for(int j = 1; j <= 80; j++){
			System.out.println("---------------->" + j);
			try {
				String xml = WebTools.executeGetMethod("http://share.mogujie.cn/cps/data/b5m/cps/items/00"+j+".xml");
				List<Product> products = BeanTools.xmlToObject(xml, Product.class);
				List<String> docIds = new ArrayList<String>();
				for(Product product : products){
					String docId = StringTools.MD5(product.getUrl());
					Map map = dao.queryUniqueBySql("select ID from T_AD_GOOD where DOC_ID = ?", new Object[]{docId}, Map.class);
					if(map == null){
						dao.exeSql("insert into T_AD_GOOD(AD_ID,MANAGER_ID,SOURCE_SHOP,DOC_ID,URL,PRICE,CATEGORY,TITLE,FLAG,PICTURE,CREATE_TIME,UPDATE_TIME,SHOW_STATUS) "
								+ "values(101,46,'蘑菇街','"+docId+"','"+product.getUrl()+"','"+product.getPrice()+"','"+StringTools.replace(product.getCategory(), "-", "&gt;")
								+"','"+product.getTitle()+"',0,'"+product.getPicture()+"',now(),now(),1);", new Object[]{});
						map = dao.queryUniqueBySql("select ID from T_AD_GOOD where DOC_ID = ?", new Object[]{docId}, Map.class);
						String id = map.get("ID").toString();
						String[] tags = StringTools.split(product.getCategory(), "-");
						for(int i = 0; i < tags.length; i++){
							if(i == 0) continue;
							String tag = tags[i];
							if(tag.indexOf("/") > 0){
								String[] tagarray = StringTools.split(tag, "/");
								for(String t : tagarray){
									dao.exeSql("insert into t_keyword(manager_id,keyword,goods_id,price,user_price,flag,create_time,update_time) values(46,'" + t + "','"+id+"','0.2','0.2',1,'1400230954','1400230954')", new Object[]{});
								}
							}else{
								dao.exeSql("insert into t_keyword(manager_id,keyword,goods_id,price,user_price,flag,create_time,update_time) values(46,'" + tags[i] + "','"+id+"','0.2','0.2',1,'1400230954','1400230954')", new Object[]{});
							}
						}
					}else{
						dao.exeSql("update T_AD_GOOD set PRICE = '" + product.getPrice() + "', FLAG = 0 where DOC_ID = '" + docId + "'", new Object[]{});
					}
					docIds.add(docId);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
