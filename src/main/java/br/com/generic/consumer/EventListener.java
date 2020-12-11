package br.com.generic.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.generic.dto.ExampleDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventListener {

	@KafkaListener(
			id="message.topic",
			groupId = "${topic.input.group}",
			topics = "${topic.input.destination}"
	)
	public void consume(@Payload Message<ExampleDTO> eventsPayload) {
		
		try {
			Acknowledgment ack = eventsPayload.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
			if (ack != null) {
				ack.acknowledge();
			}
			log.info("Message: " + eventsPayload.getPayload().getContent());
			
			
		} catch (Exception e) {
			log.error("Message Topic:" + e.getMessage());
		}
	}

}
