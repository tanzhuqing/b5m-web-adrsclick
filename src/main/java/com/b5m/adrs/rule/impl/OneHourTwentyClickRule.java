package com.b5m.adrs.rule.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.b5m.adrs.rule.AntiCheatRule;
import com.b5m.adrs.rule.RuleBean;
import com.b5m.base.common.utils.DateTools;
/**
 * 同一TUID/MUID一天内点击不同款商品，从第一次点击开始，1小时内只计其二十次点击； 
 * @author echo
 * @time 2014年5月7日
 * @mail wuming@b5m.com
 */
public class OneHourTwentyClickRule implements AntiCheatRule{

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
			//如何时间超过10秒 则跳过
			if(ruleBean.getNowtime() > (Long.valueOf(vs[1]) + 3600000)){
				continue;
			};
			num++;
		}
		if(num > 20){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
//		:1400466488194, ::1400466809946,
//		321752 3600000
		System.out.println(1400466809946l - (1400466488194l + 3600000));
		System.out.println(DateTools.formate(new Date(1400466809946l), "yyyy-MM-dd HH:mm:ss"));
		System.out.println(DateTools.formate(new Date(1400466488194l), "yyyy-MM-dd HH:mm:ss"));
		
	}

}
