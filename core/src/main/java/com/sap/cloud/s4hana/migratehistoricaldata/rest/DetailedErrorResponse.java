package com.sap.cloud.s4hana.migratehistoricaldata.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Optional: A DTO to represent the error response body in JSON.
 * <p>
 * The error model follows the error model for OData v2 Services
 * 
 * @see DetailedExceptionMapper
 * @see <a href="https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/API_SALES_ORDER_SRV">Response model for an rrror for Process Sales Order API</a>
 *
 */
@JsonRootName("error")
public class DetailedErrorResponse {
	
	public static class ErrorMessage {
		@JsonProperty("lang")
		private final String lang = "en";
		
		@Getter @Setter
		@JsonProperty("value")
		private String value;
	}
	
	@Getter @Setter
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("message")
	private DetailedErrorResponse.ErrorMessage message = new ErrorMessage();
	
	@Getter @Setter
	@JsonProperty("innererror")
	private DetailedErrorResponse innerError;
	
	private DetailedErrorResponse(String code, String message, DetailedErrorResponse innerError) {
		this.code = code;
		this.message.value = message;
		this.innerError = innerError;
	}
	
	public static DetailedErrorResponse of(String code, String message) {
		return DetailedErrorResponse.of(code, message, /* innerError = */ null);
	}
	
	public static DetailedErrorResponse of(String code, String message, DetailedErrorResponse innerError) {
		return new DetailedErrorResponse(code, message, innerError);
	}
	
	private DetailedErrorResponse() {
		// default constructor to enable deserialization by Jackson
	}
	
	@JsonIgnore
	public String getMessageText() {
		return message.getValue();
	}

	@JsonIgnore
	public void setMessageText(String messageText) {
		message.setValue(messageText);
	}

}