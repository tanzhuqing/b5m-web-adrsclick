package com.b5m.adrs.web;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.cache.CacheAop;
import com.b5m.adrs.common.Constants;
import com.b5m.adrs.domain.SF1SearchBean;
import com.b5m.adrs.rule.AntiCheatRuleService;
import com.b5m.adrs.service.Sf1DataQueryService;
import com.b5m.adrs.utils.LogUtils;
import com.b5m.adrs.utils.WebUtils;
import com.b5m.adrs.web.base.AbstractBaseController;
import com.b5m.base.common.Lang;
import com.b5m.base.common.utils.DateTools;
import com.b5m.base.common.utils.WebTools;

@Controller
public class AdrsPageController extends AbstractBaseController {

	@Autowired
	private Sf1DataQueryService sf1DataQueryService;
	@Autowired
	private AntiCheatRuleService antiCheatRuleService;
	
	private static final String CHAR_SET = "UTF-8";
	
	@Resource(name = "properties")
	private Properties properties;
	
	@RequestMapping("/s/data/sf")
	@ResponseBody
	public JSONObject sf(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", Constants.SUCCESS);
		jsonObject.put("failed", Constants.FAILED);
		return jsonObject;
	}
	
	@RequestMapping("/s/data/{limit}_{offset}_{position}")
	@ResponseBody
	public void sf1DataBack(@PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset, String uid, String cid, String keywords, String category,
			@PathVariable("position") String position, Boolean isDetail, String price, HttpServletRequest request, HttpServletResponse response){
		try {
			JSONArray jsonArray = sf1DataQueryService.queryData(limit, offset, keywords, "", isDetail, price);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, position, request, limit, price);
			output(response, request, jsonArray);
		} catch (Exception e) {
			try {
				output(response, request, new JSONArray());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@RequestMapping("/s/tanx/{limit}_{uid}")
	@ResponseBody
	public void tanxAdData(@PathVariable("limit") Integer limit, @PathVariable("uid") String uid, HttpServletRequest request, HttpServletResponse response){
		JSONArray jsonArray = sf1DataQueryService.queryTanxData(limit, uid);
		try {
			output(response, request, jsonArray);
		} catch (Exception e) {
			try {
				output(response, request, new JSONArray());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	// 垂直
	@RequestMapping("/ad/vlt_{limit}_{offset}")
	public String vt10(@PathVariable("offset") Integer offset,
			@PathVariable("limit") Integer limit, String keywords, String uid,
			String cid, Integer width, HttpServletRequest request) throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			if (limit > 10)
				limit = 10;
			JSONArray jsonArray = sf1DataQueryService.queryData(limit, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "V", request, limit);
			request.setAttribute("resList", jsonArray);
			request.setAttribute("width", width);
		}
		setAttr(uid, cid, request);
		return "/ad/v_10";
	}

	// 横向
	@RequestMapping("/ad/hlt_{limit}_{offset}")
	public String ht4(@PathVariable("offset") Integer offset,
			@PathVariable("limit") Integer limit, String keywords, String uid,
			String cid, Integer width, HttpServletRequest request) throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			if (limit > 6)
				limit = 6;
			JSONArray jsonArray = sf1DataQueryService.queryData(limit, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "H", request, limit);
			request.setAttribute("resList", jsonArray);
			request.setAttribute("width", width);
		}
		setAttr(uid, cid, request);
		return "/ad/h_6";
	}

	// 垂直
	@RequestMapping("/ad/vt_{offset}")
	public String _vt10(@PathVariable("offset") Integer offset,
			String keywords, String uid, String cid, Integer width,
			HttpServletRequest request) throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			JSONArray jsonArray = sf1DataQueryService.queryData(10, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "V", request, 10);
			request.setAttribute("resList", jsonArray);
			request.setAttribute("width", width);
		}
		setAttr(uid, cid, request);
		return "/ad/v_10";
	}

	// 横向
	@RequestMapping("/ad/ht_{offset}")
	public String _ht4(@PathVariable("offset") Integer offset, String keywords,
			String uid, String cid, Integer width, HttpServletRequest request)
			throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			JSONArray jsonArray = sf1DataQueryService.queryData(6, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "H", request, 6);
			request.setAttribute("resList", jsonArray);
			request.setAttribute("width", width);
		}
		setAttr(uid, cid, request);
		return "/ad/h_6";
	}

	// 横向
	@RequestMapping("/ad/hlb5m_{limit}_{offset}")
	public String b5mlh4(@PathVariable("offset") Integer offset, @PathVariable("limit") Integer limit, String keywords, String uid, String cid, HttpServletRequest request) throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			JSONArray jsonArray = sf1DataQueryService.queryData(limit, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "H", request, limit);
			request.setAttribute("resList", jsonArray);
		}
		setAttr(uid, cid, request);
		return "/ad/hb5m_4";
	}

	// 垂直
	@RequestMapping("/ad/vb5m_{offset}")
	public String b5mv10(@PathVariable("offset") Integer offset,
			String keywords, Integer width, String uid, String cid,
			HttpServletRequest request) throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			JSONArray jsonArray = sf1DataQueryService.queryData(10, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "V", request, 10);
			request.setAttribute("resList", jsonArray);
			request.setAttribute("width", width);
		}
		setAttr(uid, cid, request);
		return "/ad/vb5m_10";
	}

	//垂直
	@RequestMapping("/ad/vlb5m_{limit}_{offset}")
	public String b5mv10(@PathVariable("offset") Integer offset, @PathVariable("limit") Integer limit, String keywords, String domain, String pathSuffix, 
			Integer width, String uid, String cid, HttpServletRequest request) throws Exception{
		if(!StringUtils.isEmpty(keywords)){
			JSONArray jsonArray =  sf1DataQueryService.queryData(limit, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "V", request, limit);
			request.setAttribute("resList", jsonArray);
			request.setAttribute("width", width);
			if(Lang.isEmpty(domain)){
				domain = "s";
			}
			request.setAttribute("domain", domain);
			if(Lang.isEmpty(pathSuffix)){
				pathSuffix = "jsp";
			}
			request.setAttribute("pathSuffix", pathSuffix);
		}
		setAttr(uid, cid, request);
		return "/ad/vb5m_10";
	}
	
	@RequestMapping("/ad/hb5m_{offset}")
	public String b5mh4(@PathVariable("offset") Integer offset,
			String keywords, String uid, String cid, HttpServletRequest request)
			throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			JSONArray jsonArray = sf1DataQueryService.queryData(4, offset, keywords);
			jsonArray = sf1DataQueryService.dealWithData(jsonArray, keywords, uid, cid, "H", request, 4);
			request.setAttribute("resList", jsonArray);
		}
		setAttr(uid, cid, request);
		return "/ad/hb5m_4";
	}
	
	@RequestMapping("/haiwai/data/{limit}_{offset}")
	@ResponseBody
	public void hhaiwaiDataBack(@PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset, String keywords, HttpServletRequest req, HttpServletResponse res) throws Exception {
		JSONArray jsonArray = null;
		if (!StringUtils.isEmpty(keywords)) {
			SF1SearchBean searchBean = new SF1SearchBean();
			searchBean.setLimit(limit);
			searchBean.setKeywords(keywords);
			searchBean.setOffset(offset);
			searchBean.setCollection("haiwaip");
			//不需要比较的商品
			searchBean.addCondition("itemcount", "<=", String.valueOf(1));
			jsonArray = sf1DataQueryService.queryDataFromSf1(searchBean);
		}else{
			jsonArray = new JSONArray();
		}
		output(res, req, jsonArray);
	}
	
	//海外数据横向
	@RequestMapping("/ad/hhaiwai_{limit}_{offset}")
	public String hhaiwai(@PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset, String keywords,
			HttpServletRequest req) throws Exception {
		if (!StringUtils.isEmpty(keywords)) {
			SF1SearchBean searchBean = new SF1SearchBean();
			searchBean.setLimit(limit);
			searchBean.setKeywords(keywords);
			searchBean.setOffset(offset);
			searchBean.setCollection("haiwaip");
			//不需要比较的商品
			searchBean.addCondition("itemcount", "<=", String.valueOf(1));
			JSONArray jsonArray = sf1DataQueryService.queryDataFromSf1(searchBean);
			req.setAttribute("resList", jsonArray);
		}
		return "/ad/hhaiwai_4";
	}
	
	/*@RequestMapping("/jump")
	public void jumpAndRecord(String uid, String cid, String aid, String da, String curl, String durl, String dd, String dr, String rp, String source, String key, String kid, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		if (durl.indexOf("b5m") < 0) {
			if (durl.indexOf("?") > 0) {
				durl = durl + "&tjh=true";
			} else {
				durl = durl + "?tjh=true";	
			}
		}
		//click记录
		Map<String, String> params = new HashMap<String, String>();
		params.put("lt", "8800");
		params.put("ad", "103");
		params.put("source", source);
		params.put("key", key);
		params.put("kid", kid);
		params.put("aid", aid);
		//进行规则过滤
		LogUtils.infoClick(antiCheatRuleService, sf1DataQueryService, req, params, dd);
		res.sendRedirect(durl);
	}*/
	
	@RequestMapping("/jump")
	public String jumpAndRecord(String uid, String cid, String aid, String da, String curl, String durl, String dd, String dr, String rp, String source, String key, String kid, HttpServletRequest req)
			throws Exception {
		if (durl.indexOf("b5m") < 0) {
			if (durl.indexOf("?") > 0) {
				durl = durl + "&tjh=true";
			} else {
				durl = durl + "?tjh=true";	
			}
		}
		//click记录
		Map<String, String> params = new HashMap<String, String>();
		params.put("lt", "8800");
		params.put("ad", "103");
		params.put("source", source);
		params.put("key", key);
		params.put("kid", kid);
		params.put("aid", aid);
		//进行规则过滤
		LogUtils.infoClick(antiCheatRuleService, sf1DataQueryService, req, params, dd);
		req.setAttribute("_durl", durl);
		return "redirect";
	}
	
	/*@RequestMapping("/jump")
	public String jumpAndRecord(String uid, String cid, String aid, String da, String curl, String durl, String dd, String dr, String rp, String source, String key, String kid, HttpServletRequest req)
			throws Exception {
		setAttr(uid, cid, req);
		req.setAttribute("aid", aid);
		req.setAttribute("da", da);
		req.setAttribute("dd", dd);
		req.setAttribute("dr", dr);
		req.setAttribute("rp", rp);
		req.setAttribute("t", new Date().getTime());
		boolean isDirectOut = durl.indexOf("s.b5m.com") < 0;
		if(isDirectOut){
			req.setAttribute("ip", WebTools.getIpAddr(req));
			req.setAttribute("chargeIntimeUrl", properties.getProperty("charge.intime.url"));
		}
		req.setAttribute("isDirectOut", isDirectOut);
		if (!StringUtils.isEmpty(curl)) {
			req.setAttribute("dr", URLDecoder.decode(curl, CHAR_SET));
		}
		if (durl.indexOf("b5m") < 0) {
			if (durl.indexOf("?") > 0) {
				durl = durl + "&tjh=true";
			} else {
				durl = durl + "?tjh=true";	
			}
		}
		//click记录
		Map<String, String> params = new HashMap<String, String>();
		params.put("lt", "8800");
		params.put("ad", "103");
		params.put("source", source);
		params.put("key", key);
		params.put("kid", kid);
		params.put("aid", aid);
		
		//进行规则过滤
		LogUtils.infoClick(antiCheatRuleService, sf1DataQueryService, req, params, dd);
		
		req.setAttribute("cpcRecordUrl", properties.get("cpc.recored.url"));
		req.setAttribute("durl", URLEncoder.encode(durl, CHAR_SET));
		req.setAttribute("_durl", URLDecoder.decode(durl, CHAR_SET));
		req.setAttribute("curl", URLEncoder.encode(jumpUrl(uid, cid, aid, da, curl, durl, dd, dr, req), CHAR_SET));
		return "redirect";
	}*/
	
	public String bitid(HttpServletRequest req){
		Cookie[] cookies = req.getCookies();
		String tid = WebTools.getCooKieValue("tid", cookies);
		if(StringUtils.isEmpty(tid)){
			tid = req.getParameter("tid");
		}
		return tid;
	}
	
	public String uid(HttpServletRequest req){
		Cookie[] cookies = req.getCookies();
		String tid = WebTools.getCooKieValue("token", cookies);
		if(StringUtils.isEmpty(tid)){
			tid = req.getParameter("uid");
		}
		return tid;
	}
	
	public String jumpUrl(String uid, String cid, String aid, String da, String curl, String durl, String dd, String dr, HttpServletRequest req){
		return new StringBuilder(500).append(getServerPath(req)).append("jump.html").append("?uid=").append(uid).append("&cid=")
		.append(cid).append("&aid=").append(aid).append("&da=")
		.append(da).append("&curl=").append(curl).append("&durl=").append(durl).append("&dd=").append(dd).toString();
	}

	protected void setAttr(String uid, String cid, HttpServletRequest req) throws Exception {
		String ip = WebTools.getIpAddr(req);
		req.setAttribute("ip", ip);
		req.setAttribute("uid", uid);
		req.setAttribute("cid", cid);
		req.setAttribute("t", new Date().getTime());
		req.setAttribute("cpcRecordUrl", properties.get("cpc.recored.url"));
		String referer = WebUtils.referer(req);
		if (referer != null) {
			req.setAttribute("refere", URLEncoder.encode(referer, CHAR_SET));
		}
		req.setAttribute("servertime", new Date().getTime());
	}

	protected String getServerPath(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		int port = req.getServerPort();
		sb.append(req.getScheme()).append("://").append(req.getServerName())
				.append(":").append(port == 80 ? "" : port)
				.append(req.getContextPath()).append("/");
		return sb.toString();
	}
	
	@RequestMapping("/ad/clearCache")
	@ResponseBody
	public Object clearLocalCache(String key){
		Object o = CacheAop.getLocalCache().get(key);
		CacheAop.getLocalCache().remove(key);
		return o;
	}
	
	public static void main(String[] args) {
		System.out.println(DateTools.formate(new Date(1401724800000l), "yyyy-MM-dd HH:mm:ss"));
	}
}
