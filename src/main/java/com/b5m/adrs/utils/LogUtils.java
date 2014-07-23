package com.b5m.adrs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.b5m.adrs.rule.AntiCheatRuleService;
import com.b5m.adrs.rule.RuleBean;
import com.b5m.adrs.service.Sf1DataQueryService;
import com.b5m.base.common.spring.utils.ApplicationContextUtils;
import com.b5m.base.common.utils.CollectionTools;
import com.b5m.base.common.utils.DateTools;
import com.b5m.base.common.utils.StringTools;
import com.b5m.base.common.utils.WebTools;

public class LogUtils {
	private static Map<String, FileOutputStream> fileMap = new HashMap<String, FileOutputStream>();
	private static String logPath;
	private static ReentrantLock lock = new ReentrantLock();
	private static ReentrantLock writeLock = new ReentrantLock();
	private static ExecutorService executorService = Executors.newFixedThreadPool(20);
	private static List<String> filterRequestParams = CollectionTools.newList("jsoncallback", "isDetail", "_", "keywords","key","kid","mps","rd");

	public static void toMessage(Map<String, String> requestMap, Map<String, String> params, Cookie[] cookies, JSONObject resultMessage) {
		resultMessage.put("uid", "");
		for(String name : requestMap.keySet()){
			if (filterRequestParams.contains(name))
				continue;
			String value = requestMap.get(name);
			if (value != null && !StringUtils.isEmpty(value)) {
				if("aid".equals(name)){
					name = "adsid";
				}
				if("adOwerID".equals(name)){
					name = "aid";
				}
				if(value.indexOf("\n") > 0){
					value = StringUtils.replace(value, "\n", "");
				}
				resultMessage.put(name, value);
			}
		}
		if(cookies != null){
			//设置cid
			String cid = WebTools.getCooKieValue("cookieId", cookies);
			if(!StringTools.isEmpty(cid)){
				resultMessage.put("cid", cid);
			}
			String login = WebTools.getCooKieValue("login", cookies);
			if("true".equals(login)){//只有登陆状态 才添加uid
				//设置uid
				String uid = WebTools.getCooKieValue("token", cookies);
				if(!StringTools.isEmpty(uid) || "undefined".equals(uid) || "null".equals(uid)){
					resultMessage.put("uid", uid);
				}
			}
			//设置uid B5T ID(tid)
			String b5tid = WebTools.getCooKieValue("b5tuid", cookies);
			if(!StringTools.isEmpty(b5tid)){
				resultMessage.put("b5tuid", b5tid);
			}
		}
		resultMessage.put("ip", requestMap.get("ip"));
		resultMessage.put("dr", requestMap.get("referer"));
		for (String key : params.keySet()) {
			resultMessage.put(key, params.get(key));
		}
		resultMessage.put("rp", "1002");//判断是否来自网站 还是 B5T
		if(!StringUtils.isEmpty(resultMessage.getString("uid"))){
			resultMessage.put("rp", "1001");
		}
		resultMessage.put("lt", 8800);
		resultMessage.put("ct", new Date().getTime());
	}
	
	public static void infoClick(final AntiCheatRuleService antiCheatRuleService, final Sf1DataQueryService sf1DataQueryService, HttpServletRequest request, final Map<String, String> params, final String dd){
		final Map<String, String> requestMap = cloneMap(request);
		final Cookie[] cookies = request.getCookies();
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					sf1DataQueryService.recordAd(params.get("source"), params.get("key"), params.get("kid"), params.get("aid"), "1");
					String b5tid = WebTools.getCooKieValue("b5tuid", cookies);
					//日志记录供sf1使用
					sf1DataQueryService.recordToSf1(requestMap.get("ip"), b5tid, dd, requestMap.get("durl"), params.get("ad"), requestMap.get("da"), "0.5");
					
					if(!antiCheatRuleService.filter(new RuleBean(uid(cookies, requestMap), bitid(cookies, requestMap), dd, requestMap.get("ip")))){
						//metaq 日志记录
						JSONObject resultMessage = new JSONObject();
						toMessage(requestMap, params, cookies, resultMessage);
						for(String key : params.keySet()){
							if (filterRequestParams.contains(key)) continue;
							resultMessage.put(key, params.get(key));
						}
						writeFile(resultMessage.toJSONString() + "\n", "click", true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void infoPV(final HttpServletRequest request, final Map<String, String> params, final String position, final JSONArray jsonArray){
		final Map<String, String> requestMap = cloneMap(request);
		final Cookie[] cookies = request.getCookies();
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					int length = jsonArray.size();
					if (length < 1) return;
					
					JSONObject resultMessage = new JSONObject();
					StringBuilder sb = new StringBuilder();
					toMessage(requestMap, params, cookies, resultMessage);
					//Action 108-pv,103-click
					resultMessage.put("ad", 108);
					
					for (int index = 0; index < length; index++) {
						JSONObject resultMessageClone = (JSONObject) resultMessage.clone();
						
						JSONObject jsonObject = jsonArray.getJSONObject(index);
						if (filterLog(jsonObject))
							continue;
						//广告主id，原先是用 aid定义广告id的，所以这里不改变原来的逻辑做了相应的调整
						String adOwerID = jsonObject.getString("AdOwerID");
						if(StringTools.isEmpty(adOwerID)) adOwerID = "";
						resultMessageClone.put("aid", adOwerID);
						//广告id
						resultMessageClone.put("adsid", jsonObject.getString("aid"));
						//docid
						resultMessageClone.put("dd", jsonObject.getString("DOCID"));
						//位置
						resultMessageClone.put("da", position + (index + 1));
						
						sb.append(resultMessageClone).append("\n");
					}
					writeFile(sb.toString(), "pv", true);
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		});
	}
	
	public static boolean filterLog(JSONObject jsonObject) {
		return false;
	}

	public static void info(final String message, final String prefix, final boolean haveTime) {
		if(StringTools.isEmpty(message)) return;
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					writeFile(message, prefix, haveTime);
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		});
	}
	
	public static void writeFile(String message, String prefix, boolean haveTime) throws IOException{
		Date now = DateTools.now();
		String date = DateTools.formate(now);
		String fileName = getFileName(prefix, date);
		FileOutputStream fileOutputStream = fileMap.get(fileName);
		if (fileOutputStream == null) {
			fileOutputStream = newOutputStream(fileName, fileMap);
		}
		writeLock.lock();
		try {
			if(haveTime){
				IOUtils.write(message, fileOutputStream, "UTF-8");
			}else{
				IOUtils.write(DateTools.formate(now, "yyyy-MM-dd HH:mm:ss") + "-->" + message + "\n", fileOutputStream, "UTF-8");
			}
		} finally {
			writeLock.unlock();
		}
	}

	public static FileOutputStream newOutputStream(String fileName,
			Map<String, FileOutputStream> fileMap) throws FileNotFoundException {
		lock.lock();
		try {
			fileMap.clear();
			closeOutputStream(fileMap);
			File file = new File(getLogPath());
			if (!file.exists()) {
				file.mkdirs();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(new File(getLogPath() + fileName), true);
			fileMap.put(fileName, fileOutputStream);
			return fileOutputStream;
		} finally {
			lock.unlock();
		}
	}

	public static String getFileName(String prefix, String date) {
		String fileName = prefix + "-" + date + ".log";
		return fileName;
	}

	public static void closeOutputStream(Map<String, FileOutputStream> fileMap) {
		for (String file : fileMap.keySet()) {
			FileOutputStream outputStream = fileMap.get(file);
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static String getLogPath() {
		if (logPath == null) {
			Properties properties = ApplicationContextUtils.getBean("properties", Properties.class);
			return properties.getProperty("keywords.record.path");
		}
		return logPath;
	}
	
	private static String bitid(Cookie[] cookies, Map<String, String> requestMap){
		String tid = WebTools.getCooKieValue("tid", cookies);
		if(StringUtils.isEmpty(tid)){
			tid = requestMap.get("tid");
		}
		return tid;
	}
	
	private static String uid(Cookie[] cookies, Map<String, String> requestMap){
		String tid = WebTools.getCooKieValue("token", cookies);
		if(StringUtils.isEmpty(tid)){
			tid = requestMap.get("uid");
		}
		return tid;
	}
	
	private static Map<String, String> cloneMap(HttpServletRequest request){
		Map<String, String> requestMap = new HashMap<String, String>();
		Enumeration<String> keys = request.getParameterNames();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			if("source".equals(key)) continue;
			requestMap.put(key, request.getParameter(key));
		}
		requestMap.put("ip", WebTools.getIpAddr(request));
		requestMap.put("referer", WebUtils.referer(request));
		return requestMap;
	}
	
	public static void main(String[] args) throws Exception {
		IOUtils.write("你好", new FileOutputStream(new File("text.txt")), "GBK");
	}
}
