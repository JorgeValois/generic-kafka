package br.com.generic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.generic.dto.Example;

@Service
public class ProducerSenderServiceUtil {

	@Autowired
	private KafkaTemplate<String, Example> produce;
	
	public void sendMessage(String topic, Example dto) {
		produce.send(topic, dto);
		produce.flush();
	}
	
	public void setKafkaTemplate(KafkaTemplate<String, Example> kafkaTemplate) {
		this.produce = kafkaTemplate;
	}
	
	
}
