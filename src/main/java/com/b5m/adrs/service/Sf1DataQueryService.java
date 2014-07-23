package com.b5m.adrs.service;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.b5m.adrs.domain.SF1SearchBean;
import com.taobao.metamorphosis.exception.MetaClientException;

public interface Sf1DataQueryService {
	
	JSONArray queryDataFromSf1(SF1SearchBean searchBean);
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 查询cpc的数据 
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年3月17日 上午10:59:37
	 *
	 * @param limit
	 * @param offset
	 * @param keywords
	 * @param category
	 * @param collection
	 * @return
	 */
	JSONArray queryData(Integer limit, Integer offset, String keywords, String category, Boolean isDetail, String price);
	
	JSONArray queryData(Integer limit, Integer offset, String keywords, String category, Boolean isDetail, String price, boolean isNoCache);
	
	JSONArray queryData(Integer limit, Integer offset, String keywords);
	
	JSONArray queryData(Integer limit, Integer offset, String keywords, String category);
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 处理数据
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年3月17日 上午11:34:07
	 *
	 * @param jsonArray
	 * @param keywords
	 * @param uid
	 * @param cid
	 * @param position
	 * @param req
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	JSONArray dealWithData(JSONArray jsonArray, String keywords, String uid, String cid, String position, HttpServletRequest req, Integer limit, String price) throws Exception;
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 方法重载
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年4月23日 下午3:49:30
	 *
	 * @param jsonArray
	 * @param keywords
	 * @param uid
	 * @param cid
	 * @param position
	 * @param req
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	JSONArray dealWithData(JSONArray jsonArray, String keywords, String uid, String cid, String position, HttpServletRequest req, Integer limit) throws Exception;
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 日志记录
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年4月22日 下午3:47:26
	 *
	 * @param source
	 * @param key
	 * @param keyid
	 * @param aid
	 * @param type
	 * @throws MetaClientException
	 * @throws InterruptedException
	 */
	void recordAd(String source, String key, String keyid, String aid, String type) throws MetaClientException, InterruptedException;
	
	void recordToSf1(String ip, String uid, String docId, String dstl, String type, String position, String cost);
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 数据库查询
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月7日 下午10:27:09
	 *
	 * @param limit
	 * @param keywords
	 * @param price
	 * @param isNoCache
	 * @return
	 */
	JSONArray queryDataFromDB(Integer limit, String keywords, String price, boolean isNoCache);
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 查询tanx数据
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月22日 上午10:46:33
	 *
	 * @param limit
	 * @param uid
	 * @return
	 */
	JSONArray queryTanxData(Integer limit, String uid);
}
