package br.com.generic.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import br.com.generic.dto.ExampleDTO;
import br.com.generic.dto.WorkshopDTO;

@Configuration
public class KafkaConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value(value = "${topic.input.group}")
	private String consumerGroupId;
	
	@Bean
	public RetryTemplate retryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
		exponentialBackOffPolicy.setInitialInterval(1000L);
		exponentialBackOffPolicy.setMultiplier(2L);
		exponentialBackOffPolicy.setMaxInterval(60000L);
		retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(5);
		retryTemplate.setRetryPolicy(retryPolicy);
		return retryTemplate;
	}

	/* ============================= CONSUMER ============================ */

	@Bean
	public ConsumerFactory<String, ExampleDTO> consumerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("br.com.generic.dto.ExampleDTO", ExampleDTO.class);
		typeMapper.setIdClassMapping(classMap);

		typeMapper.addTrustedPackages("*");

		JsonDeserializer<ExampleDTO> valueDeserializer = new JsonDeserializer<>(ExampleDTO.class);
		valueDeserializer.setTypeMapper(typeMapper);
		valueDeserializer.setUseTypeMapperForKey(true);

		return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), valueDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, ExampleDTO> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, ExampleDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}


	 /* ============================= PRODUCER ============================ */ 

	
	@Bean
	public ProducerFactory<String, ?> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
		configProps.put(ProducerConfig.ACKS_CONFIG, "1");
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}
	
	@Bean
	public KafkaTemplate<String, ?> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

}
