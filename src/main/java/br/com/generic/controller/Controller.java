package br.com.generic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.generic.dto.Example;
import br.com.generic.producer.ProducerSenderServiceUtil;

@RestController
public class Controller {
	

	@Autowired
	private ProducerSenderServiceUtil messageSenderService;
	
	@PostMapping("/example")
	public void sendMessage(@RequestBody Example example){
		System.out.println("Sending message to topic");
		messageSenderService.sendMessage("topicName",example);
		System.out.println("Sucsess!");
	}
	
	
}
