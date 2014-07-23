package com.b5m.adrs.rule.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.cache.MemCachedUtils;
import com.b5m.adrs.rule.AntiCheatRule;
import com.b5m.adrs.rule.AntiCheatRuleService;
import com.b5m.adrs.rule.RuleBean;
/**
 *  @author echo
 *  @time 2014年5月7日
 *  @mail wuming@b5m.com
 */
public class AntiCheatRuleServiceImpl implements AntiCheatRuleService{
	private List<AntiCheatRule> antiCheatRules;
	
	public boolean filter(RuleBean ruleBean){// userId 是 TUID 和 MUID 的组合
		getValueFromMemcache(ruleBean);
		for(AntiCheatRule antiCheatRule : antiCheatRules){
			if(antiCheatRule.filter(ruleBean)){
				return true;
			};
		}
		setValueToMemcache(ruleBean);
		return false;
	}
	
	public void setValueToMemcache(RuleBean ruleBean){
		long remainTime = getRemainTime(ruleBean.getNowtime());
		Map<String, String> map = ruleBean.getValues();
		for(String key : map.keySet()){
			MemCachedUtils.setCache(key, map.get(key), (int)remainTime);
		}
	}
	
	public void getValueFromMemcache(RuleBean ruleBean){
		long now = new Date().getTime();
		ruleBean.setNowtime(now);
		String uid = ruleBean.getUid();
		String docId = ruleBean.getDocId();
		String mid = ruleBean.getMid();
		String ip = ruleBean.getIp();
		long remainTime = getRemainTime(now);
		if(!StringUtils.isEmpty(uid)){//设置mid memcache值
			Object v = MemCachedUtils.getCache(uid);
			StringBuilder sb = new StringBuilder(100);
			if(v == null){
				v = MemCachedUtils.getCache(mid);
			}else if(!StringUtils.isEmpty(mid)){//如果uid 获取出来的 结果为空，则用mid进行获取
				MemCachedUtils.setCache(mid + "_", uid, (int)remainTime);
			}
			if(v != null){
				sb.append(v.toString());
			}
			String values = getValues(v, docId, now);
			ruleBean.put(uid, values);
//			MemCachedUtils.setCache(uid, values, (int)remainTime);
		}else if(!StringUtils.isEmpty(mid)){//如果uid为空，可能出现用户登出状态下在进行点击 //设置mid memcache值
			Object key = MemCachedUtils.getCache(mid + "_");
			Object v = null;
			if(key == null){//如果uid为空，则用mid进行获取
				v = MemCachedUtils.getCache(mid);
			}else{//如果不为空，则利用uid进行获取
				v = MemCachedUtils.getCache(key.toString());
			}
			String values = getValues(v, docId, now);
			ruleBean.put(mid, values);
//			MemCachedUtils.setCache(mid, values, (int)remainTime);
		}
		//设置ip memcache值
		Object v = MemCachedUtils.getCache(ip);
		String values = getValues(v, docId, now);
		ruleBean.put(ip, values);
//		MemCachedUtils.setCache(ip, values, (int)remainTime);
	}
	
	public String getValues(Object v, String docId, long now){
		StringBuilder sb = new StringBuilder(100);
		if(v != null){
			sb.append(v.toString());
		}
		sb.append(docId).append(",").append(now).append(";");
		return sb.toString();
	}
	
	public void setAntiCheatRules(List<AntiCheatRule> antiCheatRules) {
		this.antiCheatRules = antiCheatRules;
	}
	
	public long getRemainTime(long now){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long time = c.getTime().getTime();
		return (time - now + 86400000)/1000;
	}
	
	public static void main(String[] args) {
		System.out.println((int)new AntiCheatRuleServiceImpl().getRemainTime(new Date().getTime()));
		System.out.println(new AntiCheatRuleServiceImpl().getRemainTime(new Date().getTime()));
		System.out.println(new AntiCheatRuleServiceImpl().getRemainTime(new Date().getTime()));
	}
}
