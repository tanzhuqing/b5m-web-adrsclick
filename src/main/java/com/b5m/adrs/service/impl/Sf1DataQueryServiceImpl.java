package com.b5m.adrs.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.analysis.AVLTree;
import com.b5m.adrs.cache.Cache;
import com.b5m.adrs.cache.CacheAop;
import com.b5m.adrs.cache.MemCachedUtils;
import com.b5m.adrs.common.Constants;
import com.b5m.adrs.domain.SF1SearchBean;
import com.b5m.adrs.domain.SearchMode;
import com.b5m.adrs.entity.AdGood;
import com.b5m.adrs.entity.DocIdRel;
import com.b5m.adrs.httpclient.sf1.Sf1Client;
import com.b5m.adrs.httpclient.sf1.TanxClient;
import com.b5m.adrs.service.AdDataService;
import com.b5m.adrs.service.Sf1DataQueryService;
import com.b5m.adrs.utils.LogUtils;
import com.b5m.adrs.utils.WebUtils;
import com.b5m.base.common.Lang;
import com.b5m.base.common.utils.LogTools;
import com.b5m.base.common.utils.StringTools;
import com.b5m.base.common.utils.WebTools;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.producer.MessageProducer;
import com.taobao.metamorphosis.exception.MetaClientException;

/**
 * @Company B5M.com
 * @description
 * 
 * @author echo
 * @since 2013-11-22
 * @email wuming@b5m.com
 */
@Service("sf1DataQueryService")
public class Sf1DataQueryServiceImpl implements Sf1DataQueryService{
	private static final String CHAR_SET = "UTF-8";
	
	@Autowired
	private Sf1Client sf1Client;
	@Autowired
	private TanxClient tanxClient;
	@Resource
	private AdDataService adDataService;
	@Resource(name = "properties")
	private Properties properties;
	@Autowired
	@Qualifier("messageProducer")
	private MessageProducer messageProducer;
	@Autowired
	@Qualifier("threadPool")
	private ThreadPoolExecutor threadPool;
	
	@Override
	public JSONArray queryTanxData(Integer limit, String uid) {
		return tanxClient.queryData(limit, uid);
	}
	
	@Override
	@Cache
	public JSONArray queryDataFromSf1(SF1SearchBean searchBean) {
		try {
			return sf1Client.queryData(searchBean);
		} catch (Exception e) {
			LogTools.error(getClass(), "query sf1 time out");
			return new JSONArray();
		}
	}
	
	/*@Override
	public JSONArray queryData(final Integer limit, final Integer offset, final String keywords, final String category, final Boolean isDetail, final String price, final boolean isNoCache) {
		if(!isNoCache){//不进行缓存的 不需要记录
			LogUtils.info(keywords, "keywords", false);
		}
		List<Callable<JSONArray>> tasks = new ArrayList<Callable<JSONArray>>();
		tasks.add(new Callable<JSONArray>() {

			@Override
			public JSONArray call() throws Exception {
				JSONArray newJsonArray = queryDataFromDB(limit, keywords, price, isNoCache);
				return newJsonArray;
			}
		});
		tasks.add(new Callable<JSONArray>() {

			@Override
			public JSONArray call() throws Exception {
				JSONArray jsonArray = queryDataFromSf1(limit + 10, offset, keywords, "cpcpromote", category, isDetail);
				return jsonArray;
			}
		});
		JSONArray[] jsonArrayList = ThreadTools.executor(tasks, JSONArray.class, threadPool);
		JSONArray first = jsonArrayList[0];
		if(first == null){
			first = new JSONArray();
		}
		Set<String> docIds = getDocids(first);
		
		JSONArray second = jsonArrayList[1];
		if(second == null){
			second = new JSONArray();
		}
		int sf1DataSize = second.size();
		for(int i = 0; i < sf1DataSize; i++){
			JSONObject jsonObject = second.getJSONObject(i);
			String docId = jsonObject.getString("DOCID");
			if(!docIds.contains(docId)){//判断是否已经存在数据了
				first.add(jsonObject);
				docIds.add(docId);
			}
		}
		Collections.sort(first, new Comparator<Object>(){

			@Override
			public int compare(Object o1, Object o2) {
				JSONObject first = (JSONObject) o1;
				JSONObject second = (JSONObject) o2;
				return second.getString("CpcPrice").compareTo(first.getString("CpcPrice"));
			}
			
		});
		JSONArray newJsonArray = new JSONArray();
		int length = first.size();
		for(int index = 0; index < limit && index < length; index++){
			newJsonArray.add(first.get(index));
		}
		//从db查询数据
		int dataSize = 0;
		if(newJsonArray != null){
			dataSize = newJsonArray.size();
		}
		//如果数据库中匹配的数据超过了limit 则直接返回数据库中的数据
		if(dataSize >= limit) return newJsonArray;
		//从sf1查询数据 添加cpc数据
		JSONArray jsonArray = queryDataFromSf1(limit, offset, keywords, "cpc", category, isDetail);
		addDataFromSf1(newJsonArray, jsonArray, docIds, dataSize, limit);
		return newJsonArray;
	}*/
	
	public JSONArray queryData(Integer limit, Integer offset, String keywords, String category, Boolean isDetail, String price, boolean isNoCache, String ref) {
//		if("b5trecom".equals(ref)){
//			JSONArray jsonArray = queryDataFromSf1("苏宁易购", limit, offset, keywords, "cpcpromote", category, isDetail);
//			int length = jsonArray.size();
//			for(int index = 0; index < length; index++){
//				JSONObject jsonObject = jsonArray.getJSONObject(index);
//				jsonObject.put("CpcPrice", "0.1");
//			}
//			return jsonArray;
//		}
		if(!isNoCache){//不进行缓存的 不需要记录
			LogUtils.info(keywords, "keywords", false);
		}
		//从db查询数据
		JSONArray newJsonArray = queryDataFromDB(limit, keywords, price, isNoCache);
		int dataSize = 0;
		if(newJsonArray != null){
			dataSize = newJsonArray.size();
		}
		//如果数据库中匹配的数据超过了limit 则直接返回数据库中的数据
		if(dataSize >= limit) return newJsonArray;
		//从sf1查询数据 添加cpcpromote数据
		String source = "";
		if(isTimelimit()){//如果是在时间限制内的，则只查询蘑菇街的数据，主要为了去除m18的数据
			source = "蘑菇街";
		}
		JSONArray jsonArray = queryDataFromSf1(source, limit, offset, keywords, "cpcpromote", category, isDetail, price);
		Collections.sort(jsonArray, new Comparator<Object>(){

			@Override
			public int compare(Object o1, Object o2) {
				JSONObject jsono1 = (JSONObject) o1;
				JSONObject jsono2 = (JSONObject) o2;
				String price1 = jsono1.getString("CpcPrice");
				int compare = 0;
				if(StringUtils.isEmpty(price1)){
					compare = 1;
				}else{
					compare = jsono2.getString("CpcPrice").compareTo(jsono1.getString("CpcPrice"));
				}
				return compare > 0 ? -1 : (compare == 0 ? 0 : 1);
			}
			
		});
		Set<String> docIds = getDocids(newJsonArray);
		if(dataSize == 0 && jsonArray != null){//如果数据库中没有匹配的数据 则不用进行添加工作了
			newJsonArray = jsonArray;
			dataSize = newJsonArray.size();
		}else{
			dataSize = addDataFromSf1(newJsonArray, jsonArray, docIds, dataSize, limit);
		}
		if(dataSize >= limit) return newJsonArray;
		//从sf1查询数据 添加cpc数据
		jsonArray = queryDataFromSf1(limit, offset, keywords, "cpc", category, isDetail, price);
		addDataFromSf1(newJsonArray, jsonArray, docIds, dataSize, limit);
		return newJsonArray;
	}
	//临晨到早上11点 
	protected boolean isTimelimit(){
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour >= 0 && hour < 11){
			return true;
		}
		return false;
	}
	
	@Override
	public JSONArray queryData(Integer limit, Integer offset, String keywords, String category, Boolean isDetail, String price) {
		return queryData(limit, offset, keywords, category, isDetail, price, false, "");
	}
	
	@Override
	public JSONArray queryData(Integer limit, Integer offset, String keywords, String category, Boolean isDetail, String price, String ref) {
		return queryData(limit, offset, keywords, category, isDetail, price, false, ref);
	}
	
	protected int addDataFromSf1(JSONArray newJsonArray, JSONArray jsonArray, Set<String> docIds, int dataSize, Integer limit){
		if(jsonArray == null) return dataSize;
		int sf1DataSize = jsonArray.size();
		for(int i = 0; i < sf1DataSize; i++){
			if(dataSize >= limit) break;
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String docId = jsonObject.getString("DOCID");
			if(!docIds.contains(docId)){//判断是否已经存在数据了
				newJsonArray.add(jsonObject);
				docIds.add(docId);
			}
			dataSize++;
		}
		return dataSize;
	}
	
	@Cache
	@Override
	public JSONArray queryData(Integer limit, Integer offset, String keywords) {
		return queryData(limit, offset, keywords, "", false, "");
	}
	
	@Cache
	@Override
	public JSONArray queryData(Integer limit, Integer offset, String keywords, String category) {
		return queryData(limit, offset, keywords, category, false, "");
	}
	
	/*public JSONArray queryDataFromDB(Integer limit, String keywords, String price, boolean isNoCache){
		JSONArray newJsonArray = new JSONArray();
		List<SearchKeywords> searchKeywordsList;
		Map<Long, AdGood> adGoodsMap;
		if(isNoCache){
			searchKeywordsList = adDataService.queryAllSearchKeywordsNoCache();
			adGoodsMap = new HashMap<Long, AdGood>();
		}else{
			searchKeywordsList = adDataService.queryAllSearchKeywords();
			adGoodsMap = adDataService.queryAllAdGoods();
		}
		List<AdGood> adGoods = adDataService.queryAdGoodByIndexKey(keywords, searchKeywordsList, adGoodsMap);
		if(adGoods.isEmpty()) return newJsonArray;
		//针对limit为1的进行特殊处理，不然相同条件的广告 有些就可能永远不出来
		if(limit == 1){
			AdGood adGood = getAdGoodForLimitIsOnly(adGoods, price);
			newJsonArray.add(convertTo(adGood));
			return newJsonArray;
		}
		int num = 0;
		for(AdGood adGood : adGoods){
			JSONObject jsonObject = convertTo(adGood);
			if(jsonObject != null){
				if(num >= limit) {
					break;
				}
				newJsonArray.add(jsonObject);
			}
			num++;
		}
		return newJsonArray;
	}*/
	
	public JSONArray queryDataFromDB(Integer limit, String keywords, String price, boolean isNoCache){
		JSONArray newJsonArray = new JSONArray();
		List<AdGood> adGoods = adDataService.queryAdGoodByIndexKey(keywords, limit);
		if(adGoods.isEmpty()) return newJsonArray;
		//针对limit为1的进行特殊处理，不然相同条件的广告 有些就可能永远不出来
		if(limit == 1){
			AdGood adGood = getAdGoodForLimitIsOnly(adGoods, price);
			newJsonArray.add(convertTo(adGood));
			return newJsonArray;
		}
		int num = 0;
		for(AdGood adGood : adGoods){
			JSONObject jsonObject = convertTo(adGood);
			if(jsonObject != null){
				if(num >= limit) {
					break;
				}
				newJsonArray.add(jsonObject);
			}
		}
		return newJsonArray;
	}

	//当limit为1的时候 返回商品 
	public AdGood getAdGoodForLimitIsOnly(List<AdGood> adGoods, String price){
		if(adGoods.isEmpty()) return null;
		List<AdGood> sameCpcPriceAd = new ArrayList<AdGood>(adGoods.size());
		//标准cpc价格
		String scp = adGoods.get(0).getCpcPrice();
		for(AdGood adGood : adGoods){
			if(scp.equals(adGood.getCpcPrice())){
				sameCpcPriceAd.add(adGood);
			}
		}
		int size = sameCpcPriceAd.size();
		//如果匹配标准价格的只有一个 则直接返回
		if(!StringTools.isEmpty(price)){//如果客户端传递过来的价格不为空, 则取出第一个比这个价格低的广告
			try {//防止异常导致中断
				for(AdGood adGood : sameCpcPriceAd){
					BigDecimal p1 = new BigDecimal(price);
					BigDecimal p2 = new BigDecimal(adGood.getPrice());
					if(p1.compareTo(p2) > 0){
						return adGood;
					}
				}
			} catch (Exception e) {
			}
		}
		if(size == 1) return sameCpcPriceAd.get(0);
		//随即获取一个
		return sameCpcPriceAd.get(new Random().nextInt(size));
	}
	
	protected JSONArray queryDataFromSf1(Integer limit, Integer offset, String keywords, String collection, String category, Boolean isDetail, String price){
		return queryDataFromSf1("", limit, offset, keywords, collection, category, isDetail, price);
	}
	
	protected JSONArray queryDataFromSf1(String source, Integer limit, Integer offset, String keywords, String collection, String category, Boolean isDetail, String price){
		SF1SearchBean searchBean = new SF1SearchBean();
		searchBean.setLimit(limit);
		searchBean.setKeywords(keywords);
		searchBean.setOffset(offset);
		searchBean.setCollection(collection);
		searchBean.setCategory(category);
		searchBean.setSources(source);
		dealWithPriceCondition(searchBean, price);
		//按销量排序
		searchBean.addSort("SalesAmount", "DESC");
		if(isDetail != null && isDetail){
			searchBean.setSearchMode(SearchMode.SUFFIX);
		}
		JSONArray jsonArray = queryDataFromSf1(searchBean);
		if(jsonArray == null && isDetail != null && isDetail){
			searchBean.setSearchMode(SearchMode.ZAMBEZI);
			jsonArray = queryDataFromSf1(searchBean);
		}
		return jsonArray;
	}
	
	protected void dealWithPriceCondition(SF1SearchBean searchBean, String price){
		if(StringUtils.isEmpty(price)) return;
		try {
			BigDecimal _price = new BigDecimal(price);
			BigDecimal remain = _price.multiply(new BigDecimal("0.3")).setScale(0, RoundingMode.UP);
			searchBean.addCondition("Price", "between", new Object[]{_price.subtract(remain).setScale(0, RoundingMode.UP), _price.add(remain).setScale(0, RoundingMode.UP)});
		} catch (Exception e) {
			return;
		}
	}
	
	@Override
	public JSONArray dealWithData(JSONArray jsonArray, String keywords, String uid, String cid, String position, HttpServletRequest req, Integer limit, String price) throws Exception {
		dealWithUrl(jsonArray, uid, cid, position, price, req);
		return jsonArray;
	}
	
	@Override
	public JSONArray dealWithData(JSONArray jsonArray, String keywords, String uid, String cid, String position, HttpServletRequest req, Integer limit) throws Exception {
		dealWithUrl(jsonArray, uid, cid, position, "", req);
		return jsonArray;
	}
	
	//获取docid集合
	protected Set<String> getDocids(JSONArray jsonArray){
		Set<String> docIds = new HashSet<String>();
		if(jsonArray == null || jsonArray.size() < 1) return docIds;
		int length = jsonArray.size();
		for(int i = 0; i < length; i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			docIds.add(jsonObject.getString("DOCID"));
		}
		return docIds;
	}
	
	protected void dealWithUrl(JSONArray jsonArray, String uid, String cid, String position, String clprice, HttpServletRequest req) throws Exception {
		if (jsonArray == null) return;
		int length = jsonArray.size();
		StringBuilder common = new StringBuilder(500);
		common.append(getServerPath(req)).append("jump.html?uid=").append(uid).append("&cid=").append(cid);
		String referer = WebUtils.referer(req);
		String ip = WebTools.getIpAddr(req);
		Cookie[] cookies = req.getCookies();
		String b5tid = "";
		if(cookies != null){
			b5tid = WebTools.getCooKieValue("b5tuid", cookies);
		}
		common.append("&rp=").append(getRp(referer));
		if (!StringUtils.isEmpty(referer)) {
			common.append("&curl=").append(URLEncoder.encode(referer, CHAR_SET));
		}
//		Map<String, Long> map = adDataService.queryIds();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("cid", cid);
		AVLTree docIdRelsAvlTree = CacheAop.getLocalCache().getConstant("docIdRelsAvlTree", AVLTree.class);
		
		for (int index = 0; index < length; index++) {
			JSONObject jsonObject = jsonArray.getJSONObject(index);
			if(!StringTools.isEmpty(clprice)){
				BigDecimal _oprice = jsonObject.getBigDecimal("Price");
				BigDecimal _clprice = new BigDecimal(clprice);
				if(_clprice.compareTo(_oprice) < 0){
					jsonObject.put("isAd", true);
				}
			}
			//针对搜索引擎这边调整做了一点调整
			String title = jsonObject.getString("Title");
			int _index = title.indexOf("_");
			if(_index > 0){
				String isNotSf1 = jsonObject.getString("isNotSf1");
				if(StringTools.isEmpty(isNotSf1)){
					title = title.substring(0, _index);
					jsonObject.put("Title", title);
				}
			}
			String url = jsonObject.getString("Url");
			Object price = jsonObject.get("CpcPrice");
			jsonObject.put("CpcPrice", "");
			String dd = jsonObject.getString("DOCID");//
//			String aid = adId(dd);
			String aid = jsonObject.getString("ID");
			boolean isRecored = true;
			if(StringTools.isEmpty(aid)) {
				aid = adId(dd);
			}
			if(StringTools.isEmpty(aid) && docIdRelsAvlTree != null) {
				DocIdRel docIdRel = (DocIdRel) docIdRelsAvlTree.getItem(dd.hashCode());
				aid = dd;
				if(docIdRel != null){
					aid = docIdRel.getId();
				}else{
					isRecored = false;
				}
				/*Long id = map.get(aid);
				aid = dd;
				if(id != null){
					aid = String.valueOf(id);
				}else{
					isRecored = false;
				}*/
			}
			String key = jsonObject.getString("Key");	
			String kid = jsonObject.getString("KID");
			//该逻辑表示，虽然没有通过
			if(StringTools.isEmpty(kid) && isRecored){
				kid = "-1";
				key = "其他";
			}
			jsonObject.put("aid", aid);
			String source = jsonObject.getString("Source");
			//日志 记录关键词
			recordAd(source, key, kid, aid, "0");
			recordToSf1(ip, b5tid, dd, url, "108", position + (index + 1), "0.5");
			
			if("蘑菇街".equals(source)){
				url = properties.getProperty("mogujie.cpc.jumpurl") + url;
			}else if("趣天麦网".equals(source)){
				price = null;
				url = m18Url(url, req);
				source = "m18";
			}
			if (price == null || new BigDecimal(price.toString()) == null || new BigDecimal(price.toString()).compareTo(BigDecimal.ZERO) <= 0) {
				url = "http://" + properties.getProperty("b5mserver") + "/cpc/item/" + dd + ".html";
			}
			if(url.indexOf("yooli.com") > 0){
				jsonObject.put("Price", jsonObject.getString("Price") + "元起投");
			}
			if(StringTools.isEmpty(url)){
				continue;
			}
			String durl = URLEncoder.encode(url, CHAR_SET);
			jsonObject.put("durl", durl);
			jsonObject.put("Url", getUrl(common.toString(), source, aid, jsonObject.getString("AdOwerID"), dd, durl, position, index, key, kid));
		}
		//记录 pv click
		//LogUtils.infoPV(req, params, position, jsonArray);
	}
	
	private Random random = new Random();
	
	public String m18Url(String url, HttpServletRequest req){
		Cookie[] cookies = req.getCookies();
		String uid = WebTools.getCooKieValue("b5tuid", cookies);
		if(StringUtils.isEmpty(uid)){
			uid = WebTools.getCooKieValue("cookieId", cookies);
		}
		boolean isATest = true;
//		if(!StringUtils.isEmpty(uid)){
//			if(uid.hashCode() % 2 == 0){
//				isATest = true;
//			}
//		}else{
//			if(random.nextInt(2) % 2 == 0){
//				isATest = true;
//			}
//		}
		
//		http://list.m18.com/DynamicAD/55/?jaehuid=2025523722&fit=417864943&fillfit=best
		
//		http://list.m18.com/dynamicad/54?gd_no=" + shopId + "&jaehuid=2025525379
		
//		if(url.indexOf("list.m18.com/DynamicAD/55/?jaehuid=") > 0 && isATest){
//			System.out.println(url);
//			int fitIndex = url.indexOf("fit=");
//			int lastFitIndex = url.indexOf("&", fitIndex);
//			if(lastFitIndex < 0) lastFitIndex = url.length();
//			String shopId = url.substring(fitIndex + "fit=".length(), lastFitIndex);
//			url = "http://list.m18.com/dynamicad/54?gd_no=" + shopId + "&jaehuid=2025525379"; 
//		}
		if (url.indexOf("list.m18.com/dynamicad/54") > 0 && isATest){
			System.out.println(url);
			int fitIndex = url.indexOf("gd_no=");
			int lastFitIndex = url.indexOf("&", fitIndex);
			if(lastFitIndex < 0) lastFitIndex = url.length();
			String shopId = url.substring(fitIndex + "gd_no=".length(), lastFitIndex);
			url = "http://list.m18.com/DynamicAD/55/?jaehuid=2025523722&fit=" + shopId + "&fillfit=best"; 
		}
		return url;
	}
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 *
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年4月22日 上午10:16:49
	 *
	 * @param key
	 * @param keyid
	 * @param aid
	 * @param type 点击或者pv
	 * @throws InterruptedException 
	 * @throws MetaClientException 
	 */
	public void recordAd(final String source, final String key, final String keyid, final String aid, final String type){
		String excludeShopsStr = properties.getProperty("exclude.record.shop");
		if(StringTools.isEmpty(keyid)) return;
		if(!StringTools.isEmpty(excludeShopsStr)){
			String[] excludeShops = StringTools.split(excludeShopsStr, ",");
			for(String excludeShop : excludeShops){
				if(excludeShop.equals(source)){
					return;
				}
			}
		}
		threadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				Calendar c1 = Calendar.getInstance();
				c1.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), c1.get(Calendar.HOUR_OF_DAY), 0, 0);
				c1.set(Calendar.MILLISECOND, 0);
				long timeHour = c1.getTime().getTime();
				
				c1.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), 0, 0, 0);
				long timeDate = c1.getTime().getTime();
				
				StringBuilder sb = new StringBuilder();
				sb.append(aid).append(",");
				sb.append(key).append(",").append(keyid).append(",");
				sb.append(timeHour).append(",").append(timeDate).append(",").append(type);
				try {
					messageProducer.sendMessage(new Message("search-click-pv-data", sb.toString().getBytes()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 记录日志到metaq中 供sf1这边使用
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年6月10日 上午10:14:37
	 *
	 * @param req
	 * @param uid
	 * @param aid
	 * @param dstl
	 * @param type
	 * @param position
	 * @param cost
	 */
	public void recordToSf1(final String ip, final String uid, final String docId, final String dstl, final String type, final String position, final String cost){
		threadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("timestamp", new Date().getTime());
				jsonObject.put("remoteAddress", ip);
				JSONObject args = new JSONObject();
				jsonObject.put("args", args);
				args.put("uid", uid);
				args.put("aid", docId);
				args.put("ad", type);
				args.put("dstl", dstl);
				args.put("click_cost", cost);
				args.put("click_slot", position);
				try {
					messageProducer.sendMessage(new Message("log_sponsored_ad_sf1", jsonObject.toString().getBytes()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public String getRp(String referer){
		if(StringTools.isEmpty(referer) || referer.indexOf("b5m.com") > 0) return "1002";
		return "1001";
	}
	
	public String getUrl(String common, String source, String aid, String adOwerID, String dd, String durl, String position, int index, String key, String kid){
		//如果dr不存在 则用source
		return new StringBuilder(1000).append(common.toString()).append("&dr=").append(source).append("&aid=").append(aid)
				.append("&key=").append(key).append("&kid=").append(kid).append("&source=").append(source)
				.append("&").append("dd=").append(dd).append("&durl=")
				.append(durl).append("&da=").append(position).append(index + 1).append("&adOwerID=").append(adOwerID).toString();
	}
	
	protected JSONObject convertTo(AdGood adGood){
		JSONObject jsonObject = new JSONObject();
		if(StringTools.isEmpty(adGood.getUrl())) return null;
		if(StringTools.isEmpty(adGood.getTitle())) return null;
		if(StringTools.isEmpty(adGood.getDocId())) return null;
		if(StringTools.isEmpty(adGood.getPrice())) return null;
		if("-1".equals(adGood.getFlag())){
			return null;
		}
		jsonObject.put("Url", adGood.getUrl());
		jsonObject.put("Title", adGood.getTitle());
		jsonObject.put("DOCID", adGood.getDocId());
		jsonObject.put("Price", adGood.getPrice());
		jsonObject.put("Picture", adGood.getPicture());
		jsonObject.put("CpcPrice", adGood.getCpcPrice());
		jsonObject.put("Source", adGood.getSourceShop());
		jsonObject.put("ID", adGood.getId());
		jsonObject.put("AdOwerID", adGood.getAdId());
		jsonObject.put("Key", adGood.getKeywords());
		jsonObject.put("KID", adGood.getKeywordsId());
		jsonObject.put("isNotSf1", "true");
		jsonObject.put("isAd", true);
		return jsonObject;
	}
	
	protected String adId(String docid) {
		try {
			Object adId = MemCachedUtils.getCache(Constants.MEM_PREFIX_AD + docid);
			if (!Lang.isNull(adId))
				return adId.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	protected String getServerPath(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		int port = req.getServerPort();
		sb.append(req.getScheme()).append("://").append(req.getServerName())
				.append(port == 80 ? "" : (":" + port))
				.append(req.getContextPath()).append("/");
		return sb.toString();
	}
}
