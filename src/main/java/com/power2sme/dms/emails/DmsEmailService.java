package com.power2sme.dms.emails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.power2sme.dms.externaldto.ZipDocumentResponseDto;
import com.power2sme.dms.utils.FsmeJsonUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DmsEmailService {
	
	@Autowired
	MailSender mailSender;
	
	public void sendMailOnZippingCompleted(ZipDocumentResponseDto responseDto) throws Exception {
		log.debug("sendMailOnZippingCompleted:: " + responseDto);
		
		OnZippingCompletedEmailData.OnZippingCompletedEmailDataBuilder payloadBuilder = OnZippingCompletedEmailData.builder();

		OnZippingCompletedEmailData onZippingCompletedEmailData = payloadBuilder
				.object("dms")
				.event("zip_download")
				.smeId(responseDto.getSmeId())
				.mailTo(EmailIdDto.builder().emailId(responseDto.getEmailId()).build())
				.link(responseDto.getZipLocation())
				.build();

		sendEmailByModelPayload(onZippingCompletedEmailData);
	}
	
	
	/////////////////////////////////////
	public String sendEmailByModelPayload(Object payloadObj) throws JsonProcessingException {
		log.debug( "Inside sendEmail:: " + payloadObj);
		String emailPayloadJsonStr = FsmeJsonUtil.getXmlStr(payloadObj);
		log.info("emailPayloadJsonStr::" + emailPayloadJsonStr);
		String res = mailSender.callPostMQService(emailPayloadJsonStr);
		log.debug( "sendEmail() response:: " + res);
		return res;
	}

	public static String getXmlStr(Object obj) throws JsonProcessingException
	{
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.writeValueAsString(obj);
	}
}
