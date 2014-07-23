package com.b5m.adrs.rule.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.rule.AntiCheatRule;
import com.b5m.adrs.rule.RuleBean;
/**
 * 同一TUID/MUID一天内点击同一款商品，从第一次点击开始，10秒只计其一次点击； 
 * @author echo
 * @time 2014年5月7日
 * @mail wuming@b5m.com
 */
public class TensencondRule implements AntiCheatRule{

	@Override
	public boolean filter(RuleBean ruleBean) {
		Map<String, String> map = ruleBean.getValues();
		String key = ruleBean.getUid();
		if(StringUtils.isEmpty(key)){
			key = ruleBean.getMid();
		}
		if(StringUtils.isEmpty(key)){
			return false;
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
			String[] vs = StringUtils.split(v, ",");
			//不是同一款的 跳过
			if(!vs[0].equals(ruleBean.getDocId())){
				continue;
			}
			//如何时间超过10秒 则跳过
			if(ruleBean.getNowtime() > (Long.valueOf(vs[1]) + 10000)){
				continue;
			};
			num++;
		}
		if(num > 1){
			return true;
		}
		return false;
	}

}
