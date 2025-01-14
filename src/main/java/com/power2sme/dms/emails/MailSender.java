package com.power2sme.dms.emails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.power2sme.dms.utils.FsmeRestUtils;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class MailSender {
	
	@Autowired
	private FsmeRestUtils fsmeRestUtils;
	
	@Autowired
	private MMMConfiguration mmmConfiguration;

	
	public String callPostMQService(String payload) {
		
			String url = mmmConfiguration.getBaseUrl();
			log.debug("URL :: " + url);
			payload = payload.replaceAll("&", "&amp\\;");
			MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
			formData.add("method", "enqueue");
			formData.add("payload", payload);
			log.debug("URL + PAYLOAD :: " + url + payload);

			ResponseEntity<String> response =  fsmeRestUtils.callFormAPIWithStringResponse(url, formData, null);
			String output = response.getBody();		
			log.debug("MQ Response status = " + response.getStatusCodeValue());
			log.debug("MQ Response text = " + output);
			return output;
	}
}
