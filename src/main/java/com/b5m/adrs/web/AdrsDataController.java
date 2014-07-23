package com.b5m.adrs.web;

import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.b5m.adrs.domain.SF1SearchBean;
import com.b5m.adrs.service.Sf1DataQueryService;
import com.b5m.adrs.web.base.AbstractBaseController;

@Controller
public class AdrsDataController extends AbstractBaseController{
	@Resource(name = "properties")
	private Properties properties;
	
	@Autowired
	private Sf1DataQueryService sf1DataQueryService;
	
	public JSONArray sf1Data(Integer limit, Integer offset, String keywords, String collection, HttpServletRequest req){
		SF1SearchBean searchBean = new SF1SearchBean();
		searchBean.setLimit(limit);
		searchBean.setKeywords(keywords);
		searchBean.setOffset(offset);
		searchBean.setCollection(collection);
		JSONArray jsonArray = sf1DataQueryService.queryDataFromSf1(searchBean);
		if(jsonArray == null) jsonArray = new JSONArray();
		return jsonArray;
	}
	
	public JSONArray sf1Data(Integer limit, Integer offset, String keywords, HttpServletRequest req){
		return sf1Data(limit, offset, keywords, "cpc", req);
	}
	
	//垂直
	@RequestMapping("/ad/data/vlt_{limit}_{offset}")
	public void vt10(@PathVariable("offset") Integer offset, @PathVariable("limit") Integer limit, String keywords, String uid, String cid, Integer width, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			if(limit > 10) limit = 10;
			JSONArray jsonArray = sf1Data(limit, offset, keywords, req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	} 
	
	//横向
	@RequestMapping("/ad/data/hlt_{limit}_{offset}")
	public void ht4(@PathVariable("offset") Integer offset, @PathVariable("limit") Integer limit, String keywords, String uid, String cid, Integer width, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			if(limit > 6) limit = 6;
			JSONArray jsonArray =  sf1Data(limit, offset, keywords, req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	}
	
	//垂直
	@RequestMapping("/ad/data/vt_{offset}")
	public void _vt10(@PathVariable("offset") Integer offset, String keywords, String uid, String cid, Integer width, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			JSONArray jsonArray = sf1Data(10, offset, keywords, req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	} 
	
	//横向
	@RequestMapping("/ad/data/ht_{offset}")
	public void _ht4(@PathVariable("offset") Integer offset, String keywords, String uid, String cid, Integer width, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			JSONArray jsonArray =  sf1Data(4, offset, keywords, req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	}
	
	//垂直
	@RequestMapping("/ad/data/vb5m_{offset}")
	public void b5mv10(@PathVariable("offset") Integer offset, String keywords, String uid, String cid, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			JSONArray jsonArray =  sf1Data(10, offset, keywords, req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	}
	
	//横向
	@RequestMapping("/ad/data/hb5m_{offset}")
	public void b5mh4(@PathVariable("offset") Integer offset, String keywords, String uid, String cid, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			JSONArray jsonArray =  sf1Data(4, offset, keywords, req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	}
	
	//海外数据横向
	@RequestMapping("/ad/data/hhaiwai_{limit}_{offset}")
	public void hhaiwai(@PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset, String keywords, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			JSONArray jsonArray = sf1Data(limit, offset, keywords, "haiwaip", req);
			output(resp, req, jsonArray.size());
		}else{
			output(resp, req, 0);
		}
	} 
	
}
