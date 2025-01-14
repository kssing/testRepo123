package com.power2sme.dms.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.power2sme.metalogging.entity.P2SAPILog;
import com.power2sme.metalogging.service.ActiveMQMetaloggingService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Himanshu Shekhar(rishav.arya@power2sme.com)
 * @date 1-Sep-2017
 *
 */
@Service
@Slf4j
@SuppressWarnings("unchecked")
public class FsmeRestUtils {

	private static final String LOG_NULL_RESPONSE = "RESPONSE IS NULL. THIS CAN CAUSE SERIOUS ISSUE. PLEASE CHECK";
	private static final String API_CALLING_GOING_TO_START = "API CALLING GOING TO START";
	private static final String SOMETHING_WENT_WRONG = "SOMETHING WENT WRONG";
	private static final String SEVERE_GENERIC_API_RESPONSES_EXCEPTION = "SEVERE GENERIC API RESPONSES EXCEPTION";
	private static final String HTTP_STATUS_CODE_EXCEPTION = "Http Status CodeException";
	private static final String DASHES = "-------------------";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Autowired
	private RestTemplate restTemplate;

	@Autowired(required = false)
	private ActiveMQMetaloggingService p2sApiServices;
	
//	@Value("${finansme.camunda.authorization}")
//	private String finansmeCamundaAuthorization;

	@Autowired
	private Environment env;

//	public <T, S> ResponseEntity<T> callCamundaJsonAPI(String url, HttpMethod httpMethod, S requestJsonObject,
//			Map<String, String> headers, Class<T> responseClassType)
//	{
//		if(headers==null)
//		{
//			headers=new HashMap<>();
//		}
////		headers.put("Authorization", "Basic c29tOnNvbQ==");
//		headers.put("Authorization", finansmeCamundaAuthorization);
//		return callJsonAPI(url, httpMethod, requestJsonObject, headers, responseClassType);
//	}
	
	/**
	 * This method returns a response after calling exchange method with the
	 * specified URL,httpMethod,httpEntity. This method is called by signinUser
	 * method implemented in UserLoginControllerImpl class.
	 * 
	 * @param {@link
	 *            String}
	 * @param {@link
	 *            HttpMethod}
	 * @param {@link
	 *            Map}
	 * @param {@link
	 *            Class}
	 * @return {@link ResponseEntity}
	 * @throws RestClientException
	 */
	public <T, S> ResponseEntity<T> callJsonAPI(String url, HttpMethod httpMethod, S requestJsonObject,
			Map<String, String> headers, Class<T> responseClassType)
	{
		return callJsonAPI(url, httpMethod, requestJsonObject,headers, responseClassType, restTemplate);
	}
	
	public <T, S> ResponseEntity<T> callJsonAPI(String url, HttpMethod httpMethod, S requestJsonObject,
			Map<String, String> headers, Class<T> responseClassType, RestTemplate restTemplate) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		if (headers != null) {
			Set<Entry<String, String>> headersEntry = headers.entrySet();
			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
			while (headerEntryIterator.hasNext()) {
				Entry<String, String> entry = headerEntryIterator.next();
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}
		HttpEntity<?> httpEntity = new HttpEntity<>(requestJsonObject, httpHeaders);
		log.debug( httpEntity.toString());
		log.debug( "URL:: " + url);
		log.debug( "Response Type :: " + responseClassType.getName());
		ResponseEntity<T> response;
		if (responseClassType.isAssignableFrom(String.class)) {
			try {
				response = callNonStringApi(restTemplate, url, httpMethod, httpEntity, responseClassType);
			} catch (HttpStatusCodeException h) {
				log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
				log.info( h.getResponseBodyAsString());
				ResponseEntity<String> resp = new ResponseEntity<>(h.getResponseBodyAsString(),
						getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
				response = (ResponseEntity<T>) resp;
				logApi(url, httpMethod, httpEntity, response);
				log.error( "", h);
			}
		} else {
			response = callNonStringApi(restTemplate, url, httpMethod, httpEntity, responseClassType);
		}
		return response;
	}

	public <T, S> ResponseEntity<T> callJsonParamAPI(String url, HttpMethod httpMethod,
			LinkedMultiValueMap<String, String> reqMap, Map<String, String> headers, Class<T> responseClassType) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		if (headers != null) {
			Set<Entry<String, String>> headersEntry = headers.entrySet();
			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
			while (headerEntryIterator.hasNext()) {
				Entry<String, String> entry = headerEntryIterator.next();
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<LinkedMultiValueMap<String, String>>(
				reqMap, httpHeaders);

		log.debug( httpEntity.toString());
		log.debug( "URL:: " + url);
		log.debug( "Response Type :: " + responseClassType.getName());

		ResponseEntity<T> response;
		if (responseClassType.isAssignableFrom(String.class)) {
			try {
				response = callNonStringQueryParamApi(restTemplate, url, httpMethod, httpEntity, responseClassType);
			} catch (HttpStatusCodeException h) {
				log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
				log.info( h.getResponseBodyAsString());
				ResponseEntity<String> resp = new ResponseEntity<>(h.getResponseBodyAsString(),
						getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
				response = (ResponseEntity<T>) resp;
				logApi(url, httpMethod, httpEntity, response);
				log.error( "", h);
			}
		} else {
			response = callNonStringQueryParamApi(restTemplate, url, httpMethod, httpEntity, responseClassType);
		}
		return response;
	}

	public <T, S> ResponseEntity<T> callMultipartJsonAPI(String url, HttpMethod httpMethod,
			LinkedMultiValueMap<String, Object> reqMap, Map<String, String> headers, Class<T> responseClassType) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		if (headers != null) {
			Set<Entry<String, String>> headersEntry = headers.entrySet();
			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
			while (headerEntryIterator.hasNext()) {
				Entry<String, String> entry = headerEntryIterator.next();
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				reqMap, httpHeaders);

		log.debug( httpEntity.toString());
		log.debug( "URL:: " + url);
		log.debug( "Response Type :: " + responseClassType.getName());

		ResponseEntity<T> response;
		if (responseClassType.isAssignableFrom(String.class)) {
			try {
				response = callNonStringApi(restTemplate, url, httpMethod, httpEntity, responseClassType);
			} catch (HttpStatusCodeException h) {
				log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
				log.info( h.getResponseBodyAsString());
				ResponseEntity<String> resp = new ResponseEntity<>(h.getResponseBodyAsString(),
						getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
				response = (ResponseEntity<T>) resp;
				logApi(url, httpMethod, httpEntity, response);
				log.error( "", h);
			}
		} else {
			response = callNonStringApi(restTemplate, url, httpMethod, httpEntity, responseClassType);
		}
		return response;
	}

	/**
	 * This method returns a response after calling exchange method with the
	 * specified URL,httpMethod,httpEntity. This method is called by signinUser
	 * method implemented in UserLoginControllerImpl class.
	 * 
	 * @param {@link
	 *            String}
	 * @param {@link
	 *            HttpMethod}
	 * @param {@link
	 *            Map}
	 * @param {@link
	 *            Class}
	 * @return {@link ResponseEntity} with json string
	 * @throws RestClientException
	 */
//	public ResponseEntity<String> callFormAPIWithXMLDataResponse(String url, MultiValueMap<String, Object> formValues,
//			Map<String, String> headers, Class<?> xmlSuccessMappingClass, Class<?> xmlErrorMappingClass) {
//		RestTemplate rawRestTemplate = new RestTemplate();
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setHttpClient(httpClient);
//		rawRestTemplate.setRequestFactory(requestFactory);
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
//		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		if (headers != null) {
//			Set<Entry<String, String>> headersEntry = headers.entrySet();
//			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
//			while (headerEntryIterator.hasNext()) {
//				Entry<String, String> entry = headerEntryIterator.next();
//				httpHeaders.add(entry.getKey(), entry.getValue());
//			}
//		}
//		HttpEntity<?> httpEntity = new HttpEntity<>(formValues, httpHeaders);
//		log.debug( httpEntity.toString());
//		ResponseEntity<String> response = null;
//		try {
//			ResponseEntity<?> res = callXmlApi(rawRestTemplate, url, HttpMethod.POST, httpEntity,
//					xmlSuccessMappingClass, xmlErrorMappingClass);
//			response = (ResponseEntity<String>) res;
//		} catch (Exception e) {
//			log.error( DASHES + SEVERE_GENERIC_API_RESPONSES_EXCEPTION + DASHES);
//			log.error( SOMETHING_WENT_WRONG, e);
//			log.error( DASHES);
//		}
//		return response;
//	}

	/**
	 * This method returns a response after calling exchange method with the
	 * specified URL,httpMethod,httpEntity. This method is called by signinUser
	 * method implemented in UserLoginControllerImpl class.
	 * 
	 * @param {@link
	 *            String}
	 * @param {@link
	 *            HttpMethod}
	 * @param {@link
	 *            Map}
	 * @param {@link
	 *            Class}
	 * @return {@link ResponseEntity} with json string
	 * @throws RestClientException
	 */
	public ResponseEntity<String> callFormAPIWithJSONDataResponse(String url, MultiValueMap<String, Object> formValues,
			Map<String, String> headers) {
		log.debug( String.valueOf("URL TO CALL :: " + url));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		if (headers != null) {
			Set<Entry<String, String>> headersEntry = headers.entrySet();
			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
			while (headerEntryIterator.hasNext()) {
				Entry<String, String> entry = headerEntryIterator.next();
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<?> httpEntity = new HttpEntity<>(formValues, httpHeaders);
		log.debug( String.valueOf(httpEntity));
		ResponseEntity<String> response = null;
		Gson gson = new Gson();
		try {
			log.debug( API_CALLING_GOING_TO_START);
			ResponseEntity<Object> res = callApi(restTemplate, url, HttpMethod.POST, httpEntity, Object.class);
			if (res != null) {
				ResponseEntity<String> resp = new ResponseEntity<>(gson.toJson(res.getBody()),
						getHeadersCopy(res.getHeaders()), res.getStatusCode());
				response = resp;
			} else {
				response = null;
				log.debug( LOG_NULL_RESPONSE);
			}
			logApi(url, HttpMethod.POST, httpEntity, response);
		} catch (HttpStatusCodeException h) {
			log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
			log.info( h.getResponseBodyAsString());
			ResponseEntity<String> resp = new ResponseEntity<>(h.getResponseBodyAsString(),
					getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
			response = resp;
			logApi(url, HttpMethod.POST, httpEntity, response);
			log.error( "", h);
		}
		return response;
	}

	/**
	 * This method returns a response after calling exchange method with the
	 * specified URL,httpMethod,httpEntity. This method is called by signinUser
	 * method implemented in UserLoginControllerImpl class.
	 * 
	 * @param {@link
	 *            String}
	 * @param {@link
	 *            HttpMethod}
	 * @param {@link
	 *            Map}
	 * @param {@link
	 *            Class}
	 * @return {@link ResponseEntity} with json string
	 * @throws RestClientException
	 */
	public ResponseEntity<String> callFormAPIWithStringResponse(String url, MultiValueMap<String, Object> formValues,
			Map<String, String> headers) {
		log.debug( String.valueOf("URL TO CALL :: " + url));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		if (headers != null) {
			Set<Entry<String, String>> headersEntry = headers.entrySet();
			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
			while (headerEntryIterator.hasNext()) {
				Entry<String, String> entry = headerEntryIterator.next();
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<?> httpEntity = new HttpEntity<>(formValues, httpHeaders);
		log.debug( String.valueOf(httpEntity));
		ResponseEntity<String> response = null;
		try {
			log.debug( API_CALLING_GOING_TO_START);
			ResponseEntity<String> res = callApi(restTemplate, url, HttpMethod.POST, httpEntity, String.class);
			response = res;
			logApi(url, HttpMethod.POST, httpEntity, response);
		} catch (HttpStatusCodeException h) {
			log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
			log.info( h.getResponseBodyAsString());
			ResponseEntity<String> resp = new ResponseEntity<>(h.getResponseBodyAsString(),
					getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
			response = resp;
			logApi(url, HttpMethod.POST, httpEntity, response);
			log.error( "", h);
		}
		return response;
	}

	/**
	 * This method returns a response after calling exchange method with the
	 * specified URL,httpMethod,httpEntity. This method is called by signinUser
	 * method implemented in UserLoginControllerImpl class.
	 * 
	 * @param {@link
	 *            String}
	 * @param {@link
	 *            HttpMethod}
	 * @param {@link
	 *            Map}
	 * @param {@link
	 *            Class}
	 * @return {@link ResponseEntity} with json string
	 * @throws RestClientException
	 */
//	public ResponseEntity<String> callMultipartAPIWithXMLDataResponse(String url,
//			MultiValueMap<String, Object> formValues, Map<String, String> headers, Class<?> xmlSuccessMappingClass,
//			Class<?> xmlErrorMappingClass) {
//		RestTemplate rawRestTemplate = new RestTemplate();// just to maintain
//															// order of xml
//															// marsheller
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
//		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
//		if (headers != null) {
//			Set<Entry<String, String>> headersEntry = headers.entrySet();
//			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
//			while (headerEntryIterator.hasNext()) {
//				Entry<String, String> entry = headerEntryIterator.next();
//				httpHeaders.add(entry.getKey(), entry.getValue());
//			}
//		}
//
//		HttpEntity<?> httpEntity = new HttpEntity<>(formValues, httpHeaders);
//		log.debug( String.valueOf(httpEntity));
//		ResponseEntity<String> response = null;
//		try {
//			ResponseEntity<?> res = callXmlApi(rawRestTemplate, url, HttpMethod.POST, httpEntity,
//					xmlSuccessMappingClass, xmlErrorMappingClass);
//			response = (ResponseEntity<String>) res;
//		} catch (Exception e) {
//			log.error( DASHES + SEVERE_GENERIC_API_RESPONSES_EXCEPTION + DASHES);
//			log.error( SOMETHING_WENT_WRONG, e);
//			log.error( DASHES);
//		}
//		return response;
//	}

	public ResponseEntity<byte[]> callFiledownloadAPI(String url, Map<String, String> headers) {
		log.debug( "in download file method:: " + url);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

		if (headers != null) {
			Set<Entry<String, String>> headersEntry = headers.entrySet();
			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
			while (headerEntryIterator.hasNext()) {
				Entry<String, String> entry = headerEntryIterator.next();
				httpHeaders.add(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
		ResponseEntity<byte[]> response;
		try {
			response = callNonStringApi(restTemplate, url, HttpMethod.GET, entity, byte[].class);
		} catch (HttpStatusCodeException h) {
			log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
			byte[] emtyArray = new byte[0];
			ResponseEntity<byte[]> resp = new ResponseEntity<>(emtyArray, getHeadersCopy(h.getResponseHeaders()),
					h.getStatusCode());
			response = resp;
			logApi(url, HttpMethod.GET, entity, response);
			log.error( "", h);
		}
		return response;
	}

//	public ResponseEntity<byte[]> callFormAPIWithXMLDataResponseForFileDownload(String url,
//			MultiValueMap<String, Object> formValues, Map<String, String> headers, Class<?> xmlErrorMappingClass) {
//		RestTemplate rawRestTemplate = new RestTemplate();
//
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
//		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		if (headers != null) {
//			Set<Entry<String, String>> headersEntry = headers.entrySet();
//			Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
//			while (headerEntryIterator.hasNext()) {
//				Entry<String, String> entry = headerEntryIterator.next();
//				httpHeaders.add(entry.getKey(), entry.getValue());
//			}
//		}
//		HttpEntity<?> httpEntity = new HttpEntity<>(formValues, httpHeaders);
//		log.debug( httpEntity.toString());
//		ResponseEntity<byte[]> response = null;
//		try {
//			response = callXmlApi(rawRestTemplate, url, HttpMethod.POST, httpEntity, byte[].class,
//					xmlErrorMappingClass);
//		} catch (Exception e) {
//			log.error( DASHES + SEVERE_GENERIC_API_RESPONSES_EXCEPTION + DASHES);
//			log.error( SOMETHING_WENT_WRONG, e);
//			log.error( DASHES);
//		}
//		return response;
//	}

	private HttpHeaders getHeadersCopy(HttpHeaders headers) {
		HttpHeaders header = new HttpHeaders();
		Set<String> corsHeaders = getCorsHeaders();
		Set<Entry<String, List<String>>> headerEntries = headers.entrySet();
		for (Entry<String, List<String>> headerEntry : headerEntries) {
			if (!corsHeaders.contains(headerEntry.getKey())) {
				header.put(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		return header;
	}

	private Set<String> getCorsHeaders() {
		Set<String> corsHeaders = new HashSet<>();
		corsHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
		corsHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
		corsHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
		corsHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
		return corsHeaders;
	}

//	private XStreamMarshaller getXStreamMarshaller(Class<?> xmlMappingClass) {
//		XStreamMarshaller marshaller = new XStreamMarshaller();
//		marshaller.setAnnotatedClasses(xmlMappingClass);
//		return marshaller;
//	}
//
//	private <T> ResponseEntity<T> callXmlApi(RestTemplate restTemplate, String url, HttpMethod method,
//			HttpEntity<?> httpEntity, Class<?> successResponseType, Class<?> xmlErrorMappingClass) throws IOException {
//
//		ResponseEntity<T> response = null;
//		try {
//			log.debug( API_CALLING_GOING_TO_START);
//			ResponseEntity<?> resp = callNonStringApi(restTemplate, url, method, httpEntity, successResponseType);
//			if (successResponseType.isAssignableFrom(byte[].class)) {
//				response = (ResponseEntity<T>) resp;
//			} else if (resp != null) {
//				ResponseEntity<String> res = new ResponseEntity<>(new Gson().toJson(resp.getBody()),
//						getHeadersCopy(resp.getHeaders()), resp.getStatusCode());
//				response = (ResponseEntity<T>) res;
//			} else {
//				response = null;
//				log.debug( LOG_NULL_RESPONSE);
//			}
//			logApi(url, HttpMethod.POST, httpEntity, response);
//		} catch (HttpStatusCodeException h) {
//			log.info( DASHES + HTTP_STATUS_CODE_EXCEPTION + DASHES);
//			log.debug( "xmlErrorMappingClass :: " + xmlErrorMappingClass);
//			XStreamMarshaller marshaller = getXStreamMarshaller(xmlErrorMappingClass);
//			log.debug( "h BODY :: " + h.getResponseBodyAsString());
//			Object perfiosErrorResponse = marshaller.unmarshalReader(new StringReader(h.getResponseBodyAsString()));
//			log.debug( "perfiosErrorResponse object :: " + perfiosErrorResponse);
//			if (successResponseType.isAssignableFrom(byte[].class)) {
//				ResponseEntity<byte[]> resp = new ResponseEntity<>(
//						new Gson().toJson(perfiosErrorResponse, xmlErrorMappingClass).getBytes(),
//						getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
//				response = (ResponseEntity<T>) resp;
//			} else {
//				ResponseEntity<String> resp = null;
//				if (xmlErrorMappingClass.isAssignableFrom(PerfiosErrorResponse.class)) {
//					log.debug(
//							"GSON CONVERSION NON TRUSTED :: " + new Gson().toJson(perfiosErrorResponse));
//					log.debug( "HEADERS :: " + getHeadersCopy(h.getResponseHeaders()));
//					log.debug( "SATTUS CODE :: " + h.getStatusCode());
//					resp = new ResponseEntity<>(new Gson().toJson(perfiosErrorResponse),
//							getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
//				} else {
//					resp = new ResponseEntity<>(new Gson().toJson(perfiosErrorResponse, xmlErrorMappingClass),
//							getHeadersCopy(h.getResponseHeaders()), h.getStatusCode());
//				}
//				response = (ResponseEntity<T>) resp;
//			}
//			logApi(url, HttpMethod.POST, httpEntity, response);
//			log.error( "", h);
//		}
//		return response;
//	}

	private <T> ResponseEntity<T> callApi(RestTemplate restTemplate, String url, HttpMethod method,
			HttpEntity<?> httpEntity, Class<T> responseType) {
		Date date = new Date();
		Long startTime = System.currentTimeMillis();
		Long requestTime = date.getTime();
		ResponseEntity<T> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, method, httpEntity, responseType);
			return responseEntity;
		} finally {
			try {
				Long endTime = System.currentTimeMillis();
				Long responseTime = date.getTime();
				logApi(url, method, httpEntity, responseEntity);
				url = url.replaceAll(" ", "%20");
				URI uri = new URI(url);
				P2SAPILog p2sapilog = new P2SAPILog();
				p2sapilog.setApplicationName("finansme_external");
				p2sapilog.setRequestingProjectName("finansme_external");
				p2sapilog.setApiName("finansme_external");
				setTimings(p2sapilog, startTime, endTime, requestTime, responseTime);
				setResponse(p2sapilog, responseType, responseEntity);
				p2sapilog.setType(method.toString());
				p2sapilog.setRequestPayload(String.valueOf(httpEntity));
				p2sapilog.setApiVersion(url);
				p2sapilog.setHost(uri.getHost());
				p2sapilog.setApiName(url);
				p2sapilog.setQueryParams(uri.getQuery());
				p2sapilog.setBaseUri(uri.getPath());
				if (httpEntity != null) {
					//p2sapilog.setRequestHeaders(httpEntity.getHeaders());
				}
				if (responseEntity != null) {
					if (responseEntity.getStatusCodeValue() != 200 || responseEntity.getStatusCodeValue() != 201) {
						p2sapilog.setSuccess_status(false);
					} else {
						p2sapilog.setSuccess_status(true);
					}
					//p2sapilog.setResponseHeaders(responseEntity.getHeaders());
					p2sapilog.setResponsePayload(String.valueOf(responseEntity.getBody()));
				}
				persist(p2sapilog);
			} catch (Exception e) {
				log.error( "EXCEPTION IN PERSISTING THE API LOG", e);
			}
		}
	}

	private <T> ResponseEntity<T> callQueryParamApi(RestTemplate restTemplate, String url, HttpMethod method,
			HttpEntity<?> httpEntity, Class<T> responseType) {
		Date date = new Date();
		Long startTime = System.currentTimeMillis();
		Long requestTime = date.getTime();
		ResponseEntity<T> responseEntity = null;
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
					.queryParams((LinkedMultiValueMap<String, String>) httpEntity.getBody());
			UriComponents uriComponents = builder.build().encode();
			responseEntity = restTemplate.exchange(uriComponents.toUri(), method, httpEntity, responseType);
			return responseEntity;
		} finally {
			try {
				Long endTime = System.currentTimeMillis();
				Long responseTime = date.getTime();
				logApi(url, method, httpEntity, responseEntity);
				url = url.replaceAll(" ", "%20");
				String urlo = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
				URI uri = new URI(urlo);
				P2SAPILog p2sapilog = new P2SAPILog();
				p2sapilog.setApplicationName("finansme_external");
				p2sapilog.setRequestingProjectName("finansme_external");
				p2sapilog.setApiName("finansme_external");
				setTimings(p2sapilog, startTime, endTime, requestTime, responseTime);
				setResponse(p2sapilog, responseType, responseEntity);
				p2sapilog.setType(method.toString());
				p2sapilog.setRequestPayload(String.valueOf(httpEntity));
				p2sapilog.setApiVersion(url);
				p2sapilog.setHost(uri.getHost());
				p2sapilog.setApiName(url);
				p2sapilog.setQueryParams(uri.getQuery());
				p2sapilog.setBaseUri(uri.getPath());
				if (httpEntity != null) {
					//p2sapilog.setRequestHeaders(httpEntity.getHeaders());
				}
				if (responseEntity != null) {
					if (responseEntity.getStatusCodeValue() != 200 || responseEntity.getStatusCodeValue() != 201) {
						p2sapilog.setSuccess_status(false);
					} else {
						p2sapilog.setSuccess_status(true);
					}
					//p2sapilog.setResponseHeaders(responseEntity.getHeaders());
					p2sapilog.setResponsePayload(String.valueOf(responseEntity.getBody()));
				}
				persist(p2sapilog);
			} catch (Exception e) {
				log.error( "EXCEPTION IN PERSISTING THE API LOG", e);
			}
		}
	}

	private <T> ResponseEntity<T> callNonStringApi(RestTemplate restTemplate, String url, HttpMethod method,
			HttpEntity<?> httpEntity, Class<T> responseType) {
		ResponseEntity<T> responseEntity = null;
		if (responseType.isAssignableFrom(String.class)) {
			Gson gson = new Gson();
			ResponseEntity<Object> res = callApi(restTemplate, url, method, httpEntity, Object.class);
			if (res != null) {
				ResponseEntity<String> resp = new ResponseEntity<>(gson.toJson(res.getBody()),
						getHeadersCopy(res.getHeaders()), res.getStatusCode());
				responseEntity = (ResponseEntity<T>) resp;
			} else {
				responseEntity = null;
				log.debug( LOG_NULL_RESPONSE);
			}
		} else {
			responseEntity = callApi(restTemplate, url, method, httpEntity, responseType);
		}
		return responseEntity;
	}

	private <T> ResponseEntity<T> callNonStringQueryParamApi(RestTemplate restTemplate, String url, HttpMethod method,
			HttpEntity<?> httpEntity, Class<T> responseType) {
		ResponseEntity<T> responseEntity = null;
		if (responseType.isAssignableFrom(String.class)) {
			Gson gson = new Gson();
			ResponseEntity<Object> res = callQueryParamApi(restTemplate, url, method, httpEntity, Object.class);
			if (res != null) {
				ResponseEntity<String> resp = new ResponseEntity<>(gson.toJson(res.getBody()),
						getHeadersCopy(res.getHeaders()), res.getStatusCode());
				responseEntity = (ResponseEntity<T>) resp;
			} else {
				responseEntity = null;
				log.debug( LOG_NULL_RESPONSE);
			}
		} else {
			responseEntity = callQueryParamApi(restTemplate, url, method, httpEntity, responseType);
		}
		return responseEntity;
	}

	private void setTimings(final P2SAPILog p2sapilog, long startTime, long endTime, long requestTime,
			long responseTime) {
		p2sapilog.setReqStartTimeInMillis(startTime);
		p2sapilog.setReqEndTimeInMillis(endTime);
		p2sapilog.setRequestedTime(new Timestamp(requestTime));
		p2sapilog.setRespondTime(new Timestamp(responseTime));
	}

	private void setResponse(final P2SAPILog p2sapilog, Class<?> responseType, ResponseEntity<?> responseEntity) {
		Object responsePayload = null;
		if (responseEntity != null && responseEntity.getBody() != null) {
			if (responseType == String.class) {
				if (String.valueOf(responseEntity.getBody()).startsWith("[")) {
					responsePayload = new JSONArray(String.valueOf(responseEntity.getBody()));
				} else if (String.valueOf(responseEntity.getBody()).startsWith("{")) {
					responsePayload = new JSONObject(String.valueOf(responseEntity.getBody()));
				}
			} else {
				responsePayload = new JSONObject(responseEntity.getBody());
			}
			p2sapilog.setResponsePayload(String.valueOf(responsePayload));
		}
		if (responsePayload instanceof JSONObject) {
			p2sapilog.setSuccess_status(getSuccessStatusFromResponse(((JSONObject) responsePayload).toString()));
		} else {
			p2sapilog.setSuccess_status(true);
		}
	}

	public boolean getSuccessStatusFromResponse(final String responsePayload) {

		boolean successStat = false;
		if (responsePayload == null || responsePayload == "") {
			successStat = false;
		} else {
			if (responsePayload.startsWith("{") || responsePayload.startsWith("<") || responsePayload.startsWith("h")) {
				successStat = true;
			} else {
				successStat = false;
			}

		}
		return successStat;
	}

	private void logApi(String url, HttpMethod method, HttpEntity<?> httpEntity, ResponseEntity<?> responseEntity) {
		log.debug( String.valueOf(LINE_SEPARATOR + DASHES + "URL" + DASHES));
		log.debug( String.valueOf(LINE_SEPARATOR + url));
		log.debug( String.valueOf(LINE_SEPARATOR + DASHES + "REQUEST METHOD" + DASHES));
		log.debug( String.valueOf(LINE_SEPARATOR + method));
		if (httpEntity != null) {
			log.debug( String.valueOf(LINE_SEPARATOR + DASHES + "REQUEST HEADERS" + DASHES));
			log.debug( String.valueOf(LINE_SEPARATOR + httpEntity.getHeaders()));
			log.debug( String.valueOf(LINE_SEPARATOR + DASHES + "REQUEST BODY" + DASHES));
			log.debug( String.valueOf(LINE_SEPARATOR + httpEntity.getBody()));
		}
		if (responseEntity != null) {
			log.debug( String.valueOf(LINE_SEPARATOR + DASHES + "RESPONSE BODY" + DASHES));
			log.debug( String.valueOf(LINE_SEPARATOR + responseEntity.getBody()));
			log.debug( String.valueOf(LINE_SEPARATOR + DASHES + "RESPONSE HEADERS" + DASHES));
			log.debug( String.valueOf(LINE_SEPARATOR + responseEntity.getHeaders()));
		} else {
			log.debug( String.valueOf(LINE_SEPARATOR));
		}
	}

	private void persist(final P2SAPILog p2sapiLog) {
		try {
			if (!env.acceptsProfiles("local")) {
				p2sApiServices.sendJsonToActiveMQ(p2sapiLog);
			} else {
				log.info(
						"NOT SENDING FSME REST TEMPLATE LOG TO ACTIVE MQ BECAUSE ACTIVE PROFILE IS LOCAL");
			}
		} catch (Exception e) {
			log.error( "API DB LOGGING ERROR FOR " + p2sapiLog, e);
		}
	}

	public ResponseEntity<String> callFileUpload(String url, Map<String, String> headers,
			LinkedMultiValueMap<String, Object> map) {
		Date date = new Date();
		Long startTime = System.currentTimeMillis();
		Long requestTime = date.getTime();
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = null;
		ResponseEntity<String> response = null;
		try {
			HttpHeaders httpheaders = new HttpHeaders();
			httpheaders.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

			if (headers != null) {
				Set<Entry<String, String>> headersEntry = headers.entrySet();
				Iterator<Entry<String, String>> headerEntryIterator = headersEntry.iterator();
				while (headerEntryIterator.hasNext()) {
					Entry<String, String> entry = headerEntryIterator.next();
					httpheaders.add(entry.getKey(), entry.getValue());
				}
			}

			requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, httpheaders);
			RestTemplate restTemplate = new RestTemplate();
			String responseStr = restTemplate.postForObject(url, requestEntity, String.class);
			response = new ResponseEntity<String>(responseStr, HttpStatus.CREATED);
			return response;
		} finally {
			try {
				Long endTime = System.currentTimeMillis();
				Long responseTime = date.getTime();
				logApi(url, HttpMethod.POST, requestEntity, response);
				url = url.replaceAll(" ", "%20");
				String urlo = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
				URI uri = new URI(urlo);
				P2SAPILog p2sapilog = new P2SAPILog();
				p2sapilog.setApplicationName("finansme_external");
				p2sapilog.setRequestingProjectName("finansme_external");
				p2sapilog.setApiName("finansme_external");
				setTimings(p2sapilog, startTime, endTime, requestTime, responseTime);
				setResponse(p2sapilog, String.class, response);
				p2sapilog.setType(HttpMethod.POST.toString());
				p2sapilog.setRequestPayload(String.valueOf(requestEntity));
				p2sapilog.setApiVersion(url);
				p2sapilog.setHost(uri.getHost());
				p2sapilog.setApiName(url);
				p2sapilog.setQueryParams(uri.getQuery());
				p2sapilog.setBaseUri(uri.getPath());
				if (requestEntity != null) {
					//p2sapilog.setRequestHeaders(requestEntity.getHeaders());
				}
				if (response != null) {
					if (response.getStatusCodeValue() != 200 || response.getStatusCodeValue() != 201) {
						p2sapilog.setSuccess_status(false);
					} else {
						p2sapilog.setSuccess_status(true);
					}
					//p2sapilog.setResponseHeaders(response.getHeaders());
					p2sapilog.setResponsePayload(String.valueOf(response.getBody()));
				}
				persist(p2sapilog);
			} catch (Exception e) {
				log.error( "EXCEPTION IN PERSISTING THE API LOG", e);
			}
		}

	}
}
