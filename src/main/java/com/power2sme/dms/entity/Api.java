package com.power2sme.dms.entity;

import java.util.Date;
import java.util.UUID;

public class Api {

	private String completeUrl;
	private String apiname;
	private String methodType;
	private String requestPayload;
	private String responsePayload;
	private Date requestedTime;
	private Date respondTime;
	private Long response_time_millis;
	private String headers;
	private Integer httpStatusCode;
	private boolean isSuccess;
	private String clientIp;
	private String serverIp;
	private String uuid;
	private String sessionid;
	private String userNameInSession;
	private String apiKey;
	
	
	public Api(){
		uuid=UUID.randomUUID().toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Api [completeUrl=" + completeUrl + ", apiname=" + apiname + ", methodType=" + methodType
				+ ", requestPayload=" + requestPayload + ", responsePayload=" + responsePayload + ", requestedTime="
				+ requestedTime + ", respondTime=" + respondTime + ", response_time_millis=" + response_time_millis
				+ ", headers=" + headers + ", isSuccess=" + isSuccess + ", clientIp=" + clientIp + ", serverIp="
				+ serverIp + ", uuid=" + uuid + ", sessionid=" + sessionid + ", userNameInSession=" + userNameInSession
				+ ", apiKey=" + apiKey + "]";
	}

	/**
	 * @return the completeUrl
	 */
	public String getCompleteUrl() {
		return completeUrl;
	}
	/**
	 * @param completeUrl the completeUrl to set
	 */
	public void setCompleteUrl(String completeUrl) {
		this.completeUrl = completeUrl;
	}
	/**
	 * @return the apiname
	 */
	public String getApiname() {
		return apiname;
	}
	/**
	 * @param apiname the apiname to set
	 */
	public void setApiname(String apiname) {
		this.apiname = apiname;
	}
	/**
	 * @return the methodType
	 */
	public String getMethodType() {
		return methodType;
	}
	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	/**
	 * @return the requestPayload
	 */
	public String getRequestPayload() {
		return requestPayload;
	}
	/**
	 * @param requestPayload the requestPayload to set
	 */
	public void setRequestPayload(String requestPayload) {
		this.requestPayload = requestPayload;
	}
	/**
	 * @return the responsePayload
	 */
	public String getResponsePayload() {
		return responsePayload;
	}
	/**
	 * @param responsePayload the responsePayload to set
	 */
	public void setResponsePayload(String responsePayload) {
		this.responsePayload = responsePayload;
	}
	/**
	 * @return the requestedTime
	 */
	public Date getRequestedTime() {
		return requestedTime;
	}
	/**
	 * @param requestedTime the requestedTime to set
	 */
	public void setRequestedTime(Date requestedTime) {
		this.requestedTime = requestedTime;
	}
	/**
	 * @return the respondTime
	 */
	public Date getRespondTime() {
		return respondTime;
	}
	/**
	 * @param respondTime the respondTime to set
	 */
	public void setRespondTime(Date respondTime) {
		this.respondTime = respondTime;
	}
	/**
	 * @return the response_time_millis
	 */
	public Long getResponse_time_millis() {
		return response_time_millis;
	}
	/**
	 * @param response_time_millis the response_time_millis to set
	 */
	public void setResponse_time_millis(Long response_time_millis) {
		this.response_time_millis = response_time_millis;
	}
	/**
	 * @return the headers
	 */
	public String getHeaders() {
		return headers;
	}
	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	/**
	 * @return the httpStatusCode
	 */
	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	/**
	 * @param httpStatusCode the httpStatusCode to set
	 */
	public void setHttpStatusCode(Integer httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}
	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}
	/**
	 * @param clientIp the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	/**
	 * @return the serverIp
	 */
	public String getServerIp() {
		return serverIp;
	}
	/**
	 * @param serverIp the serverIp to set
	 */
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @return the sessionid
	 */
	public String getSessionid() {
		return sessionid;
	}
	/**
	 * @param sessionid the sessionid to set
	 */
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	/**
	 * @return the userNameInSession
	 */
	public String getUserNameInSession() {
		return userNameInSession;
	}

	/**
	 * @param userNameInSession the userNameInSession to set
	 */
	public void setUserNameInSession(String userNameInSession) {
		this.userNameInSession = userNameInSession;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
