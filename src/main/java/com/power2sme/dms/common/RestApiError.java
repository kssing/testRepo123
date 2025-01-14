package com.power2sme.dms.common;

public class RestApiError {

	private Integer errorCode;
	private String message;
	
	public RestApiError(Integer errorCode,Throwable e) {
		this.errorCode = errorCode;
		this.message = e.getClass().getSimpleName() + " : "+e.getMessage();
	}

	public RestApiError(Integer errorCode,String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
	

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}
}
