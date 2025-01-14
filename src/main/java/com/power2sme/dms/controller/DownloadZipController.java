package com.power2sme.dms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.power2sme.dms.entity.DownloadDocumentRequestDto;
import com.power2sme.dms.entity.GetDocumentsRequestDto;
import com.power2sme.dms.externaldto.ZippingResponseDto;
import com.power2sme.dms.serviceImpl.DownloadZipService;

@RestController
@RequestMapping(value = {"/api/v1/", "/api/v2"})
public class DownloadZipController {
	private final DownloadZipService downloadZipService;
	
	private final HttpServletRequest httpServletRequest;
	
	@Autowired
	public  DownloadZipController(DownloadZipService downloadZipService,HttpServletRequest httpServletRequest) {
		this.downloadZipService=downloadZipService;
		this.httpServletRequest = httpServletRequest;
	}
	
	/**
	 * Old download all doc api for a customer, this api downloading only latest version of documents of customer.
	 * @param smeId
	 * @param systemName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="zipurl", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public String zipUrl(@RequestParam String smeId , @RequestParam String systemName ) throws Exception {
		return downloadZipService.zipUrl(smeId, systemName,httpServletRequest);
	}
	
	/**
	 * Updated download all api to get async behaviour, update zip events removed when new documents added in same customer.
	 * @param smeId
	 * @param systemName
	 * @param emailId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="zipdocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public ZippingResponseDto zipDocuments(@RequestParam("sme_id") String smeId , @RequestParam("system_name") String systemName, @RequestParam("email_id") String emailId) throws Exception {
		return downloadZipService.zipDocuments(smeId, systemName,emailId,httpServletRequest);
	}
	
	@RequestMapping(value="downloadDocuments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public ZippingResponseDto downloadDocuments(@Valid @RequestBody DownloadDocumentRequestDto requestDto) throws Exception {
		return downloadZipService.downloadDocuments(requestDto,httpServletRequest);
	}
}
