package br.com.generic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExampleDTO {

	@JsonProperty(value = "messageContent")
	private String content;

	
}
