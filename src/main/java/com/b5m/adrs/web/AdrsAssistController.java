package com.b5m.adrs.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.analysis.AVLTree;
import com.b5m.adrs.analysis.FenciHelper;
import com.b5m.adrs.analysis.Item;
import com.b5m.adrs.analysis.SearchKeywords;
import com.b5m.adrs.cache.CacheAop;
import com.b5m.adrs.cache.MemCachedUtils;
import com.b5m.adrs.common.CallBack;
import com.b5m.adrs.domain.Msg;
import com.b5m.adrs.entity.AdGood;
import com.b5m.adrs.service.AdDataService;
import com.b5m.adrs.service.Sf1DataQueryService;
import com.b5m.adrs.web.base.AbstractBaseController;
import com.b5m.base.common.utils.StringTools;
import com.b5m.base.common.utils.WebTools;

@Controller
public class AdrsAssistController extends AbstractBaseController{
	
	@Autowired
	private AdDataService adDataService;
	
	@Autowired
	private Sf1DataQueryService sf1DataQueryService;
	
	@Autowired
	@Qualifier("properties")
	private Properties properties;
	
	@RequestMapping("/ad/mogujie/change")
	@ResponseBody
	public Object changeMogujieShop(){
		adDataService.runMogujieShop();
		return "success";
	}
	
	@RequestMapping("/ad/clear/keywords")
	@ResponseBody
	public Msg clearAllKeyWordsCache(){
		CacheAop.clearCache("all_search_keywords");
		return Msg.newSuccInstance("clear successed");
	}
	
	@RequestMapping("/ad/clear/ids")
	@ResponseBody
	public Msg clearAllIdsCache(){
		CacheAop.clearCache("all_search_ids");
		CacheAop.clearCache("all_search_adgoods");
		return Msg.newSuccInstance("clear successed");
	}
	
	@RequestMapping("/ad/modify/keywords")
	@ResponseBody
	public Msg modifyKeyWordsCache(String keywords, Long id, Boolean isDel){
		AVLTree avlTree = CacheAop.getLocalCache().getConstant("avlTree", AVLTree.class);
		if(id == null){
			return Msg.newFailedInstance("operation failed for id is null");
		}
		if(!isDel){
			SearchKeywords e = adDataService.querySearchKeywordsById(id);
			if(e == null){
				return Msg.newFailedInstance("operation failed for object is not in db for id[" + id + "]");
			}
			if(e != null){
				FenciHelper.remove(e, avlTree);
				FenciHelper.addToTree(e, avlTree);
			}
		}else{
			SearchKeywords e = adDataService.querySearchKeywordsById(id);
			if((e == null && StringUtils.isEmpty(keywords)) || id == null){
				return Msg.newFailedInstance("operation failed for keywords or id is null");
			}
			keywords = e.getKeywords();
			FenciHelper.remove(id, keywords, 0, 0, avlTree);
		}
		return Msg.newSuccInstance("modify successed");
	}
	
	@RequestMapping("/ad/modify/adGoods")
	@ResponseBody
	public Msg modifyAdGoodsCache(Long id, Boolean isDel){
		if(id == null) return Msg.newFailedInstance("no data modify");
		AVLTree adGoodsAvlTree = CacheAop.getLocalCache().getConstant("adGoodsAvlTree", AVLTree.class);
		if(!isDel){
			AdGood adGood = adDataService.queryAdGood(id);
			if(adGood == null){
				return Msg.newFailedInstance("modify is failed for id is no found for DB");
			}
			Item item = adGoodsAvlTree.getItem(id.intValue());
			if(item != null){
				adGoodsAvlTree.remove(item);
			}
			adGoodsAvlTree.add(adGood);
			return Msg.newSuccInstance("adgoods not exists");
		}else{
			Item item = adGoodsAvlTree.getItem(id.intValue());
			if(item != null){
				adGoodsAvlTree.remove(item);
			}
		}
		return Msg.newSuccInstance("modify successed");
	}
	
	@RequestMapping("/ad/modify/allKeywords")
	@ResponseBody
	public Msg modifyAllKeywords(final Long id, final Boolean isDel){
		execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/ad/modify/keywords.html?id=" + id + "&isDel=" + isDel);
				return null;
			}
			
		});
		return Msg.newSuccInstance("clear successed");
	}
	
	@RequestMapping("/ad/modify/_allKeywords")
	@ResponseBody
	public Msg _modifyAllKeywords(final Long id, final Boolean isDel){
		String msg = _execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/ad/modify/keywords.html?id=" + id + "&isDel=" + isDel);
				return null;
			}
			
		});
		return Msg.newSuccInstance(msg);
	}
	
	@RequestMapping("/ad/modify/allAdGoods")
	@ResponseBody
	public Msg modifyAllAdGoods(final Long id, final Boolean isDel){
		execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/ad/modify/adGoods.html?id=" + id + "&isDel=" + isDel);
				return null;
			}
			
		});
		return Msg.newSuccInstance("modify successed");
	}
	
	@RequestMapping("/ad/modify/_allAdGoods")
	@ResponseBody
	public Msg _modifyAllAdGoods(final Long id, final Boolean isDel){
		String msg = _execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/ad/modify/adGoods.html?id=" + id + "&isDel=" + isDel);
				return null;
			}
			
		});
		return Msg.newSuccInstance(msg);
	}
	
	public void execute(final CallBack call){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String ips = properties.getProperty("ips");
				String[] arrays = StringTools.split(ips, ",");
				for(String ip : arrays){
					call.callback(ip, null);
				}
			}
		}).start();
	}
	
	public String _execute(CallBack call){
		StringBuilder sb = new StringBuilder();
		String ips = properties.getProperty("ips");
		String[] arrays = StringTools.split(ips, ",");
		for(String ip : arrays){
			sb.append("modify for server[" + ip + "] success");
			Object o = call.callback(ip, null);
			if(o != null){
				sb.append(call.callback(ip, null));
			}
			sb.append("\n\t");
		}
		return sb.toString();
	}
	
	@RequestMapping("/api/s/data/{limit}_{offset}_{position}")
	@ResponseBody
	public void sf1DataBack(@PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset, String uid, String cid, String keywords, String category,
			@PathVariable("position") String position, Boolean isDetail, String price, HttpServletRequest request, HttpServletResponse response){
		try {
			JSONArray jsonArray = sf1DataQueryService.queryData(limit, offset, keywords, category, isDetail, price, true);
			output(response, request, jsonArray);
		} catch (Exception e) {
			try {
				output(response, request, new JSONArray());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} ;
	}
	
	@RequestMapping("/api/s/rank")
	@ResponseBody
	public void sf1GoodsRank(String keywords, String price, HttpServletRequest request, HttpServletResponse response){
		JSONArray jsonArray = sf1DataQueryService.queryDataFromDB(20, keywords, price, true);
		int length = jsonArray.size();
		int rank = 1;
		try {
			for(int index = 0; index < length; index++){
				JSONObject jsonObject = jsonArray.getJSONObject(index);
				BigDecimal cpcPrice = jsonObject.getBigDecimal("CpcPrice");
				BigDecimal newPrice = new BigDecimal(price);
				if(cpcPrice.compareTo(newPrice) <= 0){
					break;
				}
				rank++;
			}
			output(response, request, rank);
		} catch (Exception e) {
			try {
				outputFailed(response, request, 0);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@RequestMapping("/cache/adgoods")
	public void cacheAdGoods(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<AdGood> adGoods = adDataService.queryAdGoodsList();
		for(AdGood adGood : adGoods){
			MemCachedUtils.setCache("good_" + adGood.getId(), adGood, MemCachedUtils.DEFAULT_CACHE_TIME);
		}
		output(response, request, "save cache success");
	}
	
	@RequestMapping("/data/indexAdGoods")
	@ResponseBody
	public void indexAdGoods(HttpServletRequest request, HttpServletResponse response) throws Exception{
		adDataService.indexAdGoods();
		output(response, request, "index AdGood success");
	}
	
	@RequestMapping("/ad/allIndexAdGoods")
	@ResponseBody
	public Msg indexAdGoods(){
		String msg = _execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/data/indexAdGoods.html");
				return null;
			}
			
		});
		return Msg.newSuccInstance(msg);
	}
	
	@RequestMapping("/data/indexKeywords")
	@ResponseBody
	public void indexKeywords(HttpServletRequest request, HttpServletResponse response) throws Exception{
		adDataService.indexKeywords();
		output(response, request, "index keywords success");
	}
	
	@RequestMapping("/ad/allIndexKeywords")
	@ResponseBody
	public Msg indexKeywords(){
		String msg = _execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/data/indexKeywords.html");
				return null;
			}
			
		});
		return Msg.newSuccInstance(msg);
	}
	
	@RequestMapping("/data/indexDocIdRel")
	@ResponseBody
	public void indexDocIdRel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		adDataService.indexDocIdRel();
		output(response, request, "index docIdRel success");
	}
	
	@RequestMapping("/ad/allIndexDocIdRel")
	@ResponseBody
	public Msg indexDocIdRel(){
		String msg = _execute(new CallBack() {

			@Override
			public <T> T callback(Object data, Class<T> rtnType) {
				WebTools.executeGetMethod("http://" + data + "/data/indexDocIdRel.html");
				return null;
			}
			
		});
		return Msg.newSuccInstance(msg);
	}
}
