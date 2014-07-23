package com.b5m.adrs.rule.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.rule.AntiCheatRule;
import com.b5m.adrs.rule.RuleBean;

/**
 * .同一TUID/MUID一天内点击同一款商品，1天内只计其三次点击；
 * @author echo
 * @time 2014年5月7日
 * @mail wuming@b5m.com
 */
public class SameIPOneDayOneHundredClickRule implements AntiCheatRule{

	@Override
	public boolean filter(RuleBean ruleBean) {
		Map<String, String> map = ruleBean.getValues();
		String key = ruleBean.getIp();
		//没有ip的则不记录
		if(StringUtils.isEmpty(key)){
			return true;
		}
		String value = map.get(key);
		if(StringUtils.isEmpty(value)){
			return false;
		}
		String[] values = StringUtils.split(value, ";");
		int num = 0;
		for(String v : values){
			//为空的 跳过
			if(StringUtils.isEmpty(v)) continue;
			//因为memcache的过期时间 是当天余下来的时间，所以这里不做时间过滤
			num++;
		}
		if(num >100){
			return true;
		}
		return false;
	}

}
