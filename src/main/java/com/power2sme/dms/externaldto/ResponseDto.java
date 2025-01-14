package com.power2sme.dms.externaldto;


import com.fasterxml.jackson.annotation.JsonProperty;


public class ResponseDto {
	
	@JsonProperty("message")
	private String message;
	@JsonProperty("errorCode")
	private int errorCode;
	@JsonProperty("totalRecords")  
	private int totalRecords;
	@JsonProperty("data") 
	private Object data;
	@JsonProperty("status") 
	private String status;
	  
//		public ResponseDto() {
//		message = PropertyUtil.properties.getProperty("API_MESSAGE_SUCCESS");
//	    errorCode = 0;
//	    data = new Object();
//	    totalRecords = 0;
//	    status = 0;
//	  }

	  
	@Override
	public String toString() {
		return ResponseDtoService.getJson(this);
	}
	
	public String getMessage() {
		return message;
	}
	
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getTotalRecords() {
		return totalRecords;
	}
	
	
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public Object getData() {
		return data;
	}
	
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}


}
