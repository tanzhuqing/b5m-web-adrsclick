package com.b5m.adrs.rule;

/**
 * 防作弊规则
 * @author echo
 * @time 2014年5月7日
 * @mail wuming@b5m.com
 */
public interface AntiCheatRule {
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * 根据userid 这唯一条件进行过滤
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月7日 上午11:09:56
	 *
	 * @param ruleBean
	 * @return
	 */
	boolean filter(RuleBean ruleBean);
	
}
