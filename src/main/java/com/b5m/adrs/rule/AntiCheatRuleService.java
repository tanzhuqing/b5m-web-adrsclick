package com.b5m.adrs.rule;

/**
 *  @description
 * 1.同一TUID/MUID一天内点击同一款商品，从第一次点击开始，10秒只计其一次点击； 
 * 2.同一TUID/MUID一天内点击同一款商品，1天内只计其三次点击； 
 * 3.同一TUID/MUID一天内点击不同款商品，从第一次点击开始，1分钟内只计其十次点击； 
 * 4.同一TUID/MUID一天内点击不同款商品，从第一次点击开始，1小时内只计其二十次点击； 
 * 5.同一TUID/MUID一天内点击不同款商品，从第一次点击开始，24小时内只计其五十次点击； 
 * 6.同一IP，多个TUID/MUID一天内点击同款或不同款商品，从第一次点击开始，24小时内只计其一百次点击；
 * @author echo
 * @time 2014年5月7日
 * @mail wuming@b5m.com
 */
public interface AntiCheatRuleService {
	
	/**
	 *<font style="font-weight:bold">Description: </font> <br/>
	 * userid 只是一个唯一表识，可以是用户名，也可以是其他的组合
	 * @author echo
	 * @email wuming@b5m.cn
	 * @since 2014年5月7日 上午11:30:42
	 *
	 * @param userId
	 * @return
	 */
	boolean filter(RuleBean ruleBean);
}
