package com.power2sme.dms.dao;

import java.util.List;

import com.power2sme.dms.entity.*;
import org.springframework.stereotype.Repository;

import com.power2sme.dms.entity.AccessibleTo;
import com.power2sme.dms.entity.Category;
import com.power2sme.dms.entity.Comments;
import com.power2sme.dms.entity.DigitizedInfo;
import com.power2sme.dms.entity.Document;
import com.power2sme.dms.entity.DocumentInfoResponse;
import com.power2sme.dms.entity.DocumentType;
import com.power2sme.dms.entity.File;
import com.power2sme.dms.entity.GetDocumentsRequestListItem;
import com.power2sme.dms.entity.LosDocumentType;
import com.power2sme.dms.entity.SearchResult;
import com.power2sme.dms.entity.SystemIdentity;
import com.power2sme.dms.entity.Verification;
import com.power2sme.dms.entity.ZipDocumentsId;
import com.power2sme.dms.externaldto.DocumentTypeDto;
import com.power2sme.dms.externaldto.SearchFilterDto;
import com.power2sme.dms.metadata.FetchDocumentsViewEntity;

@Repository
public interface DocumentDao {

	List<FetchDocumentsViewEntity> findAllBySmeIdAndSystemId(String smeId, Integer systemId);
	
	List<FetchDocumentsViewEntity> findAllBySmeIdAndSystemIdAndDocTypeIdIn(String smeId, Integer systemId, List<GetDocumentsRequestListItem> getDocumentsRequestListItem);
	
//	List<DocTypeListProjection> getDocTypeBySystemId(Integer systemId);
	
	
	List<DocumentType> getDocumentType(Integer systemId);
	
	List<LosDocumentType> getLosDocumentType(Integer systemId);

	List<Document> getAllDocumentsByDocTypeAndSmeId(Integer documentTypeId, String smeId);

	List<Comments> getComments(String smeId, Integer documentTypeId, Integer documentId);

	List<File> getFiles(Integer parentId);

	List<DigitizedInfo> getDigitalInfo(Integer parentId);

	Verification getVerification(Integer parentId);

	List<SystemIdentity> getSystemDetail();

	List<SystemIdentity> getSystemDetailById(int id);

	Integer updateSmeIdInDocuments(String smeId, String uniqueId) throws Exception;

	Integer updateSmeIdInComments(String smeId, String uniqueId) throws Exception;

	List<Document> getDocumentId(Integer documentTypeId, String smeId);

	void createNewEntryForDocument(String smeId, Integer docTypeId, Integer systemId, Document document);

	Document getDocumentLatestDocument(Document document);

	void doUpdationForDocument(String smeId, Document documentfetched, Document document, Integer systemId,
			Integer docTypeId);

	Integer getLatestDocumentId(Integer docTypeId, String smeId);

	List<DocumentType> getDocumentTypes();

	List<DocumentType> getDocumentTypeById(int id);

	List<Category> getCategoryDetail();

	boolean addAccessibilityMapping(AccessibleTo mapping);

	List<Document> getParentId(ZipDocumentsId zipDocumentsId, String smeId);

	Integer saveFileInfo(File file);

	File getFileInfo(String fileId);

	SystemIdentity saveSystemDetail(SystemIdentity details);

	Integer updateSystemDetail(SystemIdentity details);

	DocumentTypeDto saveDocumentType(DocumentTypeDto documentType);

	DocumentTypeDto updateDocumentType(DocumentTypeDto documentType);

	List<DocumentType> getDocumentTypeByName(String docTypeName);

	List<DigitizedInfo> getDigitalInfoMapper(Integer docTypeId);

	boolean isAccessabletoSystem(Integer systemId, Integer documentTypeId);

	boolean isFileUploadedInDms(String fileId);

	boolean doesDocumentExist(String smeId, Integer documentId, Integer versionNo, Integer docTypeId);

	List<SearchResult> search(SearchFilterDto searchFilter);

	Integer getSearchResultCount(SearchFilterDto searchFilter);

	List<DocumentInfoResponse> getDocumentInfo(String smeId, Integer systemId);

	List<String> getCorsAllowedOrigins();

	List<DocumentsEntity> getDocumentsList(ZipFileDocuments zipFileDocuments, String smeId) throws Exception;

}
