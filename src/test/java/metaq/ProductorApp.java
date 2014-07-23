package metaq;

import java.util.Calendar;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.producer.MessageProducer;

public class ProductorApp {
	
	public static void main(String[] args) {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("metaq/metaq.xml");
			
			Record record = new Record();
			record.setCreateTime(new Date());
			record.setGoodsId(1);
			record.setKeywords("百万小店 男装2014春季新品衬衣韩版潮男修身碎花拼接长袖衬衫潮-淘宝网");
			record.setKeywordsId(100);
			record.setType(108);
//			MetaqTemplate template = context.getBean("metaqTemplate", MetaqTemplate.class);
//			template.send(MessageBuilder.withTopic("data-record").withBody(record));
			
			Calendar c1 = Calendar.getInstance();
			c1.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), c1.get(Calendar.HOUR_OF_DAY), 0, 0);
			c1.set(Calendar.MILLISECOND, 0);
			long timeHour = c1.getTime().getTime();
			
			c1.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), 0, 0, 0);
			long timeDay = c1.getTime().getTime();
			
			MessageSessionFactory messageSessionFactoryBean = context.getBean("sessionFactory", MessageSessionFactory.class);
			MessageProducer producer = messageSessionFactoryBean.createProducer();
			producer.publish("search-click-pv-data");
			for(int i = 0; i < 100; i++){
				for(int j = 0; j < 5; j++){
					producer.sendMessage(new Message("search-click-pv-data", (i+",百万小店,"+(i+10)+","+timeHour+","+timeDay+",1").toString().getBytes()));
				}
			}
			System.out.println("----------------------");
			//template.s
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
