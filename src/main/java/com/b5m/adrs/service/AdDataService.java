package com.b5m.adrs.service;

import java.util.List;

import com.b5m.adrs.analysis.SearchKeywords;
import com.b5m.adrs.entity.AdGood;

/**
 * @author echo
 */
/**
 * @author echo
 *
 */
/**
 * @author echo
 *
 */
/**
 * @author echo
 *
 */
/**
 * @author echo
 *
 */
public interface AdDataService {
	/**
	 * description
	 * 查询所有指定搜索关键词
	 * @return
	 * @author echo weng
	 * @since 2014年2月11日
	 * @mail echo.weng@b5m.com
	 */
	List<SearchKeywords> queryAllSearchKeywords();
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 没有缓存查询
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年4月28日 下午1:39:08
	 *
	 * @return
	 */
	List<SearchKeywords> queryAllSearchKeywordsNoCache();
	
	/**
	 * description
	 * 通过指定key查询商品
	 * @param searchKeywordsList
	 * @return
	 * @author echo weng
	 * @since 2014年2月11日
	 * @mail echo.weng@b5m.com
	 */
	List<AdGood> queryAdGoodByIndexKey(String keywords, Integer limit);
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 蘑菇街数据变更
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年3月18日 上午10:27:04
	 *
	 */
	void runMogujieShop();
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 广告列表
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月26日 上午10:36:52
	 *
	 * @return
	 */
	List<AdGood> queryAdGoodsList();
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 查询关键词
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月14日 下午5:50:34
	 *
	 * @param id
	 * @return
	 */
	SearchKeywords querySearchKeywordsById(Long id);
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 查询所有广告
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年4月11日 下午4:56:15
	 *
	 * @return
	 */
//	Map<Long, AdGood> queryAllAdGoods();
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 查询单个商品
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年4月24日 上午11:19:16
	 *
	 * @param id
	 * @return
	 */
	AdGood queryAdGood(Long id);
	
	//索引
	void indexAdGoods();
	
	void indexKeywords();
	
	void indexDocIdRel();
	
}
