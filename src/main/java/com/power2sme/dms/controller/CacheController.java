package com.power2sme.dms.controller;

import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.power2sme.dms.externaldto.ResponseDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Cache Handler")
@RequestMapping(value = { "/api/v1/cache", "/api/v2/cache" })
public interface CacheController {

	@RequestMapping(value = "refresh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Refresh cache", notes = "Refresh data stored in cache.")
	ResponseDto refreshCache();
}
