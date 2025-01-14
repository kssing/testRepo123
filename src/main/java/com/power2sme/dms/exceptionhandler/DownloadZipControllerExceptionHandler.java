package com.power2sme.dms.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.power2sme.dms.common.RestApiError;
import com.power2sme.dms.utils.DmsLogUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice(basePackages = "com.power2sme.dms")
public class DownloadZipControllerExceptionHandler extends ResponseEntityExceptionHandler {

	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<RestApiError> handleInvalidRoleForEmployeeException(Exception e) {
		DmsLogUtil.logAtDebug(log, e);
		return new ResponseEntity<>(new RestApiError(HttpStatus.BAD_REQUEST.value(), e), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<RestApiError> handleRuntimeException(Throwable e) {
		DmsLogUtil.logAtDebug(log, e);
		return new ResponseEntity<>(new RestApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
