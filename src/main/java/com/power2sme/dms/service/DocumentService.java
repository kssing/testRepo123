package com.power2sme.dms.service;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

/**
 * 
 * @author sunil.kunwar@power2sme.com
 *
 */
@Service
public interface DocumentService {

	ResponseDto getDocuments(String smeId, Integer docTypeId, Integer docId, Integer versionNo, String systemName)
			throws Exception;

	ResponseDto uploadDocument(MultipartFile file, FileRequestDto data, HttpServletRequest request,
			ServletContext context);

	ResponseDto updateSme(SmeIdMap smeIdMap);

	ResponseDto updateDocument(DocumentWrapper documentWrapper);

	ResponseDto getDocumentType(String systemName);

	ResponseDto getLosDocumentType(String systemName);

	void downloadFile(String fileId, HttpServletResponse response);

	void zipFiles(ZipWrapper zipWrapper, HttpServletResponse response);

	void zipfileByDocType(ZipFileWrapper zipFileWrapper, HttpServletResponse response);

	ResponseDto getDocuments(DocumentWrapper docRequestWrapper);

	ResponseDto getFileMetaData(String fileId);

	ResponseDto saveSystemDetail(SystemIdentity details);

	ResponseDto updateSystemDetail(SystemIdentity details);

	ResponseDto saveDocumentType(DocumentTypeDto documentType);

	ResponseDto updateDocumentType(DocumentTypeDto documentType);

	ResponseDto addAccessibilityMapping(AccessibleTo mapping);

	ResponseDto getCategories();

	ResponseDto search(SearchFilterDto searchFilter);

	ResponseDto getDocuments(String smeId, String systemName) throws Exception;

	List<String> getCorsAllowedOrigin();

	ResponseDto refreshCache();

	ResponseDto getDocuments(GetDocumentsRequestDto requestDto);

	ResponseDto getDefaultDocuments(Integer systemId);

	String mergeDocs(String smeId, Integer targetDocTypeId, List<Integer> srcDocTypeIds);

	ResponseDto getDocMetadat(String smeId);

	List<ResponseDto> copyDocuments(List<String> sourceSmeId, List<String> targetSmeId, String systemName)
			throws Exception;

}
