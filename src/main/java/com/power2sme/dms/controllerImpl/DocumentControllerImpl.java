package com.power2sme.dms.controllerImpl;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.power2sme.dms.config.properties.ApiMessageConfiguration;
import com.power2sme.dms.config.properties.ErrorCodeConfiguration;
import com.power2sme.dms.controller.DocumentController;
import com.power2sme.dms.entity.AccessibleTo;
import com.power2sme.dms.entity.DocumentWrapper;
import com.power2sme.dms.entity.FileRequestDto;
import com.power2sme.dms.entity.GetDocumentsRequestDto;
import com.power2sme.dms.entity.SmeIdMap;
import com.power2sme.dms.entity.SystemIdentity;
import com.power2sme.dms.entity.ZipFileWrapper;
import com.power2sme.dms.entity.ZipWrapper;
import com.power2sme.dms.externaldto.DocumentTypeDto;
import com.power2sme.dms.externaldto.ResponseDto;
import com.power2sme.dms.externaldto.SearchFilterDto;
import com.power2sme.dms.service.DocumentService;
import com.power2sme.dms.serviceImpl.DownloadZipService;

@RestController
public class DocumentControllerImpl implements DocumentController {

	Logger logger = LoggerFactory.getLogger(DocumentControllerImpl.class);

	@Autowired
	DocumentService documentService;

	@Autowired
	ServletContext context;

	@Autowired
	HttpServletRequest request;

	@Autowired
	HttpServletResponse response;

	@Autowired
	ErrorCodeConfiguration errorCodeConfiguration;

	@Autowired
	ApiMessageConfiguration apiMessageConfiguration;

	@Autowired
	DownloadZipService downloadZipService;

	@Override
	public ResponseDto getDocuments(@RequestParam(value = "smeId", required = true) final String smeId,
			@RequestParam(value = "docTypeId", required = false) final Integer docTypeId,
			@RequestParam(value = "docId", required = false) final Integer docId,
			@RequestParam(value = "versionNo", required = false, defaultValue = "") final Integer versionNo,
			@RequestParam(value = "systemName", required = true) final String systemName) throws Exception {
		ResponseDto result = documentService.getDocuments(smeId, docTypeId, docId, versionNo, systemName);
		return result;
	}

	@Override
	public ResponseDto uploadDocument(@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "data", required = false) Integer data) {
		if (!file.isEmpty()) {
			FileRequestDto dto = null;
			if (data != null) {
				dto = new FileRequestDto();
				dto.setTimeToLive(data);
			}
			ResponseDto result = documentService.uploadDocument(file, dto, request, context);
			return result;
		} else {
			ResponseDto rdto = new ResponseDto();
			rdto.setData(null);
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setMessage("Upload valid file");
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}
	}

	@Override
	public ResponseDto updateDocument(@RequestBody DocumentWrapper documentWrapper) throws Exception {
		logger.debug("Recieved request for updating documents with params:  " + documentWrapper.toString());
		ResponseDto result = documentService.updateDocument(documentWrapper);
		return result;
	}

	@Override
	public ResponseDto updateSme(@RequestBody SmeIdMap smeIdMap) {
		return documentService.updateSme(smeIdMap);

	}

	@Override
	public ResponseDto getDocumentType(@RequestParam(value = "systemName", required = true) String systemName) {
		return documentService.getDocumentType(systemName);
	}

	@Override
	public void downloadFile(@RequestParam(value = "fileId", required = true) String fileId) {
		documentService.downloadFile(fileId, response);
	}

	@Override
	public void zipFiles(@RequestBody ZipWrapper zipWrapper) {
		documentService.zipFiles(zipWrapper, response);
	}

	@Override
	public void zipfileByDocType(@RequestBody ZipFileWrapper zipFileWrapper) {
		documentService.zipfileByDocType(zipFileWrapper, response);
	}

	@Override
	public ResponseDto fetchDocuments(@RequestBody DocumentWrapper docRequestWrapper) {
		return documentService.getDocuments(docRequestWrapper);

	}

	@Override
	public ResponseDto fileMetaData(@RequestParam(value = "fileId", required = true) String fileId) {
		return documentService.getFileMetaData(fileId);

	}

	@Override
	public ResponseDto addSystemDetails(@RequestBody SystemIdentity identity) {
		return documentService.saveSystemDetail(identity);
	}

	@Override
	public ResponseDto addDocumentType(@RequestBody DocumentTypeDto documentType) {
		return documentService.saveDocumentType(documentType);
	}

	@Override
	public ResponseDto updateSystemDetails(@RequestBody DocumentTypeDto documentType) {
		return documentService.updateDocumentType(documentType);
	}

	@Override
	public ResponseDto addAccessibilityMapping(@RequestBody AccessibleTo details) {
		return documentService.addAccessibilityMapping(details);
	}

	@Override
	public ResponseDto updateSystemDetails(@RequestBody SystemIdentity identity) {
		return documentService.updateSystemDetail(identity);
	}

	@Override
	public ResponseDto getCategories() {
		return documentService.getCategories();
	}

	@Override
	public ResponseDto search(@RequestBody SearchFilterDto searchFilter) {
		return documentService.search(searchFilter);
	}

	@Override
	public ResponseDto getDocuments(@RequestParam(value = "smeId", required = true) final String smeId,
			@RequestParam(value = "systemName", required = true) final String systemName) throws Exception {
		return documentService.getDocuments(smeId, systemName);

	}

	@Override
	public ResponseDto getLosDocumentType(String systemName) {
		return documentService.getLosDocumentType(systemName);
	}

	@Override
	public ResponseDto getDocuments(@RequestBody GetDocumentsRequestDto requestDto) {
		return documentService.getDocuments(requestDto);
	}

	@Override
	public ResponseDto getDefaultDocuments(@RequestParam(value = "systemId", required = true) Integer systemId) {
		return documentService.getDefaultDocuments(systemId);
	}

	@Override
	public String mergeDocs(@RequestParam(value = "smeId", required = false) String smeId,
			@RequestParam("targetDocTypeId") Integer targetDocTypeId,
			@RequestParam("srcDocTypeIds") List<Integer> srcDocTypeIds) {
		return documentService.mergeDocs(smeId, targetDocTypeId, srcDocTypeIds);
	}

	@Override
	public ResponseDto getDocMetadat(@RequestParam("smeId") String smeId) {
		return documentService.getDocMetadat(smeId);
	}

	@Override
	public List<ResponseDto> copyDocuments(@RequestParam("sourceSmeId") List<String> sourceSmeId,
			@RequestParam("targetSmeId") List<String> targetSmeId, @RequestParam("systemName") String systemName)
			throws Exception {
		return documentService.copyDocuments(sourceSmeId, targetSmeId, systemName);
	}
}
