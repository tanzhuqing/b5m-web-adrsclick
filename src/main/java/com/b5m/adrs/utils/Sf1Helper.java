package com.b5m.adrs.utils;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.domain.CondSearchBean;
import com.b5m.adrs.domain.SF1SearchBean;
import com.b5m.adrs.domain.SearchMode;
import com.b5m.adrs.domain.SortSearchBean;
import com.b5m.base.common.Lang;
import com.b5m.base.common.utils.CollectionTools;
import com.b5m.base.common.utils.StringTools;
/**
 * @Company B5M.com
 * @description
 * 
 * @author echo
 * @since 2013-11-22
 * @email wuming@b5m.com
 */
public class Sf1Helper {
	
	public static String buildJson(SF1SearchBean bean){
		JSONObject jsonObject = new JSONObject();
		buildJsonLimit(jsonObject, bean);
		buildSearch(jsonObject, bean);
		buildJsonOfSort(jsonObject, bean);
		jsonObject.put("analyzer_result", true);
		jsonObject.put("remove_duplicated_result", false);
		jsonObject.put("collection", bean.getCollection());
		buildHeader(jsonObject, bean);
		return jsonObject.toJSONString();
	}
	
	protected static void buildJsonLimit(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		jsonObject.put("limit", sf1SearchBean.getLimit());
		jsonObject.put("offset", sf1SearchBean.getOffset());
	}
	
	protected static void buildJsonOfSort(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONArray sortJson = new JSONArray();
        List<SortSearchBean> sortLst = sf1SearchBean.getSortList();
        for (SortSearchBean sortSearchBean : sortLst) {
            JSONObject sortChildJson = new JSONObject();
            String softfield = sortSearchBean.getName();
            String sortType = sortSearchBean.getType();
            // 若排序字段为空，则不对该字段进行排序
            if (StringTools.isEmpty(softfield)) {
                continue;
            }
            sortChildJson.put("property", softfield);
            sortChildJson.put("order", sortType);
            sortJson.add(sortChildJson);
        }
        jsonObject.put("sort", sortJson);
	}
	
	protected static void buildSearch(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONObject jsonSearch = new JSONObject();
		JSONArray cnds = new JSONArray();
		buildGroup(jsonSearch, sf1SearchBean);
		buildCondition(jsonSearch, sf1SearchBean);
		jsonSearch.put("keywords", sf1SearchBean.getKeywords());
		/*if(StringTools.isEmpty(sf1SearchBean.getCategory())){
		}else{
			jsonSearch.put("keywords", "*");
			buildStartWith(cnds, sf1SearchBean);
		}*/
		jsonSearch.put("ranking_model", "bm25");
		jsonSearch.put("is_require_related", false);
		/*jsonSearch.put("query_abbreviation", 1);*/	
		buildAnalyzer(jsonSearch, sf1SearchBean);
		buildIn(jsonSearch, sf1SearchBean);
		buildMode(jsonSearch, sf1SearchBean);
		jsonObject.put("search", jsonSearch);
		jsonObject.put("conditions", cnds);
	}
	
	protected static void buildCondition(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONArray cnds = new JSONArray();
		List<CondSearchBean> condSearchBeans = sf1SearchBean.getCondLst();
		for(CondSearchBean condSearchBean : condSearchBeans){
			JSONObject cnd = new JSONObject();
			cnd.put("property", condSearchBean.getName());
			cnd.put("operator", condSearchBean.getOperator());
			JSONArray valueArray = new JSONArray();
			for(String value : condSearchBean.getParams()){
				valueArray.add(value);
			}
			cnd.put("value", valueArray);
			cnds.add(cnd);
		}
		jsonObject.put("conditions", cnds);
	}
	
	protected static void buildStartWith(JSONArray cnds, SF1SearchBean sf1SearchBean){
		JSONObject jsonObject = new JSONObject();
		JSONArray values = new JSONArray();
		values.add(sf1SearchBean.getCategory());
		jsonObject.put("value", values);
		jsonObject.put("property", "Category");
		jsonObject.put("operator", "starts_with");
		cnds.add(jsonObject);
	}
	
	protected static void buildGroup(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONArray jsonArray = new JSONArray();
		String category = sf1SearchBean.getCategory();
		if(!Lang.isEmpty(category)){
			JSONObject categoryJson = new JSONObject();
			String[] cs = StringTools.split(category, ">");
			JSONArray array = new JSONArray();
			array.addAll(Lang.newList(cs));
			categoryJson.put("value", array);
			categoryJson.put("property", "Category");
			jsonArray.add(categoryJson);
		}
		if(!StringTools.isEmpty(sf1SearchBean.getSources())){
			String[] sources = StringTools.split(sf1SearchBean.getSources(), ",");
			for(String source : sources){
				JSONObject sourceObject = new JSONObject();
				JSONArray sourceArray = new JSONArray();
				sourceArray.addAll(CollectionTools.newList(source));
				sourceObject.put("property", "Source");
				sourceObject.put("value", sourceArray);
				jsonArray.add(sourceObject);
			}
		}
		jsonObject.put("group_label", jsonArray);
	}
	
	protected static void buildAnalyzer(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONObject analyzer = new JSONObject(); 
		jsonObject.put("analyzer", analyzer);
		analyzer.put("apply_la", true);
		analyzer.put("use_original_keyword", false);
		analyzer.put("use_synonym_extension", true);
	}
	
	protected static void buildIn(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		/*JSONArray array = new JSONArray();
		JSONObject properties = new JSONObject();
		properties.put("property", "Title");
		array.add(properties);
		jsonObject.put("in", array);*/
	}
	
	protected static void buildMode(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONObject properties = new JSONObject();
		if(sf1SearchBean.getSearchMode() == null || SearchMode.ZAMBEZI.equals(sf1SearchBean.getSearchMode())){
			properties.put("mode", "zambezi");
			jsonObject.put("searching_mode", properties);
		}else if(SearchMode.SUFFIX.equals(sf1SearchBean.getSearchMode())){
			properties.put("mode", "suffix");
			properties.put("lucky", "1000");
			properties.put("use_fuzzy", "true");
			properties.put("use_fuzzyThreshold", "true");
			properties.put("fuzzy_threshold", "0.6");
			properties.put("tokens_threshold", "0.1");
		}
		jsonObject.put("searching_mode", properties);
	}
	
	protected static void buildHeader(JSONObject jsonObject, SF1SearchBean sf1SearchBean){
		JSONObject properties = new JSONObject();
		properties.put("check_time", "true");
		jsonObject.put("header", properties);
	}
	
	public static void main(String[] args) {
		SF1SearchBean sf1SearchBean = new SF1SearchBean();
		sf1SearchBean.setCollection("cpc");
		sf1SearchBean.setLimit(10);
		sf1SearchBean.setOffset(0);
		sf1SearchBean.setKeywords("iphone");
		System.out.println(buildJson(sf1SearchBean));
	}
	
}