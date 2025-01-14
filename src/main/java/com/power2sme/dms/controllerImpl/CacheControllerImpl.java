package com.power2sme.dms.controllerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.power2sme.dms.controller.CacheController;
import com.power2sme.dms.externaldto.ResponseDto;
import com.power2sme.dms.service.DocumentService;

@RestController
public class CacheControllerImpl implements CacheController {

	@Autowired
	private DocumentService docService;

	@Override
	public ResponseDto refreshCache() {
		return docService.refreshCache();
	}
}
