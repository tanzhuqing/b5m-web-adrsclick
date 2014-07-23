package analysis;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.b5m.adrs.analysis.AVLTree;
import com.b5m.adrs.analysis.FenciHelper;
import com.b5m.adrs.analysis.SearchKeywords;
import com.b5m.dao.Dao;
import com.b5m.dao.impl.DaoImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class CreateTree {

	public static void main(String[] args) throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://172.16.11.207/b5m_adrs?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxIdleTime(1800);
		dataSource.setInitialPoolSize(5);
		dataSource.setIdleConnectionTestPeriod(1200);
		dataSource.setUser("b5m");
		dataSource.setPassword("izene123");
		Dao dao = new DaoImpl(dataSource);
		
		List<SearchKeywords> list = dao.queryAll(SearchKeywords.class);
		long start = System.currentTimeMillis();
		AVLTree avlTree = FenciHelper.createTree(list);
		System.out.println("create tree time -->" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		List<SearchKeywords> keywordsList = FenciHelper.analysis("优默 夏装衣服 男士短袖t恤 男 短袖 潮男装韩版t男恤 男t恤 春款", avlTree);
		System.out.println("fenci time -->" + (System.currentTimeMillis() - start));
		for(SearchKeywords keywords : keywordsList){ 
			System.out.println(JSON.toJSONString(keywords));
		}
		System.out.println("------------>end");
		Thread.sleep(50000000);
	}
	
}