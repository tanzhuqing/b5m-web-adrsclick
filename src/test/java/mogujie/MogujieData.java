package mogujie;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mogujie.domain.Attribute;
import mogujie.domain.Product;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.b5m.adrs.entity.AdGood;
import com.b5m.base.common.utils.StringTools;
import com.b5m.base.common.utils.WebTools;
import com.b5m.dao.Dao;
import com.b5m.dao.domain.cnd.Cnd;
import com.b5m.dao.domain.cnd.Op;
import com.b5m.dao.impl.DaoImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MogujieData {
	
	public static void main(String[] args) throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://10.10.105.8:3306/b5m_adrs?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxIdleTime(1800);
		dataSource.setInitialPoolSize(5);
		dataSource.setIdleConnectionTestPeriod(1200);
		dataSource.setUser("b5m");
		dataSource.setPassword("iz3n3s0ft");
		Dao dao = new DaoImpl(dataSource);
		
		String xml = WebTools.executeGetMethod("http://share.mogujie.com/cps/data/b5m/cpc/items/0001.xml");
		SAXReader saxReader = new SAXReader();
		List<Product> products1 = new ArrayList<Product>();
		try {
			Document document = saxReader.read(new ByteArrayInputStream(xml.getBytes()));
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			for(Element element : elements){
				products1.add(convertoProduct(element));
			}
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
		List<String> docIds = new ArrayList<String>();
		for(Product product : products1){
			String docId = StringTools.MD5(product.getUrl());
			Map map = dao.queryUniqueBySql("select ID from T_AD_GOOD where DOC_ID = ?", new Object[]{docId}, Map.class);
			if(map == null){
				write("insert into T_AD_GOOD(SOURCE_SHOP,DOC_ID,URL,PRICE,CATEGORY,TITLE,FLAG,PICTURE,CREATE_TIME,UPDATE_TIME,SHOW_STATUS) "
						+ "values('蘑菇街','"+docId+"','"+product.getUrl()+"','"+product.getPrice()+"','"+StringTools.replace(product.getCategory(), "-", "&gt;")
						+"','"+product.getTitle()+"',-1,'"+product.getPicture()+"',now(),now(),1);", "/home/echo/", "mogujie.sql");
			}else{
				docIds.add(docId);
			}
		}
		List<String> docIdsDb =  dao.queryBySql("select DOC_ID from T_AD_GOOD", new Object[]{}, String.class);
		for(String docId : docIdsDb){
			if(!docIds.contains(docId)){
				write("delete from t_search_keywords where goods_id in (select id from T_AD_GOOD where DOC_ID = '" + docId + "');", "/home/echo/", "mogujiedelete.sql");
				write("delete from T_AD_GOOD where DOC_ID = '" + docId + "';", "/home/echo/", "mogujiedelete.sql");
			}
		}
		System.out.println(docIds);
//		System.out.println(StringTools.MD5("b5m000"));
	}
	
	@Test
	public void testInsertKeyword() throws PropertyVetoException{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://10.10.105.8:3306/b5m_adrs?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxIdleTime(1800);
		dataSource.setInitialPoolSize(5);
		dataSource.setIdleConnectionTestPeriod(1200);
		dataSource.setUser("b5m");
		dataSource.setPassword("iz3n3s0ft");
		Dao dao = new DaoImpl(dataSource);
		
		String xml = WebTools.executeGetMethod("http://share.mogujie.com/cps/data/b5m/cpc/items/0001.xml");
		SAXReader saxReader = new SAXReader();
		List<Product> products1 = new ArrayList<Product>();
		try {
			Document document = saxReader.read(new ByteArrayInputStream(xml.getBytes()));
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			for(Element element : elements){
				products1.add(convertoProduct(element));
			}
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
		for(Product product : products1){
			String docId = StringTools.MD5(product.getUrl());
			Map map = dao.queryUniqueBySql("select ID from T_AD_GOOD where DOC_ID = ?", new Object[]{docId}, Map.class);
			if(map != null){
				String id = map.get("ID").toString();
				String[] tags = StringTools.split(product.getCategory(), "-");
				for(int i = 0; i < tags.length; i++){
					if(i == 0) continue;
					String tag = tags[i];
					if(tag.indexOf("/") > 0){
						String[] tagarray = StringTools.split(tag, "/");
						for(String t : tagarray){
							write("insert into t_search_keywords(keywords,goods_id) values('" + t + "','"+id+"');" ,"/home/echo/", "mogujiekeyword.sql");
						}
					}else{
						write("insert into t_search_keywords(keywords,goods_id) values('" + tags[i] + "','"+id+"');" ,"/home/echo/", "mogujiekeyword.sql");
					}
				}
			}
		}
	}
	
	public static void write(String message, String dir, String fileName){
		File file = new File(dir + fileName);
		try {
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch (Exception e1) {}
	}
	
	@Test
	public void testBack() throws PropertyVetoException{
		ComboPooledDataSource dataSource1 = new ComboPooledDataSource();
		dataSource1.setDriverClass("com.mysql.jdbc.Driver");
		dataSource1.setJdbcUrl("jdbc:mysql://10.10.105.8:3306/b5m_adrs?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		dataSource1.setMinPoolSize(5);
		dataSource1.setMaxPoolSize(20);
		dataSource1.setMaxIdleTime(1800);
		dataSource1.setInitialPoolSize(5);
		dataSource1.setIdleConnectionTestPeriod(1200);
		dataSource1.setUser("b5m");
		dataSource1.setPassword("iz3n3s0ft");
		Dao dao1 = new DaoImpl(dataSource1);
	
		
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://172.16.11.207:3306/b5m_adrs?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxIdleTime(1800);
		dataSource.setInitialPoolSize(5);
		dataSource.setIdleConnectionTestPeriod(1200);
		dataSource.setUser("b5m");
		dataSource.setPassword("izene123");
		Dao dao = new DaoImpl(dataSource);
		
		List<AdGood> adGoods = dao1.query(AdGood.class, Cnd.where("id", Op.GTE, 198).add("id", Op.LTE, 710));
		Long id = null;
		List<Long> ids = new ArrayList<Long>();
		for(AdGood adGood : adGoods){
			if(id == null) {
				id = adGood.getId();
				continue;
			}
			if(id + 1 != adGood.getId()){
				for(long i = (id + 1); i < adGood.getId(); i++){
					ids.add(i);
				}
			}
			id = adGood.getId();
		}
		adGoods = dao.query(AdGood.class, Cnd.whereIn("id", ids.toArray(new Long[]{})));
		for(AdGood adGood : adGoods){
			write("insert into T_AD_GOOD(ID,AD_ID,SOURCE_SHOP,DOC_ID,URL,PRICE,CATEGORY,TITLE,FLAG,PICTURE,CREATE_TIME,UPDATE_TIME,SHOW_STATUS) "
					+ "values("+adGood.getId()+",101,'蘑菇街','"+adGood.getDocId()+"','"+adGood.getUrl()+"','"+adGood.getPrice()+"','"
					+"','"+adGood.getTitle()+"',-1,'"+adGood.getPicture()+"',now(),now(),1);", "/home/echo/", "mogujie.sql");
		}
	}
	
	public static Product convertoProduct(Element element){
		Product product = new Product();
		product.setSource(element.element("source").getTextTrim());
		product.setTitle(element.element("title").getTextTrim());
		product.setContent(element.element("content").getTextTrim());
		product.setUrl(element.element("url").getTextTrim());
		product.setPicture(element.element("picture").getTextTrim());
		product.setPrice(element.element("price").getTextTrim());
		product.setCategory(element.element("category").getTextTrim());
		
		Element attrs = element.element("attributes");
		List<Element> attrList = attrs.elements();
		for(Element attr : attrList){
			Attribute attribute = new Attribute();
			attribute.setName(attr.element("name").getTextTrim());
			attribute.setValue(attr.element("value").getTextTrim());
			product.getAttributes().add(attribute);
		}
		return product;
	}
	
}
