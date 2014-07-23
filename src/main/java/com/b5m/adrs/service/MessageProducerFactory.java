package com.b5m.adrs.service;

import java.util.List;

import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.producer.MessageProducer;

public class MessageProducerFactory {
	private MessageSessionFactory messageSessionFactory;
	private List<String> topics;
	
	public MessageProducerFactory(MessageSessionFactory messageSessionFactory, List<String> topics){
		this.messageSessionFactory = messageSessionFactory;
		this.topics = topics;
	}
	
	public MessageProducer create(){
		MessageProducer producer = messageSessionFactory.createProducer();
		for(String topic : topics){
			producer.publish(topic);
		}
		return producer;
	}
	
}
