package metaq;

import java.util.concurrent.Executor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import com.taobao.metamorphosis.exception.MetaClientException;

public class ConsumerApp {
	
	public static void main(String[] args) throws MetaClientException {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("metaq/metaq.xml");
			MessageSessionFactory messageSessionFactoryBean = context.getBean("sessionFactory", MessageSessionFactory.class);
			ConsumerConfig consumerConfig = new ConsumerConfig("ad-consumer");
			MessageConsumer consumer = messageSessionFactoryBean.createConsumer(consumerConfig);
			consumer.subscribe("log_sponsored_ad_sf1", 1024 * 1024, new MessageListener() {
				
				@Override
				public void recieveMessages(Message message) throws InterruptedException {
					System.out.println("Receive message " + new String(message.getData())); 
				}
				
				@Override
				public Executor getExecutor() {
					return null;
				}
			});
			consumer.completeSubscribe();
		} catch (BeansException e) {
			e.printStackTrace();
		} 
	}
	
}
