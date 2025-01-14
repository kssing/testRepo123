package com.power2sme.dms.serviceImpl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.power2sme.dms.config.properties.ApiMessageConfiguration;
import com.power2sme.dms.config.properties.ErrorCodeConfiguration;
import com.power2sme.dms.config.properties.FileExtensionConfiguration;
import com.power2sme.dms.config.properties.FilePathConfiguration;
import com.power2sme.dms.dao.DocumentDao;
import com.power2sme.dms.entity.AccessibleTo;
import com.power2sme.dms.entity.Category;
import com.power2sme.dms.entity.Comments;
import com.power2sme.dms.entity.DigitizedInfo;
import com.power2sme.dms.entity.Document;
import com.power2sme.dms.entity.DocumentInfoResponse;
import com.power2sme.dms.entity.DocumentType;
import com.power2sme.dms.entity.DocumentWrapper;
import com.power2sme.dms.entity.DocumentsEntity;
import com.power2sme.dms.entity.DocumentsMetadataListItemDto;
import com.power2sme.dms.entity.File;
import com.power2sme.dms.entity.FileRequestDto;
import com.power2sme.dms.entity.GetDocumentsRequestDto;
import com.power2sme.dms.entity.LosDocumentType;
import com.power2sme.dms.entity.SearchResult;
import com.power2sme.dms.entity.SmeIdMap;
import com.power2sme.dms.entity.SystemIdentity;
import com.power2sme.dms.entity.Verification;
import com.power2sme.dms.entity.Version;
import com.power2sme.dms.entity.ZipDocumentsId;
import com.power2sme.dms.entity.ZipFileDocuments;
import com.power2sme.dms.entity.ZipFileWrapper;
import com.power2sme.dms.entity.ZipWrapper;
import com.power2sme.dms.externaldto.DocumentTypeDto;
import com.power2sme.dms.externaldto.ResponseDto;
import com.power2sme.dms.externaldto.SearchFilterDto;
import com.power2sme.dms.metadata.FetchDocumentsViewEntity;
import com.power2sme.dms.metadata.FetchDocumentsViewRepository;
import com.power2sme.dms.metadata.FetchDocumentsViewRepository.DocTypeListProjection;
import com.power2sme.dms.metadata.FetchDocumentsViewRepository.DocumentsMetadataProjection;
import com.power2sme.dms.metadata.MergedocSmeIdEntity;
import com.power2sme.dms.metadata.MergedocSmeIdRepository;
import com.power2sme.dms.metadata.MetadataEntity;
import com.power2sme.dms.metadata.MetadataRepository;
import com.power2sme.dms.service.DocumentService;
import com.power2sme.dms.utils.DateUtility;
import com.power2sme.dms.utils.DmsLogUtil;
import com.power2sme.dms.utils.PropertyUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {

	private static Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Autowired
	DocumentDao documentDao;

	@Autowired
	ErrorCodeConfiguration errorCodeConfiguration;

	@Autowired
	ApiMessageConfiguration apiMessageConfiguration;

	@Autowired
	FilePathConfiguration filePathConfiguration;

	@Autowired
	FileExtensionConfiguration fileExtensionConfiguration;

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	FetchDocumentsViewRepository fetchDocumentsViewRepository;

	@Autowired
	MergedocSmeIdRepository mergedocSmeIdRepository;

	private Map<String, Integer> systemNameIdMap = new HashMap<>();
	private Map<Integer, String> systemIdNameMap = new HashMap<>();

	private Map<String, Integer> documentNameIdMap = new HashMap<>();
	private Map<Integer, String> documentIdNameMap = new HashMap<>();

	private Map<String, Integer> categoryNameIdMap = new ConcurrentHashMap<>();
	private Map<Integer, String> categoryIdNameMap = new ConcurrentHashMap<>();

	private List<String> allowedOriginsList = new ArrayList<String>();

	@PostConstruct
	public void initialize() {
		this.loadDataInCache(false);
	}

	private ResponseDto loadDataInCache(boolean isCacheRefresh) {
		logger.debug("Loading data in cache.");
		ResponseDto rdto = new ResponseDto();
		try {
			loadAllSystemDetails();
			loadAllDocumentTypeDetails();
			loadAllCategories();
			loadAllAllowedOrigins();
			if (isCacheRefresh) {
				Map<String, String> data = new HashMap<String, String>();
				data.put("systemNameIdMap", systemNameIdMap.toString());
				data.put("systemIdNameMap", systemIdNameMap.toString());
				data.put("documentNameIdMap", documentNameIdMap.toString());
				data.put("documentIdNameMap", documentIdNameMap.toString());
				data.put("categoryNameIdMap", categoryNameIdMap.toString());
				data.put("categoryIdNameMap", categoryIdNameMap.toString());
				data.put("allowedOriginsList", allowedOriginsList.toString());
				rdto.setData(data);
			}
			rdto.setErrorCode(0);
			rdto.setMessage("Success");
			rdto.setStatus(apiMessageConfiguration.getSuccess());
			logger.debug("Data loaded in cache successfully.");
		} catch (Exception e) {
			logger.debug("Error in loading data in cache: " + e.getMessage());
			logger.error("Error in initialize:", e);
			rdto.setErrorCode(errorCodeConfiguration.getInternalserver());
			rdto.setMessage("Error in refreshing cache: " + e.getMessage());
			rdto.setStatus(apiMessageConfiguration.getFailed());
		}
		return rdto;
	}

	private void loadAllSystemDetails() {
		List<SystemIdentity> ls = documentDao.getSystemDetail();
		for (SystemIdentity si : ls) {
			systemNameIdMap.put(si.getSystemName().toLowerCase(), si.getSystemId());
			systemIdNameMap.put(si.getSystemId(), si.getSystemName().toLowerCase());

		}
	}

	private void loadAllDocumentTypeDetails() {
		List<DocumentType> ls = documentDao.getDocumentTypes();
		for (DocumentType dt : ls) {
			documentNameIdMap.put(dt.getDocumentTypeName(), dt.getDocumentTypeId());
			documentIdNameMap.put(dt.getDocumentTypeId(), dt.getDocumentTypeName());

		}
	}

	private void loadAllCategories() {
		List<Category> ls = documentDao.getCategoryDetail();
		for (Category cat : ls) {
			categoryNameIdMap.put(cat.getCategoryName().toLowerCase(), cat.getCategoryId());
			categoryIdNameMap.put(cat.getCategoryId(), cat.getCategoryName().toLowerCase());

		}
	}

	private void loadAllAllowedOrigins() {
		allowedOriginsList = documentDao.getCorsAllowedOrigins();
	}

	public ResponseDto getDocuments(String smeId, Integer docTypeId, Integer docId, Integer versionNo,
			String systemName) throws Exception {
		logger.debug("Sme id in get documents: " + smeId);
		logger.debug("System Name in get documents: " + systemName);
		ResponseDto rdto = new ResponseDto();
		int count = 1;
		if (smeId == null || smeId.equalsIgnoreCase("")) {
			rdto.setMessage("SME Id and can't be null or blank");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}
		if (systemName.equalsIgnoreCase("") || systemName == null) {
			rdto.setMessage("System Name can't be null or blank");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}
		if (docTypeId == null && (docId != null || versionNo != null)) {
			rdto.setMessage("Please enter valid Document type Id for this Document Id");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}

		if (docTypeId != null && docId == null && versionNo != null) {
			rdto.setMessage("Please enter valid Document Id for this Document Type Id");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}

		DocumentWrapper documentWrapper = new DocumentWrapper();
		List<DocumentType> documentTypeList = documentDao
				.getDocumentType(systemNameIdMap.get(systemName.toLowerCase()));
		if (documentTypeList.isEmpty()) {
			rdto.setMessage("Please enter valid system name");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}
		List<DocumentType> doc_type = new ArrayList<DocumentType>();

		if (docTypeId != null) {
			logger.debug("Specific Document Type Id in get documents: " + docTypeId);
			for (int i = 0; i < documentTypeList.size(); i++) {
				if (docTypeId.equals(documentTypeList.get(i).getDocumentTypeId())) {
					doc_type.add(documentTypeList.get(i));
					break;
				}
			}
			if (doc_type.isEmpty()) {
				rdto.setMessage("Invalid Document Type Id");
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setData(null);
				rdto.setStatus(apiMessageConfiguration.getFailed());
				return rdto;
			}
		} else {
			doc_type.addAll(documentTypeList);
		}

		for (int i = 0; i < doc_type.size(); i++) {
			List<Document> documents = documentDao.getDocumentId(doc_type.get(i).getDocumentTypeId(), smeId);
			List<Document> documentVersionList = documentDao
					.getAllDocumentsByDocTypeAndSmeId(doc_type.get(i).getDocumentTypeId(), smeId);
			List<Document> documentList = new ArrayList<Document>();

			if (docId != null) {
				logger.debug("Specific Document Id in get documents: " + docId);
				for (int j = 0; j < documents.size(); j++) {
					if (docId.equals(documents.get(j).getDocumentId())) {
						documentList.add(documents.get(j));
						break;
					}
				}
				if (documentList.isEmpty()) {
					rdto.setMessage("Invalid Document Id");
					rdto.setErrorCode(errorCodeConfiguration.getInputerror());
					rdto.setData(null);
					rdto.setStatus(apiMessageConfiguration.getFailed());
					return rdto;
				}
			} else {
				documentList.addAll(documents);
			}
			/*
			 * when document list is empty for document type then returning the skeleton of
			 * document list
			 */
			if (documentList.isEmpty()) {
				Document document = new Document();
				Version version = new Version();
				List<Version> versionList = new ArrayList<Version>();

				List<DigitizedInfo> digitizedInfoList = new ArrayList<DigitizedInfo>();

				List<File> files = new ArrayList<File>();

				Verification verification = new Verification();

				List<Comments> commentList = new ArrayList<Comments>();
				digitizedInfoList = documentDao.getDigitalInfoMapper(doc_type.get(i).getDocumentTypeId());

				verification.setSystemName("");
				verification.setUpdatedBy("");
				verification.setStatus("");

				version.setVersionNo(0);
				version.setFiles(files);
				version.setDigitalInfo(digitizedInfoList);
				version.setVerification(verification);
				version.setCreatedOn(Date.valueOf("2017-07-26"));
				versionList.add(version);

				document.setDocumentId(0);
				document.setVersionList(versionList);
				document.setComments(commentList);
				document.setSystemId(0);
				documentList.add(document);
				doc_type.get(i).setDocumentList(documentList);

			} else {
				/*
				 * Returning document of specific version
				 */
				if (versionNo != null) {
					logger.debug("Specific Version No in get documents: " + versionNo);
					for (int j = 0; j < documentList.size(); j++) {
						List<Version> version = new ArrayList<Version>();
						for (int k = 0; k < documentVersionList.size(); k++) {
							if (documentList.get(j).getDocumentId()
									.equals(documentVersionList.get(k).getDocumentId())) {
								if (versionNo.equals(documentVersionList.get(k).getVersionNo())) {
									Version ver = new Version();
									ver.setVersionNo(documentVersionList.get(k).getVersionNo());
									ver.setCreatedOn(documentVersionList.get(k).getCreatedOn());
									logger.debug("Parent Id for this version: " + documentVersionList.get(k).getId());
									Verification verification = documentDao
											.getVerification(documentVersionList.get(k).getId());
									if (verification != null) {
										verification.setSystemName(systemIdNameMap.get(verification.getSystemId()));
									}

									List<File> files = documentDao.getFiles(documentVersionList.get(k).getId());
									if (files.isEmpty()) {
										ver.setFiles(files);
									} else {
										ver.setFiles(files);
									}
									List<DigitizedInfo> digitized_info = documentDao
											.getDigitalInfo(documentVersionList.get(k).getId());
									if (digitized_info.isEmpty()) {
										digitized_info = documentDao
												.getDigitalInfoMapper(doc_type.get(i).getDocumentTypeId());
									}

									ver.setDigitalInfo(digitized_info);
									ver.setVerification(verification);
									version.add(ver);

								}

							}

							List<Comments> comments = documentDao.getComments(smeId,
									doc_type.get(i).getDocumentTypeId(), documentList.get(j).getDocumentId());
							for (int t = 0; t < comments.size(); t++) {
								comments.get(t).setSystemName(systemIdNameMap.get(comments.get(t).getSystemId()));
							}
							documentList.get(j).setComments(comments);
							documentList.get(j).setVersionList(version);

						}
						/*
						 * If specific version not found in DMS
						 */
						if (version.isEmpty()) {
							rdto.setMessage("Version No. not found for this Document Id");
							rdto.setErrorCode(errorCodeConfiguration.getInputerror());
							rdto.setData(null);
							rdto.setStatus(apiMessageConfiguration.getFailed());
							return rdto;
						}

					}
					doc_type.get(i).setDocumentList(documentList);
				} else {
					for (int j = 0; j < documentList.size(); j++) {
						count = 1;
						List<Version> version = new ArrayList<Version>();
						for (int k = 0; k < documentVersionList.size(); k++) {
							if (documentList.get(j).getDocumentId()
									.equals(documentVersionList.get(k).getDocumentId())) {
								/*
								 * Returning the latest version of document
								 */
								if (count <= 1) {
									logger.debug("Latest Version No in get documents: "
											+ documentVersionList.get(k).getVersionNo());
									logger.debug("Parent Id in get documents: " + documentVersionList.get(k).getId());
									Version ver = new Version();
									ver.setVersionNo(documentVersionList.get(k).getVersionNo());
									ver.setCreatedOn(documentVersionList.get(k).getCreatedOn());

									Verification verification = documentDao
											.getVerification(documentVersionList.get(k).getId());
									if (verification != null) {
										verification.setSystemName(systemIdNameMap.get(verification.getSystemId()));
									}

									List<File> files = documentDao.getFiles(documentVersionList.get(k).getId());
									if (files.isEmpty()) {
										ver.setFiles(files);
									} else {
										ver.setFiles(files);
									}
									List<DigitizedInfo> digitized_info = documentDao
											.getDigitalInfo(documentVersionList.get(k).getId());
									if (digitized_info.isEmpty()) {
										digitized_info = documentDao
												.getDigitalInfoMapper(doc_type.get(i).getDocumentTypeId());
									}

									ver.setDigitalInfo(digitized_info);
									ver.setVerification(verification);
									version.add(ver);
									count++;
								} else {
									break;
								}
							}

							List<Comments> comments = documentDao.getComments(smeId,
									doc_type.get(i).getDocumentTypeId(), documentList.get(j).getDocumentId());
							for (int t = 0; t < comments.size(); t++) {
								comments.get(t).setSystemName(systemIdNameMap.get(comments.get(t).getSystemId()));
							}
							documentList.get(j).setComments(comments);
							documentList.get(j).setVersionList(version);

						}

					}
					doc_type.get(i).setDocumentList(documentList);
				}

			}
		}
		documentWrapper.setDocType(doc_type);
		documentWrapper.setSmeId(smeId);
		rdto.setData(documentWrapper);
		rdto.setErrorCode(0);
		rdto.setMessage("Success");
		rdto.setTotalRecords(doc_type.size());
		rdto.setStatus(apiMessageConfiguration.getSuccess());
		return rdto;
	}

	public ResponseDto uploadDocument(MultipartFile files, FileRequestDto data, HttpServletRequest request,
			ServletContext context) {
		ResponseDto rdto = new ResponseDto();
		File uploadedFile = new File();
		String uploadResult[] = new String[2];
		String baseFileSaveLocation = "";
		String uniqueId = UUID.randomUUID().toString();
		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				baseFileSaveLocation = filePathConfiguration.getFile();
			} else if (SystemUtils.IS_OS_LINUX) {
				baseFileSaveLocation = filePathConfiguration.getFile();
			} else if (SystemUtils.IS_OS_MAC) {
				baseFileSaveLocation = filePathConfiguration.getFile();
			}
			String fileSaveLocation = baseFileSaveLocation;
			// create a new file
			java.io.File file = new java.io.File(fileSaveLocation);
			file.mkdir();

			String originalFileName = files.getOriginalFilename();

			String types = fileExtensionConfiguration.getAllowed();
			String[] type = types.split(",");
			int typeFound = 0;
			for (int i = 0; i < type.length; i++) {
				if (originalFileName.substring(originalFileName.lastIndexOf('.')).equalsIgnoreCase(type[i])) {
					typeFound = 1;
				}
			}

			if (typeFound == 0) {
				rdto.setMessage("Invalid Extension Type. Allowed Types (" + types + ")");
				rdto.setErrorCode(1002);
				rdto.setStatus(apiMessageConfiguration.getFailed());
				rdto.setData(null);
				rdto.setTotalRecords(0);
				return rdto;
			}

			if (originalFileName.lastIndexOf('.') != 0) {
				uploadResult[0] = uniqueId + originalFileName.substring(originalFileName.lastIndexOf('.'));
			} else {
				uploadResult[1] = "Error in File Extension Type";
			}
			// make new file location
			String uploadedFileLocation = fileSaveLocation + uploadResult[0];
			// save uploaded file to new file location
			file = new java.io.File(uploadedFileLocation);
			java.nio.file.Path path = file.toPath();
			InputStream inputStream = files.getInputStream();

			// if size of file is greater than 25 MB return to caller function
			// with error message
			if (files.getSize() > 25000000) {
				uploadResult[1] = "File Size is Greater than 25MB. Uploading Failed";
				rdto.setMessage(uploadResult[1]);
				rdto.setErrorCode(1002);
				rdto.setStatus(apiMessageConfiguration.getFailed());
				rdto.setData(null);
				rdto.setTotalRecords(0);
				return rdto;
			}

			Files.copy(inputStream, path);
			inputStream.close();

			uploadedFile.setFileId(uploadResult[0]);
			uploadedFile.setFileSize(files.getSize());
			uploadedFile.setFileName(originalFileName);
			uploadedFile.setTimeToLive(data != null ? data.getTimeToLive() : 0);

			int result = documentDao.saveFileInfo(uploadedFile);
			if (result > 0) {
				rdto.setMessage("Successfully uploaded");
				rdto.setErrorCode(0);
				rdto.setStatus(apiMessageConfiguration.getSuccess());
				rdto.setData(uploadedFile);
				rdto.setTotalRecords(1);
			}
			return rdto;
		} catch (Exception e) {

			logger.error("Exception thrown while storing file Error Message ::" + e.getMessage());
			e.printStackTrace();
			ResponseDto responseDto = new ResponseDto();
			responseDto.setMessage("File could not be uploaded::" + uploadResult[1]);
			responseDto.setData(null);
			responseDto.setErrorCode(errorCodeConfiguration.getInputerror());
			responseDto.setStatus(apiMessageConfiguration.getFailed());

			return responseDto;

		}

	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		logger.debug("File size: " + digitGroups);
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	@Override
	public ResponseDto updateDocument(DocumentWrapper documentWrapper) {
		ResponseDto response = new ResponseDto();
		List<Version> versionList = null;
		try {
			String smeId = documentWrapper.getSmeId();
			if (smeId.isEmpty()) {
				smeId = generateUIDForSMEIdField();
			}
			List<DocumentType> docTypeList = documentWrapper.getDocType();

			/*
			 * validating system name
			 */

			response = vaidateSystemName(documentWrapper.getSystemName(), response);
			if (response.getErrorCode() != 0) {
				return response;
			}

			String systemName = documentWrapper.getSystemName();
			Integer systemId = systemNameIdMap.get(systemName.toLowerCase());

			if (docTypeList == null || docTypeList.isEmpty()) {
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("Invalid Request! No Documents Types found for update.");
				logger.debug("Invalid Request! No Documents Types found for update.");
				return response;
			}

			for (DocumentType docType : docTypeList) {
				Integer docTypeId = docType.getDocumentTypeId();

				/*
				 * Validating doc type Id
				 */

				response = validateDocTypeId(docType.getDocumentTypeId(), systemId, response);
				if (response.getErrorCode() != 0) {
					return response;
				}

				List<Document> documentList = docType.getDocumentList();
				if (documentList == null || documentList.isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid Request! No documents found to update.");
					logger.debug("Invalid Request! No documents found to update.");
					return response;
				}

				/*
				 * Validating each document
				 */
				response = validateDocuments(documentList, response);
				if (response.getErrorCode() != 0) {
					return response;
				}

				for (Document document : documentList) {
					versionList = new ArrayList<>();
					Integer docId = document.getDocumentId();
					document.setDocumentTypeId(docTypeId);
					document.setSmeId(smeId);

					String validTillStr = document.getValidTillStr();
					java.util.Date validTill = null;
					if (validTillStr != null && !validTillStr.isEmpty()) {
						validTill = DateUtility.parseDateInYYYYMMDDFormat(validTillStr);
					}

					String createdOnStr = document.getCreatedOnStr();
					java.util.Date createdOn = null;
					if (createdOnStr != null && !createdOnStr.isEmpty()) {
						createdOn = DateUtility.parseDateInYYYYMMDDFormat(createdOnStr);
					}
					document.setValidTill(validTill);
					document.setCreatedOn(createdOn);
					if (docId == null || docId == 0) {
						logger.debug("::No Doc id so creating a new document::");

						Integer latestDocId = documentDao.getLatestDocumentId(docTypeId, smeId);

						logger.debug("Latest doc id fetched: " + latestDocId);

						document.setDocumentId(latestDocId + 1);
						document.setVersionNo(1);
						document.setCreatedOn(new java.util.Date());
						documentDao.createNewEntryForDocument(smeId, docTypeId, systemId, document);
						versionList.add(addVersionDetailsForResponse(smeId, docTypeId, document));
					} else {
						Document documentfetched = this.getDocumentById(document);
						if (documentfetched == null) {
							response.setData(null);
							response.setMessage("No document found to update for SME Id:  " + document.getSmeId()
									+ ", doc type Id: " + document.getDocumentTypeId() + ", doc Id: "
									+ document.getDocumentId() + ", Kindly recheck these fields.");
							response.setErrorCode(errorCodeConfiguration.getInternalserver());
							response.setStatus("Input Error");
							return response;
						}
						document.setVersionNo(documentfetched.getVersionNo());
						if (isThereAnyFileChange(documentfetched, document)
								|| isThereAnyDigitalInfoChange(documentfetched, document)) {
							documentDao.createNewEntryForDocument(smeId, docTypeId, systemId, document);
						} else {
							documentDao.doUpdationForDocument(smeId, documentfetched, document, systemId, docTypeId);
						}
						versionList.add(addVersionDetailsForResponse(smeId, docTypeId, document));

					}
					document.setVersionList(versionList);
				}
			}
			response = prepareResponse(smeId, documentWrapper, systemId, versionList, response);
		} catch (Exception e) {
			logger.error("Exception while updating document: ", e);
			response.setData(null);
			response.setMessage("Exception while updating document: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	private boolean isThereAnyDigitalInfoChange(Document documentfetched, Document document) {
		Integer parentId = documentfetched.getId();
		List<DigitizedInfo> digitalInfoList = documentDao.getDigitalInfo(parentId);
		Map<String, String> savedDigitalInfoMap = getDigitalInfomap(digitalInfoList);
		Map<String, String> currentDigitalInfoMap = getDigitalInfomap(document.getDigitalInfoList());
		if (!currentDigitalInfoMap.equals(savedDigitalInfoMap)) {
			Integer newVersion = documentfetched.getVersionNo() + 1;
			document.setVersionNo(newVersion);
			document.setCreatedOn(new java.util.Date());
			return true;
		}
		return false;
	}

	private Map<String, String> getDigitalInfomap(List<DigitizedInfo> digitalInfoList) {
		Map<String, String> digitalInfoMap = new HashMap<>();
		if (digitalInfoList != null) {
			for (DigitizedInfo digitalInfo : digitalInfoList) {
				digitalInfoMap.put(digitalInfo.getKey(), digitalInfo.getValue());
			}
		}
		return digitalInfoMap;
	}

	/**
	 * 
	 * @param documentList
	 * @param response
	 * @return
	 */
	private ResponseDto validateDocuments(List<Document> documentList, ResponseDto response) {
		String smeId = null;
		for (Document document : documentList) {
			if (smeId == null)
				smeId = document.getSmeId();
			else {
				if (!smeId.equalsIgnoreCase(document.getSmeId())) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage(
							"Updation of documents belonging multiple SMEs in a single request is not allowed, kindly check the SME IDs passed in the request");
					logger.debug("SME IDs passed with documents in Document list dont match with each other");
					break;
				}
			}

			if (document.getFiles() != null && !document.getFiles().isEmpty()) {
				response = validateFiles(document.getFiles(), response);
				if (response.getErrorCode() != 0) {
					return response;
				}

				response = validateComments(document.getComments(), response);
				if (response.getErrorCode() != 0) {
					return response;
				}

				response = validateDigitalInfo(document.getDigitalInfoList(), response);
				if (response.getErrorCode() != 0) {
					return response;
				}

				response = validateVerification(document.getVerification(), response);
				if (response.getErrorCode() != 0) {
					return response;
				}
			}
		}
		return response;
	}

	/**
	 * 
	 * @param verification: instance of verification
	 * @param response
	 * @return
	 */
	private ResponseDto validateVerification(List<Verification> verification, ResponseDto response) {
		if (verification != null) {
			for (int i = 0; i < verification.size(); i++) {
				Verification verif = verification.get(i);
				if (verif.getStatus() == null || verif.getStatus().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for verification Status at index " + (i + 1));
					break;
				} else if (verif.getUpdatedBy() == null || verif.getUpdatedBy().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for verification updated by at index " + (i + 1));
					break;
				}
			}
		}
		return response;
	}

	/**
	 * 
	 * @param digitalInfoList: list of digital info for document
	 * @param response
	 * @return
	 */
	private ResponseDto validateDigitalInfo(List<DigitizedInfo> digitalInfoList, ResponseDto response) {
		if (digitalInfoList != null) {
			for (int i = 0; i < digitalInfoList.size(); i++) {
				DigitizedInfo digInfo = digitalInfoList.get(i);
				if (digInfo.getKey() == null || digInfo.getKey().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for digital info key at index " + (i + 1));
					break;
				} else if (digInfo.getType() == null || digInfo.getType().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for digital info value at index " + (i + 1));
					break;
				}
			}
		}
		return response;
	}

	/**
	 * 
	 * @param comments: list of comments from document
	 * @param response
	 * @return
	 */
	private ResponseDto validateComments(List<Comments> comments, ResponseDto response) {
		if (comments != null) {
			for (int i = 0; i < comments.size(); i++) {
				Comments comment = comments.get(i);
				if (comment.getComment() == null || comment.getComment().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for Comment at index " + (i + 1));
					logger.debug("Invalid input for Comment at index " + (i + 1));
					break;
				} else if (comment.getCreatedBy() == null || comment.getCreatedBy().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for created by at index " + (i + 1));
					logger.error("Invalid input for created by at index " + (i + 1));
					break;
				} else if (comment.getAction() == null || comment.getAction().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for action at index " + (i + 1));
					logger.debug("Invalid input for action at index " + (i + 1));
					break;
				}
			}
		}
		return response;
	}

	/**
	 * 
	 * @param files
	 * @param response
	 * @return
	 */
	private ResponseDto validateFiles(List<File> files, ResponseDto response) {
		if (files != null) {
			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				if (file.getFileId() == null || file.getFileId().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for File Id at index " + (i + 1));
					logger.debug("Invalid input for File Id at index " + (i + 1));
					break;
				} else if (file.getFileName() == null || file.getFileName().isEmpty()) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for File Name at index " + (i + 1));
					logger.debug("Invalid input for File Name at index " + (i + 1));
					break;
				} else if (file.getFileId() != null && !isFileUploadedInDms(file.getFileId())) {
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Invalid input for File Id at index " + (i + 1) + ", no file uploaded for id: "
							+ file.getFileId());
					logger.debug("Invalid input for File Id at index " + (i + 1) + ", no file uploaded for id: "
							+ file.getFileId());
					break;
				}
			}
		}
		return response;
	}

	private boolean isFileUploadedInDms(String fileId) {
		return documentDao.isFileUploadedInDms(fileId);
	}

	/**
	 * 
	 * @param documentTypeId
	 * @param systemId
	 * @param response
	 * @return
	 */
	private ResponseDto validateDocTypeId(Integer documentTypeId, Integer systemId, ResponseDto response) {
		if (documentTypeId == null) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input for document type Id");
			logger.debug("Invalid input for document type Id");
		} else if (documentIdNameMap.get(documentTypeId) == null) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input document type not registered with DMS");
			logger.debug("Invalid input document type not registered with DMS");
		} else if (!isDocTypeValidForSystemName(systemId, documentTypeId)) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input document type ID" + documentTypeId + " not accessible to "
					+ systemIdNameMap.get(systemId));
			logger.debug("Invalid input document type ID" + documentTypeId + " not accessible to "
					+ systemIdNameMap.get(systemId));
		}
		return response;
	}

	private boolean isDocTypeValidForSystemName(Integer systemId, Integer documentTypeId) {
		if (documentDao.isAccessabletoSystem(systemId, documentTypeId)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param systemName
	 * @param response
	 * @return
	 */
	private ResponseDto vaidateSystemName(String systemName, ResponseDto response) {
		if (systemName == null || systemName.isEmpty()) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input for system name");
			logger.debug("Invalid input for system name");
		} else if (systemNameIdMap.get(systemName.toLowerCase()) == null) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input system name '" + systemName + "' not registered with DMS");
			logger.debug("Invalid input system name '" + systemName + "' not registered with DMS");
		}
		return response;
	}

	private ResponseDto setErrorResponse(Integer errorCode, ResponseDto response) {
		response.setErrorCode(errorCode);
		response.setData(null);
		response.setStatus(apiMessageConfiguration.getFailed());
		return response;
	}

	/**
	 * 
	 * @param smeId
	 * @param documentWrapper
	 * @param systemId
	 * @param versionList
	 * @param response
	 * @return
	 */
	private ResponseDto prepareResponse(String smeId, DocumentWrapper documentWrapper, Integer systemId,
			List<Version> versionList, ResponseDto response) {
		logger.debug(":: Preparing Response for UPDATE API... ");

		DocumentWrapper responseDocWrapper = new DocumentWrapper();
		responseDocWrapper.setSmeId(smeId);
		responseDocWrapper.setSystemName(documentWrapper.getSystemName());
		List<DocumentType> docTypeListForResponse = new ArrayList<>();
		List<DocumentType> docTypeList = documentWrapper.getDocType();
		for (DocumentType docType : docTypeList) {
			DocumentType newDocumentType = new DocumentType();
			Integer docTypeId = docType.getDocumentTypeId();
			String docTypeName = docType.getDocumentTypeName();
			Integer maxDocuments = docType.getMaxDocuments();
			Integer categoryId = docType.getCategoryId();
			newDocumentType.setDocumentTypeId(docTypeId);
			newDocumentType.setDocumentTypeName(docTypeName);
			newDocumentType.setMaxDocuments(maxDocuments);
			newDocumentType.setCategoryId(categoryId);
			List<Document> documentListForResponse = new ArrayList<>();
			List<Document> documentList = docType.getDocumentList();
			for (Document document : documentList) {
				Document newDocument = new Document();
				newDocument.setDocumentId(document.getDocumentId());
				newDocument.setDocumentName(document.getDocumentName());
				List<Version> versonList = document.getVersionList();
				for (Version version : versonList) {
					if (document.getVerification() == null || document.getVerification().isEmpty()) {
						Verification verification = new Verification();
						version.setVerification(verification);
					} else {
						version.setVerification(document.getVerification().get(0));
					}
				}
				newDocument.setVersionList(versonList);
				newDocument.setComments(document.getComments());
				newDocument.setCreatedOnStr(document.getCreatedOnStr());
				documentListForResponse.add(newDocument);
			}
			newDocumentType.setDocumentList(documentListForResponse);
			docTypeListForResponse.add(newDocumentType);
		}

		responseDocWrapper.setDocType(docTypeListForResponse);
		response.setData(responseDocWrapper);
		response.setMessage("Successfully updated data ");
		response.setErrorCode(0);
		response.setStatus(apiMessageConfiguration.getSuccess());
		return response;
	}

	/**
	 * 
	 * @param smeId
	 * @param docTypeId
	 * @param document
	 * @return
	 */
	private Version addVersionDetailsForResponse(String smeId, Integer docTypeId, Document document) {
		logger.debug(":: Adding version details for response of Update API ::");
		Version version = new Version();
		version.setVersionNo(document.getVersionNo());
		version.setDigitalInfo(document.getDigitalInfoList());
		version.setFiles(document.getFiles());
		version.setCreatedOnStr(document.getCreatedOnStr());
		if (document.getVerification() != null && !document.getVerification().isEmpty()) {
			version.setVerification(document.getVerification().get(0));
		}
		return version;
	}

	/**
	 * Generates UID in case SME Id is null from system
	 * 
	 * @return
	 */
	private String generateUIDForSMEIdField() {
		UUID uniqueKey = UUID.randomUUID();
		logger.debug("GENERATED UID is: " + uniqueKey.toString());
		return uniqueKey.toString();
	}

	/**
	 * Checks if there is any file change
	 * 
	 * @param documentfetched
	 * @param document
	 * @return - > true (on file change) - > false
	 */
	private boolean isThereAnyFileChange(Document documentfetched, Document document) {
		Integer parentId = documentfetched.getId();
		List<File> files = documentDao.getFiles(parentId);
		List<String> existingFileIdList = this.getFileIdList(files);
		List<String> newFileIdList = this.getFileIdList(document.getFiles());
		if (!existingFileIdList.containsAll(newFileIdList) || (newFileIdList.isEmpty() && !existingFileIdList.isEmpty())
				|| (newFileIdList.size() != existingFileIdList.size())) {
			Integer newVersion = documentfetched.getVersionNo() + 1;
			document.setVersionNo(newVersion);
			document.setCreatedOn(new java.util.Date());
			return true;
		}
		return false;
	}

	/**
	 * Fetches all file Ids from a List of File
	 * 
	 * @param files
	 * @return
	 */
	private List<String> getFileIdList(List<File> files) {
		List<String> fileIdList = new ArrayList<>();
		for (File file : files) {
			fileIdList.add(file.getFileId());
		}
		return fileIdList;
	}

	/**
	 * Fetches documentList by document details(smeid,docId,system_id,versionNo)
	 * 
	 * @param document
	 * @return
	 */
	private Document getDocumentById(Document document) {
		Document documentfetched = documentDao.getDocumentLatestDocument(document);
		return documentfetched;
	}

	/**
	 * Map unique id with sme id
	 * 
	 * @param smeidmap
	 * @return
	 */
	@Override
	public ResponseDto updateSme(SmeIdMap smeIdMap) {
		try {
			logger.debug("Sme Id map: " + smeIdMap.toString());
			ResponseDto rdto = new ResponseDto();
			if (smeIdMap.getSmeId().trim() == null || smeIdMap.getSmeId().trim().equalsIgnoreCase("")) {
				rdto.setMessage("SME Id can't be Null or blank");
				rdto.setTotalRecords(0);
				rdto.setData(null);
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setStatus(PropertyUtil.properties.getProperty("API_MESSAGE_FAILED"));
				return rdto;
			}
			if (smeIdMap.getUniqueId().trim() == null || smeIdMap.getUniqueId().trim().equalsIgnoreCase("")) {
				rdto.setMessage("Unique Id can't be Null or blank");
				rdto.setTotalRecords(0);
				rdto.setData(null);
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setStatus(PropertyUtil.properties.getProperty("API_MESSAGE_FAILED"));
				rdto.setStatus(apiMessageConfiguration.getFailed());
				return rdto;
			}
			int result = documentDao.updateSmeIdInDocuments(smeIdMap.getSmeId(), smeIdMap.getUniqueId());
			int result2 = documentDao.updateSmeIdInComments(smeIdMap.getSmeId(), smeIdMap.getUniqueId());
			logger.debug("Update smeid in Documents : " + result);
			logger.debug("Update smeid in Comments : " + result2);

			// if (result > 0) {
			rdto.setMessage("SME Id updated successfully");
			rdto.setTotalRecords(result);
			rdto.setData(null);
			rdto.setErrorCode(0);
			rdto.setStatus(PropertyUtil.properties.getProperty("API_MESSAGE_SUCCESS"));
			rdto.setStatus(apiMessageConfiguration.getSuccess());
			return rdto;
//			} else {
//				rdto.setMessage("Unique Id doesn't exist");
//				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
//				rdto.setData(null);
//				rdto.setStatus(apiMessageConfiguration.getFailed());
//				rdto.setTotalRecords(0);
//				return rdto;
//			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseDto rdto = new ResponseDto();
			rdto.setData(null);
			rdto.setErrorCode(errorCodeConfiguration.getInternalserver());
			rdto.setMessage(e.getMessage());
			rdto.setStatus(apiMessageConfiguration.getFailed());
			rdto.setTotalRecords(0);
			return rdto;
		}
	}

	/**
	 * It returns the list of all document types for an application
	 * 
	 * @param systemName
	 *
	 * @return
	 */
	@Override
	public ResponseDto getDocumentType(String systemName) {
		try {
			logger.debug("System Name for get document type: " + systemName);
			ResponseDto rdto = new ResponseDto();

			if (systemName == null || systemName.equalsIgnoreCase("")) {
				rdto.setMessage("System Name can't be null or blank");
				rdto.setTotalRecords(0);
				rdto.setData(null);
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setStatus(apiMessageConfiguration.getFailed());
				return rdto;
			}
			rdto = vaidateSystemName(systemName, rdto);
			if (rdto.getErrorCode() != 0) {
				return rdto;
			}

			List<DocumentType> getDocumentTypeList = documentDao
					.getDocumentType(systemNameIdMap.get(systemName.toLowerCase()));
			if (getDocumentTypeList.isEmpty()) {
				rdto.setMessage("Please enter valid system name");
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setData(null);
				rdto.setStatus(apiMessageConfiguration.getFailed());
				return rdto;
			}

			List<Category> getCategoryList = documentDao.getCategoryDetail();
			List<Category> categoryList = new ArrayList<Category>();
			for (int i = 0; i < getCategoryList.size(); i++) {
				int count = 0;
				Category category = new Category();
				List<DocumentType> documentTypeList = new ArrayList<DocumentType>();
				for (int j = 0; j < getDocumentTypeList.size(); j++) {
					if (getCategoryList.get(i).getCategoryId() == getDocumentTypeList.get(j).getCategoryId()) {

						DocumentType documentType = new DocumentType();
						documentType.setDocumentTypeId(getDocumentTypeList.get(j).getDocumentTypeId());
						documentType.setDocumentTypeName(getDocumentTypeList.get(j).getDocumentTypeName());
						documentType.setMaxDocuments(getDocumentTypeList.get(j).getMaxDocuments());
						documentTypeList.add(documentType);
						count++;
					}

				}
				if (count > 0) {
					category.setCategoryId(getCategoryList.get(i).getCategoryId());
					category.setCategoryName(getCategoryList.get(i).getCategoryName());
					category.setDocumentType(documentTypeList);
					categoryList.add(category);
				}

			}

			rdto.setData(categoryList);
			rdto.setErrorCode(0);
			rdto.setTotalRecords(categoryList.size());
			rdto.setMessage("Success");
			rdto.setStatus(apiMessageConfiguration.getSuccess());

			return rdto;
		} catch (Exception e) {
			ResponseDto rdto = new ResponseDto();
			rdto.setData(null);
			rdto.setErrorCode(errorCodeConfiguration.getInternalserver());
			rdto.setMessage(e.getMessage());
			rdto.setStatus(apiMessageConfiguration.getFailed());
			rdto.setTotalRecords(0);
			return rdto;

		}
	}

	/**
	 * download file on the basis of file id
	 * 
	 * @param fileId
	 */
	@Override
	public void downloadFile(String fileId, HttpServletResponse response) {

		logger.info("FUNCTION START: ");
		try {

			File fileInfo = documentDao.getFileInfo(fileId);
			if (fileInfo == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				if (fileInfo.getTimeToLive() != 0) {
					int diffDays = DateUtility.countdiffInDays(fileInfo.getFileModifiedAt(), new java.util.Date());
					if (diffDays > fileInfo.getTimeToLive()) {
						response.setStatus(HttpServletResponse.SC_NOT_FOUND);
						return;
					}
				}
				java.io.File file = new java.io.File(filePathConfiguration.getFile() + fileId);
				String mimeType = URLConnection.guessContentTypeFromName(file.getName());
				if (mimeType == null) {
					logger.info("mimetype is not detectable, will take default");
					mimeType = "application/octet-stream";
				}

				logger.info("mimetype : " + mimeType);

				response.setContentType(mimeType);

				response.setHeader("Content-Disposition",
						String.format("attachment; filename=\"%s\"", fileInfo.getFileName()));

				response.setContentLength((int) file.length());

				long startTime = System.currentTimeMillis();
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
				long endTime = System.currentTimeMillis();

				if (logger.isDebugEnabled()) {
					logger.debug("Time taken when converting file into inputstream :" + (endTime - startTime)
							+ " for file Id: " + fileId);
				}

				startTime = System.currentTimeMillis();
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				endTime = System.currentTimeMillis();

				if (logger.isDebugEnabled()) {
					logger.debug("Time taken when copying file from inputstream to response:" + (endTime - startTime)
							+ " for file Id: " + fileId);
				}

			}
		} catch (Exception e) {
			logger.debug("Exception thrown while download the file", e);
		}
	}

	/**
	 * download multiple files in a zip with sub folder of name document type
	 * 
	 * @param zipWrapper
	 */
	@Override
	public void zipFiles(ZipWrapper zipWrapper, HttpServletResponse response) {
		try {
			logger.debug("In Zip File Service");

			ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

			java.io.File containFolder = new java.io.File(filePathConfiguration.getFolder() + "DocumentTypes/");
			containFolder.mkdir();
			int count = 0;
			List<ZipDocumentsId> zipDocumentsList = zipWrapper.getZipDocumentList();
			logger.debug("Zip document List: " + zipDocumentsList.toString());
			for (int i = 0; i < zipDocumentsList.size(); i++) {
				List<Document> documentList = documentDao.getParentId(zipDocumentsList.get(i), zipWrapper.getSmeId());
				if (documentList.isEmpty()) {
					count = 1;
					break;
				}
				List<File> fileList = new ArrayList<File>();
				for (int j = 0; j < documentList.size(); j++) {
					logger.debug("Parent id :: " + documentList.get(j).getId());
					List<File> files = documentDao.getFiles(documentList.get(j).getId());
					fileList.addAll(files);
				}

				logger.debug("File list: " + fileList.toString());

				String docTypeName = documentIdNameMap.get(zipDocumentsList.get(i).getDocumentTypeId());
				logger.debug("Document Type Id: " + zipDocumentsList.get(i).getDocumentTypeId()
						+ " Document Type Name: " + docTypeName);

				java.io.File documentTypeFolder = new java.io.File(
						filePathConfiguration.getFolder() + "DocumentTypes/" + docTypeName.trim().replace(" ", "_"));
				documentTypeFolder.mkdir();
				for (int j = 0; j < fileList.size(); j++) {
					java.io.File file = new java.io.File(filePathConfiguration.getFile() + fileList.get(j).getFileId());
					java.io.File fileFolder = new java.io.File(filePathConfiguration.getFolder() + "DocumentTypes/"
							+ docTypeName.trim().replace(" ", "_") + "/" + fileList.get(j).getFileName());
					FileUtils.copyFile(file, fileFolder);
				}

			}

			if (count == 1) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

			else {
				response.setContentType("application/zip");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
						zipWrapper.getSmeName().replace(" ", "_").replace(".", "") + ".zip"));

				java.io.File file = new java.io.File(filePathConfiguration.getFolder() + "DocumentTypes");

				zipDirectory(file, zipOutputStream);
				zipOutputStream.close();

				FileUtils.deleteDirectory(file);
			}

		} catch (Exception e) {
			logger.debug("Exception thrown while download the zip file", e);
		}
	}

	/**
	 * download the file for given doc type id and version
	 * 
	 * @param zipFileWrapper
	 * @param response
	 */
	@Override
	public void zipfileByDocType(ZipFileWrapper zipFileWrapper, HttpServletResponse response) {
		try {
			logger.debug("In Zip File Service");

			ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

			java.io.File containFolder = new java.io.File(filePathConfiguration.getFolder() + "DocumentTypes/");
			containFolder.mkdir();
			int count = 0;
			List<ZipFileDocuments> zipFileDocumentsList = zipFileWrapper.getZipFileDocumentsList();
			logger.debug("Zip document List: " + zipFileDocumentsList.toString());
			for (int i = 0; i < zipFileDocumentsList.size(); i++) {

				List<DocumentsEntity> documentList = documentDao.getDocumentsList(zipFileDocumentsList.get(i),
						zipFileWrapper.getSmeId());
				if (documentList.isEmpty()) {
					count = 1;
					break;
				}
				List<File> fileList = new ArrayList<File>();
				for (int j = 0; j < documentList.size(); j++) {
					logger.debug("Parent id :: " + documentList.get(j).getId());
					List<File> files = documentDao.getFiles(documentList.get(j).getId());
					fileList.addAll(files);
				}

				logger.debug("File list: " + fileList.toString());

				String docTypeName = documentIdNameMap.get(zipFileDocumentsList.get(i).getDocumentTypeId());
				logger.debug("Document Type Id: " + zipFileDocumentsList.get(i).getDocumentTypeId()
						+ " Document Type Name: " + docTypeName);

				java.io.File documentTypeFolder = new java.io.File(
						filePathConfiguration.getFolder() + "DocumentTypes/" + docTypeName.trim().replace(" ", "_"));
				documentTypeFolder.mkdir();
				for (int j = 0; j < fileList.size(); j++) {
					java.io.File file = new java.io.File(filePathConfiguration.getFile() + fileList.get(j).getFileId());
					java.io.File fileFolder = new java.io.File(filePathConfiguration.getFolder() + "DocumentTypes/"
							+ docTypeName.trim().replace(" ", "_") + "/" + fileList.get(j).getFileName());
					FileUtils.copyFile(file, fileFolder);
				}

			}
			if (count == 1) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				response.setContentType("application/zip");
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
						zipFileWrapper.getSmeName().replace(" ", "_").replace(".", "") + ".zip"));

				java.io.File file = new java.io.File(filePathConfiguration.getFolder() + "DocumentTypes");

				zipDirectory(file, zipOutputStream);
				zipOutputStream.close();

//				FileUtils.deleteDirectory(file);
			}

		} catch (Exception e) {
			logger.debug("Exception thrown while download the zip file", e);
		}
	}

	private void zipDirectory(java.io.File dir, ZipOutputStream zos) {
		try {
			logger.debug("Directory file: " + dir.toString());
			List<String> filesListInDir = new ArrayList<String>();
			populateFilesList(dir, filesListInDir);
			// now zip files one by one
			// create ZipOutputStream to write to the zip file

			logger.debug("total file list in directory: " + filesListInDir.toString());

			for (String filePath : filesListInDir) {
				System.out.println("Zipping " + filePath);
				// for ZipEntry we need to keep only relative file path, so we used substring on
				// absolute path
				ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);

				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method populates all the files in a directory to a List
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private void populateFilesList(java.io.File dir, List<String> filesListInDir) throws IOException {
		java.io.File[] files = dir.listFiles();
		for (java.io.File file : files) {
			if (file.isFile()) {
				filesListInDir.add(file.getAbsolutePath());
				logger.debug("file list in populate files: " + file.getAbsolutePath());
			} else {
				populateFilesList(file, filesListInDir);
				logger.debug("folder in populate files: " + file.getAbsolutePath());
			}
		}
	}

	@Override
	public ResponseDto getDocuments(DocumentWrapper docRequestWrapper) {
		String smeId = docRequestWrapper.getSmeId();
		String systemName = docRequestWrapper.getSystemName();
		long startTime = System.currentTimeMillis();
		logger.debug("Sme id in get documents: " + smeId);
		logger.debug("System Name in get documents: " + systemName);
		ResponseDto rdto = new ResponseDto();
		int count = 1;
		rdto = this.doValidation(docRequestWrapper);
		long endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total Time elapsed till doValidation :::" + (endTime - startTime));
		if (rdto.getErrorCode() != 0) {
			return rdto;
		}
		List<DocumentType> requestDocumentTypeList = docRequestWrapper.getDocType();
		DocumentWrapper documentWrapper = new DocumentWrapper();
		List<DocumentType> documentTypeList = documentDao
				.getDocumentType(systemNameIdMap.get(systemName.toLowerCase()));
		endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger,
				"Total time elapsed till fetching Document Type List :: " + (endTime - startTime));
		if (documentTypeList.isEmpty()) {
			rdto.setMessage("Please enter valid system name");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			return rdto;
		}
		List<DocumentType> doc_type_list = new ArrayList<DocumentType>();
		List<Integer> versionNoList = new ArrayList<>();

		/**
		 * When document type ID is not null that means user wants to access specific
		 * document type
		 */
		if (requestDocumentTypeList != null && requestDocumentTypeList.size() != 0) {
			DmsLogUtil.logAtDebug(logger,
					"requestDocumentTypeList is not empty and size ::" + requestDocumentTypeList.size());
			long stime = System.currentTimeMillis();
			for (int k = 0; k < requestDocumentTypeList.size(); k++) {
				DocumentType docType = requestDocumentTypeList.get(k);

				Document requestDocument = new Document();
				logger.debug("Specific Document Type Id in get documents: " + docType.getDocumentTypeId());
				for (int i = 0; i < documentTypeList.size(); i++) {
					List<Document> docList = new ArrayList<>();
					if (docType.getDocumentTypeId().equals(documentTypeList.get(i).getDocumentTypeId())) {
						if ((docType.getDocumentList() != null || docType.getDocumentList().size() != 0)) {
							for (int docIndex = 0; docIndex < docType.getDocumentList().size(); docIndex++) {
								requestDocument = new Document();
								if (docType.getDocumentList().get(docIndex).getDocumentId() != null) {
									requestDocument
											.setDocumentId(docType.getDocumentList().get(docIndex).getDocumentId());
								}
								if (docType.getDocumentList().get(docIndex).getVersionNo() != null) {
									requestDocument
											.setVersionNo(docType.getDocumentList().get(docIndex).getVersionNo());
									versionNoList.add(docType.getDocumentList().get(docIndex).getVersionNo());
								}
								docList.add(requestDocument);
							}
						}

						documentTypeList.get(i).setDocumentList(docList);
						doc_type_list.add(documentTypeList.get(i));
						break;
					}
				}
				if (doc_type_list.isEmpty()) {
					rdto.setMessage("Invalid Document Type Id");
					rdto.setErrorCode(errorCodeConfiguration.getInputerror());
					rdto.setData(null);
					rdto.setStatus(apiMessageConfiguration.getFailed());
					return rdto;
				}
			}
			endTime = System.currentTimeMillis();
			DmsLogUtil.logAtDebug(logger, "Total time elapsed till creating doc_type_list:" + (endTime - stime));

		} else {
			/**
			 * To access all documents type
			 */
			DmsLogUtil.logAtDebug(logger, "requestDocumentTypeList is  empty ");
			doc_type_list.addAll(documentTypeList);
		}

		endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed till creating doc_type_list:" + (endTime - startTime));

		for (int i = 0; i < doc_type_list.size(); i++) {
			DmsLogUtil.logAtDebug(logger, "Now processing started for doc_type_list");
			Integer versionNo = 0;
			List<Document> documents = documentDao.getDocumentId(doc_type_list.get(i).getDocumentTypeId(), smeId);
			List<Document> documentVersionList = documentDao
					.getAllDocumentsByDocTypeAndSmeId(doc_type_list.get(i).getDocumentTypeId(), smeId);
			List<Document> documentList = new ArrayList<Document>();
			if ((doc_type_list.get(i).getDocumentList() != null
					&& doc_type_list.get(i).getDocumentList().size() != 0)) {
				DmsLogUtil.logAtDebug(logger, "Inside IF block 1");
				for (Document doc : doc_type_list.get(i).getDocumentList()) {
					Integer docId = doc.getDocumentId();
					versionNo = doc.getVersionNo();
					logger.debug("Specific Document Id in get documents: " + docId);
					for (int j = 0; j < documents.size(); j++) {
						if (docId.equals(documents.get(j).getDocumentId())) {
							Document toBeAddedDoc = new Document();
							toBeAddedDoc.setDocumentId(docId);
							if (versionNo != null) {
								toBeAddedDoc.setVersionNo(versionNo);
							}
							documentList.add(toBeAddedDoc);
							break;
						}
					}
					endTime = System.currentTimeMillis();
					DmsLogUtil.logAtDebug(logger, "Total time elapsed if-else block 1 loop 1 for iteration :: " + i
							+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
					if (documentList.isEmpty()) {
						rdto.setMessage("Invalid Document Id");
						rdto.setErrorCode(errorCodeConfiguration.getInputerror());
						rdto.setData(null);
						rdto.setStatus(apiMessageConfiguration.getFailed());
						return rdto;
					}
				}
				endTime = System.currentTimeMillis();
				DmsLogUtil.logAtDebug(logger, "Total time elapsed if-else block 1 loop 2 for iteration :: " + i
						+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
			} else {
				DmsLogUtil.logAtDebug(logger, "Inside Else block 1");
				documentList.addAll(documents);
			}
			endTime = System.currentTimeMillis();
			DmsLogUtil.logAtDebug(logger, "Total time elapsed if-else block 1 for iteration :: " + i
					+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
			/*
			 * when document list is empty for document type then returning the skeleton of
			 * document list
			 */
			if (documentList.isEmpty()) {
				DmsLogUtil.logAtDebug(logger, "Inside If block 2");
				Document document = new Document();
				Version version = new Version();
				List<Version> versionList = new ArrayList<Version>();

				List<DigitizedInfo> digitizedInfoList = new ArrayList<DigitizedInfo>();

				List<File> files = new ArrayList<File>();

				Verification verification = new Verification();

				List<Comments> commentList = new ArrayList<Comments>();
				digitizedInfoList = documentDao.getDigitalInfoMapper(doc_type_list.get(i).getDocumentTypeId());

				verification.setSystemName("");
				verification.setUpdatedBy("");
				verification.setStatus("");

				version.setVersionNo(0);
				version.setFiles(files);
				version.setDigitalInfo(digitizedInfoList);
				version.setVerification(verification);
				version.setCreatedOn(Date.valueOf("2017-07-26"));
				versionList.add(version);

				document.setDocumentId(0);
				document.setVersionList(versionList);
				document.setComments(commentList);
				document.setSystemId(0);
				documentList.add(document);
				doc_type_list.get(i).setDocumentList(documentList);

			} else {
				DmsLogUtil.logAtDebug(logger, "insied else block 2");
				/*
				 * Returning document of specific version
				 */
				if (!versionNoList.isEmpty()) {
					logger.debug("Specific Version No in get documents: " + versionNo);
					for (int j = 0; j < documentList.size(); j++) {
						List<Version> version = new ArrayList<Version>();
						if (documentList.get(j).getVersionNo() != null) {
							versionNo = documentList.get(j).getVersionNo();
							for (int k = 0; k < documentVersionList.size(); k++) {
								if (documentList.get(j).getDocumentId()
										.equals(documentVersionList.get(k).getDocumentId())) {
									if (versionNo.equals(documentVersionList.get(k).getVersionNo())) {
										Version ver = new Version();
										ver.setVersionNo(documentVersionList.get(k).getVersionNo());
										ver.setCreatedOn(documentVersionList.get(k).getCreatedOn());
										logger.debug(
												"Parent Id for this version: " + documentVersionList.get(k).getId());
										Verification verification = documentDao
												.getVerification(documentVersionList.get(k).getId());
										if (verification != null) {
											verification.setSystemName(systemIdNameMap.get(verification.getSystemId()));
										}

										List<File> files = documentDao.getFiles(documentVersionList.get(k).getId());
										if (files.isEmpty()) {
											ver.setFiles(files);
										} else {
											ver.setFiles(files);
										}
										List<DigitizedInfo> digitized_info = documentDao
												.getDigitalInfo(documentVersionList.get(k).getId());
										if (digitized_info.isEmpty()) {
											digitized_info = documentDao
													.getDigitalInfoMapper(doc_type_list.get(i).getDocumentTypeId());
										}

										ver.setDigitalInfo(digitized_info);
										ver.setVerification(verification);
										version.add(ver);

									}

								}

								List<Comments> comments = documentDao.getComments(smeId,
										doc_type_list.get(i).getDocumentTypeId(), documentList.get(j).getDocumentId());
								for (int t = 0; t < comments.size(); t++) {
									comments.get(t).setSystemName(systemIdNameMap.get(comments.get(t).getSystemId()));
								}
								documentList.get(j).setComments(comments);
								documentList.get(j).setVersionList(version);

							}
							endTime = System.currentTimeMillis();
							DmsLogUtil.logAtDebug(logger, "Total time elapsed else block 2 loop 4 for iteration :: " + i
									+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
							/*
							 * If specific version not found in DMS
							 */
							if (version.isEmpty()) {
								rdto.setMessage("Version No." + versionNo + " not found for docid -"
										+ documentList.get(j).getDocumentId() + " at index " + (i + 1)
										+ "  in Document type List");
								rdto.setErrorCode(errorCodeConfiguration.getInputerror());
								rdto.setData(null);
								rdto.setStatus(apiMessageConfiguration.getFailed());
								return rdto;
							}

						} else {

							count = 1;
							for (int k = 0; k < documentVersionList.size(); k++) {
								if (documentList.get(j).getDocumentId()
										.equals(documentVersionList.get(k).getDocumentId())) {
									/*
									 * Returning the latest version of document
									 */
									if (count <= 1) {
										logger.debug("Latest Version No in get documents: "
												+ documentVersionList.get(k).getVersionNo());
										logger.debug(
												"Parent Id in get documents: " + documentVersionList.get(k).getId());
										Version ver = new Version();
										ver.setVersionNo(documentVersionList.get(k).getVersionNo());
										ver.setCreatedOn(documentVersionList.get(k).getCreatedOn());

										Verification verification = documentDao
												.getVerification(documentVersionList.get(k).getId());
										if (verification != null) {
											verification.setSystemName(systemIdNameMap.get(verification.getSystemId()));
										}

										List<File> files = documentDao.getFiles(documentVersionList.get(k).getId());
										if (files.isEmpty()) {
											ver.setFiles(files);
										} else {
											ver.setFiles(files);
										}
										List<DigitizedInfo> digitized_info = documentDao
												.getDigitalInfo(documentVersionList.get(k).getId());
										if (digitized_info.isEmpty()) {
											digitized_info = documentDao
													.getDigitalInfoMapper(doc_type_list.get(i).getDocumentTypeId());
										}

										ver.setDigitalInfo(digitized_info);
										ver.setVerification(verification);
										version.add(ver);
										count++;
									} else {
										break;
									}
								}

								List<Comments> comments = documentDao.getComments(smeId,
										doc_type_list.get(i).getDocumentTypeId(), documentList.get(j).getDocumentId());
								for (int t = 0; t < comments.size(); t++) {
									comments.get(t).setSystemName(systemIdNameMap.get(comments.get(t).getSystemId()));
								}
								documentList.get(j).setComments(comments);
								documentList.get(j).setVersionList(version);
								endTime = System.currentTimeMillis();
								DmsLogUtil.logAtDebug(logger, "Total time elapsed else block 2 loop 5 for iteration :: "
										+ i + "  TIME TAKEN FROM ROOT " + (endTime - startTime));

							}

						}

					}
					doc_type_list.get(i).setDocumentList(documentList);
					endTime = System.currentTimeMillis();
					DmsLogUtil.logAtDebug(logger, "Total time elapsed else block 2 loop 1 for iteration :: " + i
							+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
				} else {
					for (int j = 0; j < documentList.size(); j++) {
						count = 1;
						List<Version> version = new ArrayList<Version>();
						for (int k = 0; k < documentVersionList.size(); k++) {
							if (documentList.get(j).getDocumentId()
									.equals(documentVersionList.get(k).getDocumentId())) {
								/*
								 * Returning the latest version of document
								 */
								if (count <= 1) {
									logger.debug("Latest Version No in get documents: "
											+ documentVersionList.get(k).getVersionNo());
									logger.debug("Parent Id in get documents: " + documentVersionList.get(k).getId());
									Version ver = new Version();
									ver.setVersionNo(documentVersionList.get(k).getVersionNo());
									ver.setCreatedOn(documentVersionList.get(k).getCreatedOn());

									Verification verification = documentDao
											.getVerification(documentVersionList.get(k).getId());
									if (verification != null) {
										verification.setSystemName(systemIdNameMap.get(verification.getSystemId()));
									}

									List<File> files = documentDao.getFiles(documentVersionList.get(k).getId());
									if (files.isEmpty()) {
										ver.setFiles(files);
									} else {
										ver.setFiles(files);
									}
									List<DigitizedInfo> digitized_info = documentDao
											.getDigitalInfo(documentVersionList.get(k).getId());
									if (digitized_info.isEmpty()) {
										digitized_info = documentDao
												.getDigitalInfoMapper(doc_type_list.get(i).getDocumentTypeId());
									}

									ver.setDigitalInfo(digitized_info);
									ver.setVerification(verification);
									version.add(ver);
									count++;
								} else {
									break;
								}
							}

							List<Comments> comments = documentDao.getComments(smeId,
									doc_type_list.get(i).getDocumentTypeId(), documentList.get(j).getDocumentId());
							for (int t = 0; t < comments.size(); t++) {
								comments.get(t).setSystemName(systemIdNameMap.get(comments.get(t).getSystemId()));
							}
							documentList.get(j).setComments(comments);
							documentList.get(j).setVersionList(version);

						}
						endTime = System.currentTimeMillis();
						DmsLogUtil.logAtDebug(logger, "Total time elapsed else block 2 loop 6 for iteration :: " + i
								+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
					}
					doc_type_list.get(i).setDocumentList(documentList);
				}

			}
			endTime = System.currentTimeMillis();
			DmsLogUtil.logAtDebug(logger, "Total time elapsed if-else block 2 for iteration :: " + i
					+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
			endTime = System.currentTimeMillis();
			DmsLogUtil.logAtDebug(logger, "Total time elapsed till looping doc_type_list iteration :: " + i
					+ "  TIME TAKEN FROM ROOT " + (endTime - startTime));
		}
		endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed After processing of doc_type_list" + (endTime - startTime));
		documentWrapper.setDocType(doc_type_list);
		documentWrapper.setSmeId(smeId);
		rdto.setData(documentWrapper);
		rdto.setErrorCode(0);
		rdto.setMessage("Success");
		rdto.setTotalRecords(doc_type_list.size());
		rdto.setStatus(apiMessageConfiguration.getSuccess());
		endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed before returning final response:" + (endTime - startTime));
		return rdto;
	}

	private ResponseDto doValidation(DocumentWrapper docRequestWrapper) {
		ResponseDto rdto = new ResponseDto();
		String smeId = docRequestWrapper.getSmeId();
		long startTime = System.currentTimeMillis();
		if (docRequestWrapper.getSmeId() == null || docRequestWrapper.getSmeId().isEmpty()) {
			rdto.setMessage("SME Id can't be null or blank");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			long endTime = System.currentTimeMillis();
			DmsLogUtil.logAtDebug(logger, "Total time elapsed in doValidation :: " + (endTime - startTime));
			return rdto;
		}
		if (docRequestWrapper.getSystemName() == null || docRequestWrapper.getSystemName().isEmpty()) {
			rdto.setMessage("System Name can't be null or blank");
			rdto.setErrorCode(errorCodeConfiguration.getInputerror());
			rdto.setData(null);
			rdto.setStatus(apiMessageConfiguration.getFailed());
			long endTime = System.currentTimeMillis();
			DmsLogUtil.logAtDebug(logger, "Total time elapsed in doValidation :: " + (endTime - startTime));
			return rdto;
		}

		if (docRequestWrapper.getDocType() != null && docRequestWrapper.getDocType().size() != 0) {
			for (int i = 0; i < docRequestWrapper.getDocType().size(); i++) {
				DocumentType docType = docRequestWrapper.getDocType().get(i);
				if (docType.getDocumentTypeId() == null
						&& (docType.getDocumentList() != null && !docType.getDocumentList().isEmpty())) {
					rdto.setMessage("Please enter valid Document type Id for this Document Id");
					rdto.setErrorCode(errorCodeConfiguration.getInputerror());
					rdto.setData(null);
					rdto.setStatus(apiMessageConfiguration.getFailed());
					long endTime = System.currentTimeMillis();
					DmsLogUtil.logAtDebug(logger, "Total time elapsed in doValidation :: " + (endTime - startTime));
					return rdto;
				}
				if (docType.getDocumentList() != null && !docType.getDocumentList().isEmpty()) {
					for (int j = 0; j < docType.getDocumentList().size(); j++) {
						if (docType.getDocumentList().get(j).getDocumentId() == null
								&& docType.getDocumentList().get(j).getVersionNo() != null) {
							rdto.setMessage("Please enter valid Document Id for this Document Type Id");
							rdto.setErrorCode(errorCodeConfiguration.getInputerror());
							rdto.setData(null);
							rdto.setStatus(apiMessageConfiguration.getFailed());
							long endTime = System.currentTimeMillis();
							DmsLogUtil.logAtDebug(logger,
									"Total time elapsed in doValidation :: " + (endTime - startTime));
							return rdto;
						}
						if (docType.getDocumentList().get(j).getDocumentId() != null
								&& !documentDao.doesDocumentExist(smeId,
										docType.getDocumentList().get(j).getDocumentId(),
										docType.getDocumentList().get(j).getVersionNo(), docType.getDocumentTypeId())) {
							if (docType.getDocumentList().get(j).getVersionNo() != null) {
								rdto.setMessage(
										"Please enter valid smeId, documentTypeId, documentId  and versionNo Combination, currently no document exist for(SmeId:"
												+ smeId + ", documentTypeId:" + docType.getDocumentTypeId()
												+ ", documentId:" + docType.getDocumentList().get(j).getDocumentId()
												+ ", versionNo. " + docType.getDocumentList().get(j).getVersionNo()
												+ ") combination");

							} else {
								rdto.setMessage(
										"Please enter valid smeId, documentTypeId, documentId  and versionNo Combination, currently no document exist for(SmeId:"
												+ smeId + ", documentTypeId:" + docType.getDocumentTypeId()
												+ ", documentId:" + docType.getDocumentList().get(j).getDocumentId()
												+ ") combination");

							}
							rdto.setErrorCode(errorCodeConfiguration.getInputerror());
							rdto.setData(null);
							rdto.setStatus(apiMessageConfiguration.getFailed());
							long endTime = System.currentTimeMillis();
							DmsLogUtil.logAtDebug(logger,
									"Total time elapsed in doValidation :: " + (endTime - startTime));
							return rdto;
						}
					}
				}

			}
		}
		long endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed in doValidation :: " + (endTime - startTime));
		return rdto;
	}

	@Override
	public ResponseDto getFileMetaData(String fileId) {
		ResponseDto responseDto = new ResponseDto();
		List<MetadataEntity> metadataEntities = new ArrayList<>();
		metadataEntities = metadataRepository.findByfileId(fileId);
		if (metadataEntities.size() > 0) {
			responseDto.setMessage("Meta data fetched succesfully");
			responseDto.setTotalRecords(metadataEntities.size());
			responseDto.setStatus(apiMessageConfiguration.getSuccess());
			responseDto.setData(metadataEntities.get(0));
			responseDto.setErrorCode(0);
		} else {
			responseDto.setMessage("Meta data could not be fetched for fileId :: " + fileId);
			responseDto.setData(null);
			responseDto.setErrorCode(errorCodeConfiguration.getInputerror());
			responseDto.setStatus(apiMessageConfiguration.getFailed());
		}
		return responseDto;
	}

	@Override
	public ResponseDto saveSystemDetail(SystemIdentity details) {
		ResponseDto response = new ResponseDto();
		try {
			if (details.getSystemName() == null || details.getSystemName().isEmpty()) {
				logger.debug("System name cannot be null or empty");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("The system name cannot be null of empty, Kindly provide a valid name");
				return response;
			}
			if (systemNameIdMap.get(details.getSystemName().toLowerCase()) == null) {
				SystemIdentity data = documentDao.saveSystemDetail(details);
				systemIdNameMap.put(data.getSystemId(), data.getSystemName().toLowerCase());
				systemNameIdMap.put(data.getSystemName().toLowerCase(), data.getSystemId());
				response.setMessage("System added succesfully");
				response.setTotalRecords(1);
				response.setStatus(apiMessageConfiguration.getSuccess());
				response.setData(data);
				response.setErrorCode(0);
			} else {
				logger.debug("System is already registered with DMS");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage(
						"The requested system '" + details.getSystemName() + "' is already registered with DMS");
			}

		} catch (Exception e) {
			logger.error("Exception while saving system details", e);
			response.setData(null);
			response.setMessage("Exception while saving system details: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	@Override
	public ResponseDto updateSystemDetail(SystemIdentity details) {
		ResponseDto response = new ResponseDto();
		try {
			if (details.getSystemName() == null || details.getSystemName().isEmpty()) {

				logger.debug("System name cannot be null or empty");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("The system name cannot be null of empty, Kindly provide a valid name");
				return response;
			}
			if (systemIdNameMap.get(details.getSystemId()) == null) {
				logger.debug("Invalid system id provided in update request");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("The system id: '" + details.getSystemId() + "' is not recognized by DMS");
			} else {
				String oldSystemName = systemIdNameMap.get(details.getSystemId());
				List<SystemIdentity> systemDetailList = documentDao.getSystemDetailById(details.getSystemId());
				if (!systemDetailList.get(0).getSystemName().equalsIgnoreCase(details.getSystemName())) {
					Integer rowsAffected = documentDao.updateSystemDetail(details);
					if (rowsAffected > 0) {
						systemIdNameMap.put(details.getSystemId(), details.getSystemName().toLowerCase());
						systemNameIdMap.put(details.getSystemName().toLowerCase(), details.getSystemId());
						systemNameIdMap.remove(oldSystemName);
					}
					response.setMessage("System details updated succesfully");
					response.setTotalRecords(0);
					response.setStatus(apiMessageConfiguration.getSuccess());
					response.setData(null);
					response.setErrorCode(0);
				} else {
					logger.debug("no data found to update");
					setErrorResponse(errorCodeConfiguration.getInputerror(), response);
					response.setMessage("Nothing new to update the data that is already registered with DMS");
				}
			}
		} catch (Exception e) {
			logger.error("Exception while updating system details", e);
			response.setData(null);
			response.setMessage("Exception while updating system details: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	@Override
	public ResponseDto saveDocumentType(DocumentTypeDto documentType) {
		ResponseDto response = new ResponseDto();
		DocumentTypeDto data;
		validateDocumentTypeDto(documentType, response);
		if (response.getErrorCode() != 0) {
			return response;
		}
		try {

			List<DocumentType> fetchedDocumentTypeList = documentDao
					.getDocumentTypeByName(documentType.getDocumentTypeName());
			if (fetchedDocumentTypeList.isEmpty()) {
				data = documentDao.saveDocumentType(documentType);
				documentIdNameMap.put(data.getDocumentTypeId(), data.getDocumentTypeName());
				documentNameIdMap.put(data.getDocumentTypeName(), data.getDocumentTypeId());
				response.setMessage("Document Type added succesfully");
				response.setTotalRecords(1);
				response.setStatus(apiMessageConfiguration.getSuccess());
				response.setData(data);
				response.setErrorCode(0);
			} else {
				logger.debug("The requested document type is already registered with DMS");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("The requested document type  '" + documentType.getDocumentTypeName()
						+ "' is already registered with DMS");
			}
		} catch (Exception e) {
			logger.error("Exception while saving document type ", e);
			response.setData(null);
			response.setMessage("Exception while saving document type: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	@Override
	public ResponseDto updateDocumentType(DocumentTypeDto documentType) {
		ResponseDto response = new ResponseDto();
		try {
			if (documentIdNameMap.get(documentType.getDocumentTypeId()) == null) {
				logger.debug("The requested document type id  is not registered with DMS");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("The requested document type id '" + documentType.getDocumentTypeId()
						+ "' is not registered with DMS");
			} else {
				validateDocumentTypeDto(documentType, response);
				if (response.getErrorCode() != 0) {
					return response;
				}
				documentDao.updateDocumentType(documentType);
				if (documentType.getDocumentTypeName() != null && !documentIdNameMap
						.get(documentType.getDocumentTypeId()).equalsIgnoreCase(documentType.getDocumentTypeName())) {
					// invalidate cache
					String oldDocumentTypeName = documentIdNameMap.get(documentType.getDocumentTypeId());
					documentIdNameMap.put(documentType.getDocumentTypeId(),
							documentType.getDocumentTypeName().toLowerCase());
					documentNameIdMap.put(documentType.getDocumentTypeName().toLowerCase(),
							documentType.getDocumentTypeId());
					documentNameIdMap.remove(oldDocumentTypeName);
				}
				response.setMessage("Document Type details updated succesfully");
				response.setTotalRecords(0);
				response.setStatus(apiMessageConfiguration.getSuccess());
				response.setData(null);
				response.setErrorCode(0);
			}
		} catch (Exception e) {
			logger.error("Exception while updating document type details", e);
			response.setData(null);
			response.setMessage("Exception while updating document type details: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());

		}
		return response;
	}

	@Override
	public ResponseDto addAccessibilityMapping(AccessibleTo mapping) {
		ResponseDto response = new ResponseDto();
		if (mapping.getDocTypeId() == null || documentIdNameMap.get(mapping.getDocTypeId()) == null) {
			logger.debug("The requested document type id '" + mapping.getDocTypeId() + "' is not registered with DMS");
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage(
					"The requested document type id '" + mapping.getDocTypeId() + "' is not registered with DMS");
			return response;
		}
		if (mapping.getSystemId() == null || systemIdNameMap.get(mapping.getSystemId()) == null) {
			logger.debug("The requested system id '" + mapping.getSystemId() + "' is not registered with DMS");
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("The requested system id '" + mapping.getSystemId() + "' is not registered with DMS");
			return response;
		}
		Boolean isSaved = false;
		try {
			isSaved = documentDao.addAccessibilityMapping(mapping);
			if (isSaved) {
				response.setMessage("Accessibility information saved succesfully");
				response.setTotalRecords(0);
				response.setStatus(apiMessageConfiguration.getSuccess());
				response.setData(null);
				response.setErrorCode(0);
			} else {
				logger.debug("The requested accessibility mapping is already registered with DMS");
				setErrorResponse(errorCodeConfiguration.getInputerror(), response);
				response.setMessage("The requested accessibility mapping is already registered with DMS");
			}
		} catch (Exception e) {
			logger.error("Exception while saving accessiblility mapping ", e);
			response.setData(null);
			response.setMessage("Exception while saving accessiblility mapping: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	@Override
	public ResponseDto getCategories() {
		ResponseDto response = new ResponseDto();
		try {
			List<Category> data = documentDao.getCategoryDetail();
			response.setMessage("Category list fetched succesfully");
			response.setTotalRecords(data.size());
			response.setStatus(apiMessageConfiguration.getSuccess());
			response.setData(data);
			response.setErrorCode(0);
		} catch (Exception e) {
			logger.error("Exception while fetching list of categories ", e);
			response.setData(null);
			response.setMessage("Exception while fetch category list: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	@Override
	public ResponseDto search(SearchFilterDto searchFilter) {
		ResponseDto response;
		response = validateSearchFilter(searchFilter);
		if (response.getErrorCode() != 0) {
			logger.debug("Search filter failed validation, returning error response");
			return response;
		}
		if (searchFilter.getPageId() == 1) {
			// do nothing!
		} else {
			searchFilter.setPageId((searchFilter.getPageId() - 1) * searchFilter.getPageSize() + 1);
		}
		searchFilter.setSystemId(systemNameIdMap.get(searchFilter.getSystemName().toLowerCase()));
		try {
			List<SearchResult> data = documentDao.search(searchFilter);
			if (data.isEmpty()) {
				response.setMessage("No documents found matching the search criteria");
				response.setTotalRecords(data.size());
			} else {
				response.setMessage("Showing records " + searchFilter.getPageId() + " to "
						+ (searchFilter.getPageId() + data.size() - 1));
				response.setTotalRecords(documentDao.getSearchResultCount(searchFilter));
			}
			response.setStatus(apiMessageConfiguration.getSuccess());
			response.setData(data);
			response.setErrorCode(0);
		} catch (Exception e) {
			logger.error("Exception while fetching list of documents matching search criteria ", e);
			response.setData(null);
			response.setMessage("Exception while fetching list of documents matching search criteria: " + e);
			response.setErrorCode(errorCodeConfiguration.getInternalserver());
			response.setStatus(apiMessageConfiguration.getErrorcode());
		}
		return response;
	}

	private ResponseDto validateDocumentTypeDto(DocumentTypeDto documentType, ResponseDto response) {
		if (documentType.getCategoryId() != null && categoryIdNameMap.get(documentType.getCategoryId()) == null) {
			logger.debug("The requested category id  is not registered with DMS");
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("The requested category id: '" + documentType.getCategoryId()
					+ "' is not registered with DMS, kindly provide a valid input");
		} else if (documentType.getDocumentTypeName() == null
				|| (documentType.getDocumentTypeName() != null && documentType.getDocumentTypeName().isEmpty())) {
			logger.debug("The document type name is null or empty");
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("The document type name: '" + documentType.getDocumentTypeName()
					+ "' is invalid, kindly provide a valid input");
		} else if (documentType.getMaxDocuments() != null && documentType.getMaxDocuments() < 0) {
			logger.debug("The value passed in max doucuments field: '" + documentType.getMaxDocuments()
					+ "' is invalid, kindly provide a valid input");
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("The value passed in max doucuments field: '" + documentType.getMaxDocuments()
					+ "' is invalid, kindly provide a valid input");
		}

		return response;

	}

	private ResponseDto validateSearchFilter(SearchFilterDto searchFilter) {
		ResponseDto response = new ResponseDto();
		if (searchFilter.getSystemName() == null || (searchFilter.getSystemName() != null
				&& systemNameIdMap.get(searchFilter.getSystemName().toLowerCase()) == null)) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage(
					"Invalid input system name '" + searchFilter.getSystemName() + "' not registered with DMS");
			logger.debug("Invalid input system name '" + searchFilter.getSystemName() + "' not registered with DMS");
			return response;
		}
		if (searchFilter.getDocumentTypeId() != null
				&& documentIdNameMap.get(searchFilter.getDocumentTypeId()) == null) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage(
					"The document type id '" + searchFilter.getDocumentTypeId() + "' not registered with DMS");
			logger.debug("Invalid input '" + searchFilter.getDocumentTypeId() + "' for document type id field");
			return response;
		}
		if (searchFilter.getSmeId() == null && searchFilter.getDocumentTypeId() == null
				&& searchFilter.getText() == null) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Kindly provide some values for the filters to carry out the search operation");
			logger.debug("invalid input : sme id, document type id and text fields are empty");
			return response;
		}
		if (searchFilter.getPageId() <= 0) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input '" + searchFilter.getPageId()
					+ "' for page id field. Page id should be greater than 0");
			logger.debug("Invalid input '" + searchFilter.getPageId() + "' for page id field");
			return response;
		}
		if (searchFilter.getPageSize() <= 0) {
			setErrorResponse(errorCodeConfiguration.getInputerror(), response);
			response.setMessage("Invalid input '" + searchFilter.getPageSize()
					+ "' for page size field. Page size should be greater than 0");
			logger.debug("Invalid input '" + searchFilter.getPageSize() + "' for page size field");
			return response;
		}
		return response;
	}

	@Override
	public ResponseDto getDocuments(String smeId, String systemName) throws Exception {
		ResponseDto response = new ResponseDto();
		if (smeId == null || smeId.trim().isEmpty()) {
			response.setMessage("SME Id and can't be null or blank");
			response.setErrorCode(errorCodeConfiguration.getInputerror());
			response.setData(null);
			response.setStatus(apiMessageConfiguration.getFailed());
			return response;
		}
		if (systemName == null || systemName.trim().isEmpty()) {
			response.setMessage("System Name can't be null or blank");
			response.setErrorCode(errorCodeConfiguration.getInputerror());
			response.setData(null);
			response.setStatus(apiMessageConfiguration.getFailed());
			return response;
		}
		if (systemNameIdMap.get(systemName.trim().toLowerCase()) == null) {
			response.setMessage("Please enter valid system name");
			response.setErrorCode(errorCodeConfiguration.getInputerror());
			response.setData(null);
			response.setStatus(apiMessageConfiguration.getFailed());
			return response;
		}
		List<DocumentInfoResponse> docInfoList = documentDao.getDocumentInfo(smeId, systemNameIdMap.get(systemName));
		List<DocumentType> docTypeList = prepareDocumentTypeList(docInfoList);
		DocumentWrapper documentWrapper = new DocumentWrapper();
		documentWrapper.setDocType(docTypeList);
		documentWrapper.setSmeId(smeId);
		response.setData(documentWrapper);
		response.setErrorCode(0);
		response.setMessage("Success");
		response.setTotalRecords(docTypeList.size());
		response.setStatus(apiMessageConfiguration.getSuccess());
		return response;
	}

	private List<DocumentType> prepareDocumentTypeList(List<DocumentInfoResponse> docInfoList) {
		Map<Integer, DocumentType> docTypeMap = new LinkedHashMap<>();
		Map<Integer, Document> docIdMap = new HashMap<>();
		Set<String> fileIdSet = new HashSet<>();
		for (DocumentInfoResponse docInfo : docInfoList) {
			if (docTypeMap.get(docInfo.getDocTypeId()) == null) {
				// This document type was not encountered before

				DocumentType docType = prepareDocTypeEntity(docInfo);

				// add the DocumentType object to the map
				docTypeMap.put(docInfo.getDocTypeId(), docType);

				// add the fileId to the fileIdSet
				fileIdSet.add(docInfo.getFileId());

				// add the Document object to docIdSet
				docIdMap.put(docInfo.getDocId(), docType.getDocumentList().get(0));

			} else {

				// get the DocumentType object from map
				DocumentType docType = docTypeMap.get(docInfo.getDocTypeId());

				// check if docId of both object is same
				if (docIdMap.get(docInfo.getDocId()) == null) {
					// if NO then create a document object,
					// set it's fields and add it to DocumentType
					// object's documentList
					Document doc = prepareDocEntity(docInfo);

					List<Document> docList = docType.getDocumentList();
					docList.add(doc);

					// add the docId in the docIdSet
					docIdMap.put(docInfo.getDocId(), doc);
					// add the fileId in the fileId set
					fileIdSet.add(docInfo.getFileId());

				} else {

					// if YES then check if fileId is present in the fileIDSet
					// if NO then create new file object, populate fields
					// add the file object to list of file object
					// inside the versionList of Document Object
					if (!fileIdSet.contains(docInfo.getFileId())) {

						File file = new File();
						file.setFileId(docInfo.getFileId());
						file.setFileName(docInfo.getFileName());
						Document doc = docIdMap.get(docInfo.getDocId());
						List<Version> versionList = doc.getVersionList();
						Version version = versionList.get(0);
						List<File> files = version.getFiles();
						files.add(file);
						// add the fileId to the fileId set
						fileIdSet.add(docInfo.getFileId());
					} else {

						// if YES then create a new digital info object, populate its fields
						// add it to the digitalInfo list of the version list of the documentObject

						DigitizedInfo digitalInfo = new DigitizedInfo();
						digitalInfo.setKey(docInfo.getKey());
						digitalInfo.setValue(docInfo.getValue());
						Document doc = docIdMap.get(docInfo.getDocId());
						List<Version> versionList = doc.getVersionList();
						Version version = versionList.get(0);
						List<DigitizedInfo> digitalInfoList = version.getDigitalInfo();
						digitalInfoList.add(digitalInfo);

					}
				}

			}
		}

		return new ArrayList<>(docTypeMap.values());

	}

	private DocumentType prepareDocTypeEntity(DocumentInfoResponse docInfo) {
		Document doc = prepareDocEntity(docInfo);
		// create document list and add the document object to it
		List<Document> documentList = new ArrayList<>();
		documentList.add(doc);

		// create DocumentType object, set the document type id , add document list to
		// it
		DocumentType docType = new DocumentType();
		docType.setDocumentTypeId(docInfo.getDocTypeId());
		docType.setDocumentTypeName(docInfo.getDocTypeName());
		docType.setCategoryId(docInfo.getCategory());
		docType.setDocumentList(documentList);
		return docType;
	}

	private Document prepareDocEntity(DocumentInfoResponse docInfo) {
		// Prepare file object
		File file = new File();
		file.setFileId(docInfo.getFileId());
		file.setFileName(docInfo.getFileName());
		// create file list and add the object to it
		List<File> files = new ArrayList<>();
		files.add(file);

		// create digital info object
		DigitizedInfo digitalInfo = new DigitizedInfo();
		digitalInfo.setKey(docInfo.getKey());
		digitalInfo.setValue(docInfo.getValue());
		// create digital info list and add the object to it
		List<DigitizedInfo> digitalInfoList = new ArrayList<>();
		digitalInfoList.add(digitalInfo);

		// create version object, set the version no and add the digital info list and
		// file list to it
		Version version = new Version();
		version.setVersionNo(docInfo.getVersionNo());
		version.setDigitalInfo(digitalInfoList);
		version.setFiles(files);
		// create version list and add the version object to it
		List<Version> versionList = new ArrayList<>();
		versionList.add(version);

		// create document object and set doc id and version list
		Document doc = new Document();
		doc.setDocumentId(docInfo.getDocId());
		doc.setVersionList(versionList);
		return doc;
	}

	@Override
	public List<String> getCorsAllowedOrigin() {
		return allowedOriginsList;
	}

	@Override
	public ResponseDto refreshCache() {
		logger.debug("Starting cache refresh");
		logger.debug("Clearing cached data.");
		systemNameIdMap.clear();
		systemIdNameMap.clear();
		documentNameIdMap.clear();
		documentIdNameMap.clear();
		categoryNameIdMap.clear();
		categoryIdNameMap.clear();
		allowedOriginsList.clear();
		return this.loadDataInCache(true);
	}

	/**
	 * It returns the list of all document types for an application
	 * 
	 * @param systemName
	 * @return
	 */
	@Override
	public ResponseDto getLosDocumentType(String systemName) {
		try {
			logger.debug("System Name for get document type: " + systemName);
			ResponseDto rdto = new ResponseDto();
			if (systemName == null || systemName.equalsIgnoreCase("")) {
				rdto.setMessage("System Name can't be null or blank");
				rdto.setTotalRecords(0);
				rdto.setData(null);
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setStatus(apiMessageConfiguration.getFailed());
				return rdto;
			}
			rdto = vaidateSystemName(systemName, rdto);
			if (rdto.getErrorCode() != 0) {
				return rdto;
			}

			List<LosDocumentType> getDocumentTypeList = documentDao
					.getLosDocumentType(systemNameIdMap.get(systemName.toLowerCase()));
			if (getDocumentTypeList.isEmpty()) {
				rdto.setMessage("Please enter valid system name");
				rdto.setErrorCode(errorCodeConfiguration.getInputerror());
				rdto.setData(null);
				rdto.setStatus(apiMessageConfiguration.getFailed());
				return rdto;
			}

			rdto.setData(getDocumentTypeList);
			rdto.setErrorCode(0);
			rdto.setTotalRecords(getDocumentTypeList.size());
			rdto.setMessage("Success");
			rdto.setStatus(apiMessageConfiguration.getSuccess());

			return rdto;
		} catch (Exception e) {
			ResponseDto rdto = new ResponseDto();
			rdto.setData(null);
			rdto.setErrorCode(errorCodeConfiguration.getInternalserver());
			rdto.setMessage(e.getMessage());
			rdto.setStatus(apiMessageConfiguration.getFailed());
			rdto.setTotalRecords(0);
			return rdto;
		}
	}

	private Collection<DocumentType> getDocumentTypesForNewCustomer(Integer systemId) {
		List<DocTypeListProjection> docTypeListProjection = fetchDocumentsViewRepository.getDocTypeBySystemId(systemId);
		Map<Integer, List<DigitizedInfo>> infoMap = new HashMap<>();
		docTypeListProjection.forEach(projection -> {
			List<DigitizedInfo> infoList = null;
			if (infoMap.containsKey(projection.getDocTypeId())) {
				infoList = infoMap.get(projection.getDocTypeId());
			} else {
				infoList = new ArrayList<>();
			}

			if (projection.getKey() != null && projection.getType() != null) {
				DigitizedInfo info = new DigitizedInfo();
				info.setKey(projection.getKey());
				info.setType(projection.getType());
				info.setOrder(projection.getOrder());
				infoList.add(info);
				Collections.sort(infoList);
				infoMap.put(projection.getDocTypeId(), infoList);
			}
		});
		Map<Integer, DocumentType> documentTypeMap = new TreeMap<>();
		docTypeListProjection.forEach(dtl -> {
			List<Document> documentList = new ArrayList<>();
			Document document = new Document();
			document.setDocumentId(0);
			document.setSystemId(systemId);
			List<Version> versionList = new ArrayList<>();
			Version version = new Version();
			version.setVersionNo(0);
			version.setFiles(new ArrayList<>());
			version.setDigitalInfo(
					infoMap.containsKey(dtl.getDocTypeId()) ? infoMap.get(dtl.getDocTypeId()) : new ArrayList<>());
			Verification verification = new Verification();
			verification.setStatus("");
			verification.setUpdatedBy("");
			verification.setSystemName("");
			version.setVerification(verification);
			version.setCreatedOn(null);
			versionList.add(version);
			document.setVersionList(versionList);

			document.setComments(new ArrayList<>());
			documentList.add(document);

			DocumentType dt = new DocumentType();
			dt.setDocumentTypeId(dtl.getDocTypeId());
			dt.setDocumentTypeName(dtl.getDocTypeName());
			dt.setCategoryId(dtl.getCategoryId());
			dt.setDocumentList(documentList);
			documentTypeMap.put(dtl.getDocTypeId(), dt);
		});
		return documentTypeMap.values();
	}

	/**
	 * @author mohd.tausif@power2sme.com
	 */
	@Override
	public ResponseDto getDocuments(GetDocumentsRequestDto requestDto) {
		ResponseDto responseDto = new ResponseDto();
		DocumentWrapper documentWrapper = new DocumentWrapper();
		documentWrapper.setSmeId(requestDto.getSmeId());
		List<FetchDocumentsViewEntity> items = new ArrayList<>();
		if (requestDto.getIsNewCustomer() != null && requestDto.getIsNewCustomer()) {
			List<DocumentType> documentTypeList = new ArrayList<>(
					getDocumentTypesForNewCustomer(requestDto.getSystemId()));
			documentWrapper.setDocType(documentTypeList);
			responseDto.setData(documentWrapper);
			return responseDto;
		} else {
			if (requestDto.getDocTypeListItem() != null && requestDto.getDocTypeListItem().size() > 0) {
				Map<Integer, Integer> table = new HashMap<>();
				requestDto.getDocTypeListItem().forEach(dto -> {
					if (dto.getDocId() != null && dto.getVersionNo() != null) {
						table.put(dto.getDocId(), dto.getVersionNo());
					}
				});

				List<FetchDocumentsViewEntity> itemsWithAllVersions = documentDao
						.findAllBySmeIdAndSystemIdAndDocTypeIdIn(requestDto.getSmeId(), requestDto.getSystemId(),
								requestDto.getDocTypeListItem());
				items = itemsWithAllVersions.stream().filter(ent -> {
					if (table.containsKey(ent.getDocId())) {
						return table.get(ent.getDocId()).equals(ent.getVersionNo());
					}
					return false;
				}).collect(Collectors.toList());
			} else {
				items = documentDao.findAllBySmeIdAndSystemId(requestDto.getSmeId(), requestDto.getSystemId());
				if (items == null || items.size() == 0) {
					List<DocumentType> documentTypeList = new ArrayList<>(
							getDocumentTypesForNewCustomer(requestDto.getSystemId()));
					documentWrapper.setDocType(documentTypeList);
					responseDto.setData(documentWrapper);
					return responseDto;
				}
			}
		}

		Map<Integer, Set<Comments>> commentsMap = new HashMap<>();
		Map<Integer, Set<File>> filesMap = new HashMap<>();
		Map<Integer, Set<DigitizedInfo>> digitalInfoMap = new HashMap<>();
		Map<Integer, Set<Verification>> verificationMap = new HashMap<>();
		Map<Integer, Set<Document>> documentMap = new HashMap<>();
		Map<Integer, DocumentType> documentTypeMap = new TreeMap<>();

		for (FetchDocumentsViewEntity documentEntity : items) {
			if (documentWrapper.getSystemName() == null) {
				documentWrapper.setSystemName(documentEntity.getSystemName());
//				documentWrapper.setUpdatedBy(updatedBy);
			}

			if (!documentTypeMap.containsKey(documentEntity.getDocTypeId())) {
				DocumentType documentType = new DocumentType();
				documentType.setDocumentTypeId(documentEntity.getDocTypeId());
				documentType.setDocumentTypeName(documentEntity.getDocTypeName());
				documentType.setCategoryId(documentEntity.getCategoryId());
				documentType.setMaxDocuments(documentEntity.getMaxDocuments());
				documentTypeMap.put(documentEntity.getDocTypeId(), documentType);
			}

			Document document = new Document();
			document.setDocumentId(documentEntity.getDocId());
			document.setVersionNo(documentEntity.getVersionNo());
			document.setId(documentEntity.getDocumentsPk());
			document.setDocumentTypeId(documentEntity.getDocTypeId());
			document.setCreatedOn(documentEntity.getDocumentsCreatedOn());
			if (documentMap.containsKey(documentEntity.getDocTypeId())) {
				Set<Document> documentList = documentMap.get(documentEntity.getDocTypeId());
				documentList.add(document);
				documentMap.put(documentEntity.getDocTypeId(), documentList);
			} else {
				Set<Document> documentList = new HashSet<>();
				documentList.add(document);
				documentMap.put(documentEntity.getDocTypeId(), documentList);
			}

			if (documentEntity.getFilePk() != null && documentEntity.getFilePk() > 0)// files
			{
				File file = new File();
				file.setFileId(documentEntity.getFileId());
				file.setFileName(documentEntity.getFileName());
				file.setFileSize(documentEntity.getFileSize());
				file.setFileModifiedAt(documentEntity.getFileModifiedAt());
				if (filesMap.containsKey(documentEntity.getDocumentsPk())) {
					Set<File> fileList = filesMap.get(documentEntity.getDocumentsPk());
					fileList.add(file);
					filesMap.put(documentEntity.getDocumentsPk(), fileList);
				} else {
					Set<File> fileList = new HashSet<>();
					fileList.add(file);
					filesMap.put(documentEntity.getDocumentsPk(), fileList);
				}
			}

			if (documentEntity.getDigitalinfoPk() != null && documentEntity.getDigitalinfoPk() > 0) {
				DigitizedInfo digitizedInfo = new DigitizedInfo();
				digitizedInfo.setDigitizedInfoId(documentEntity.getDigitalinfoPk());
				digitizedInfo.setKey(documentEntity.getKey());
				digitizedInfo.setValue(documentEntity.getValue());
				digitizedInfo.setType(documentEntity.getType());
				digitizedInfo.setOrder(documentEntity.getOrder());
				if (digitalInfoMap.containsKey(documentEntity.getDocumentsPk())) {
					Set<DigitizedInfo> digitalinfoList = digitalInfoMap.get(documentEntity.getDocumentsPk());
					digitalinfoList.add(digitizedInfo);
					digitalInfoMap.put(documentEntity.getDocumentsPk(), digitalinfoList);
				} else {
					Set<DigitizedInfo> digitalinfoList = new HashSet<>();
					digitalinfoList.add(digitizedInfo);
					digitalInfoMap.put(documentEntity.getDocumentsPk(), digitalinfoList);
				}
			}

			if (documentEntity.getVerificationPk() != null && documentEntity.getVerificationPk() > 0) {
				Verification verification = new Verification();
				verification.setVerificationId(documentEntity.getVerificationPk());
				verification.setSystemId(documentEntity.getSystemId());
				verification.setStatus(documentEntity.getStatus());
				verification.setUpdatedBy(documentEntity.getUpdatedBy());
				verification.setSystemName(documentEntity.getSystemName());
				if (verificationMap.containsKey(documentEntity.getDocumentsPk())) {
					Set<Verification> verificationList = verificationMap.get(documentEntity.getDocumentsPk());
					verificationList.add(verification);
					verificationMap.put(documentEntity.getDocumentsPk(), verificationList);
				} else {
					Set<Verification> digitalinfoList = new HashSet<>();
					digitalinfoList.add(verification);
					verificationMap.put(documentEntity.getDocumentsPk(), digitalinfoList);
				}
			}

			if (documentEntity.getCommentPk() != null && documentEntity.getCommentPk() > 0) {
				Comments comments = new Comments();
				comments.setCommentId(documentEntity.getCommentPk());
				comments.setComment(documentEntity.getComment());

				comments.setCreatedBy(documentEntity.getCommented_by());
				comments.setCreatedTime(documentEntity.getCreated_time());
				comments.setSystemName(documentEntity.getSystemName());
				comments.setSystemId(documentEntity.getSystemId());
				comments.setAction(documentEntity.getOn_action());
				if (commentsMap.containsKey(documentEntity.getDocumentsPk())) {
					Set<Comments> commentsList = commentsMap.get(documentEntity.getDocumentsPk());
					commentsList.add(comments);
					commentsMap.put(documentEntity.getDocumentsPk(), commentsList);
				} else {
					Set<Comments> commentsList = new HashSet<>();
					commentsList.add(comments);
					commentsMap.put(documentEntity.getDocumentsPk(), commentsList);
				}
			}
		}

		List<DocumentType> documentTypeList = new ArrayList<>(documentTypeMap.values());
		for (DocumentType documentType : documentTypeList) {
			if (documentMap.containsKey(documentType.getDocumentTypeId())) {
				Set<Document> documentList = documentMap.get(documentType.getDocumentTypeId());

				for (Document document : documentList) {
					List<Version> versionList = new ArrayList<>();
					Version version = new Version();
					version.setVersionNo(document.getVersionNo());
					version.setCreatedOn(document.getCreatedOn());
					document.setCreatedOn(null);
					if (filesMap.containsKey(document.getId())) {
						Set<File> filesList = filesMap.get(document.getId());
						version.setFiles(new ArrayList(filesList));
					}

					if (digitalInfoMap.containsKey(document.getId())) {
						Set<DigitizedInfo> digitalInfoSet = digitalInfoMap.get(document.getId());
						List<DigitizedInfo> digitalInfoList = new ArrayList<>(digitalInfoSet);
						Collections.sort(digitalInfoList);
						version.setDigitalInfo(digitalInfoList);
					}

					if (verificationMap.containsKey(document.getId())) {
						Set<Verification> verificationSet = verificationMap.get(document.getId());
						List<Verification> verificationList = new ArrayList(verificationSet);
						Collections.sort(verificationList);
						version.setVerification(verificationList.get(0));
					}
					versionList.add(version);
					document.setVersionList(versionList);

					if (commentsMap.containsKey(document.getId())) {
						Set<Comments> commentsList = commentsMap.get(document.getId());
						document.setComments(new ArrayList(commentsList));
					}
					document.setId(null);
					document.setDocumentTypeId(null);
				}
				documentType.setDocumentList(new ArrayList(documentList));
			}
		}

		documentWrapper.setDocType(documentTypeList);
		responseDto.setData(documentWrapper);
		return responseDto;
	}

	@Override
	public ResponseDto getDefaultDocuments(Integer systemId) {
		log.debug("inside getDefaultDocumemt method() :::");
		ResponseDto responseDto = new ResponseDto();
		List<DocumentType> documentTypeList = new ArrayList<>(getDocumentTypesForNewCustomer(systemId));
		responseDto.setData(documentTypeList);
		responseDto.setMessage("Documents fetched successfully");
		return responseDto;
	}

	private StringBuilder logMergeDocs(StringBuilder logBuf, String msg) {
		log.debug(msg);
		logBuf.append("\n" + msg);
		return logBuf;
	}

	@Override
	public String mergeDocs(String customerId, Integer targetDocTypeId, List<Integer> srcDocTypeIds) {
		StringBuilder logBuf = new StringBuilder();
		if (customerId != null) {
			logBuf = logMergeDocs(logBuf, "Processing sme id=" + customerId + "::Start");
			String logStr = mergeDocs(targetDocTypeId, srcDocTypeIds, customerId, null);
			logBuf = logMergeDocs(logBuf, logStr);
			logBuf = logMergeDocs(logBuf, "Processing sme id=" + customerId + "::End");
		} else {
			List<MergedocSmeIdEntity> smeIds = mergedocSmeIdRepository.findAllByStatus(0);
			for (MergedocSmeIdEntity mergedocSmeIdEntity : smeIds) {
				logBuf = logMergeDocs(logBuf, "Processing sme id=" + mergedocSmeIdEntity.getSmeId() + "::Start");
				String logStr = mergeDocs(targetDocTypeId, srcDocTypeIds, mergedocSmeIdEntity.getSmeId(),
						mergedocSmeIdEntity);
				logBuf = logMergeDocs(logBuf, logStr);
				logBuf = logMergeDocs(logBuf, "Processing sme id=" + mergedocSmeIdEntity.getSmeId() + "::End");
				logBuf = new StringBuilder("");
			}
		}
		return logBuf.toString();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private String mergeDocs(Integer targetDocTypeId, List<Integer> srcDocTypeIds, String smeId,
			MergedocSmeIdEntity mergedocSmeIdEntity) {
		StringBuilder logBuf = new StringBuilder();
		DocumentType targetDocumentType = null;
		Map<Integer, DocumentType> srcDocTypeIdsMap = new HashMap<>();
		for (Integer srcDocTypeId : srcDocTypeIds) {
			srcDocTypeIdsMap.put(srcDocTypeId, null);
		}

		GetDocumentsRequestDto requestDto = new GetDocumentsRequestDto();
		requestDto.setIsNewCustomer(false);
		requestDto.setSmeId(smeId);
		requestDto.setSystemId(1);
		ResponseDto getDocApiResponseDto = getDocuments(requestDto);
		if (getDocApiResponseDto != null && getDocApiResponseDto.getData() instanceof DocumentWrapper) {
			DocumentWrapper documentWrapper = (DocumentWrapper) getDocApiResponseDto.getData();
			List<DocumentType> documentTypes = documentWrapper.getDocType();
			for (DocumentType documentType : documentTypes) {
				if (srcDocTypeIdsMap.containsKey(documentType.getDocumentTypeId())) {
					srcDocTypeIdsMap.put(documentType.getDocumentTypeId(), documentType);
				} else if (documentType.getDocumentTypeId().equals(targetDocTypeId)) {
					targetDocumentType = documentType;
				}
			}

			if (targetDocumentType == null) {
				targetDocumentType = new DocumentType();
				targetDocumentType.setDocumentTypeId(targetDocTypeId);
				logBuf = logMergeDocs(logBuf, "targetDocTypeId '" + targetDocTypeId + "' not found in SME Id=" + smeId
						+ " so adding target docType in sme.");
			}

			List<Document> srcDocContainerList = new ArrayList<>();
			Iterator<Entry<Integer, DocumentType>> srcDocTypeIdsMapItr = srcDocTypeIdsMap.entrySet().iterator();
			while (srcDocTypeIdsMapItr.hasNext()) {
				Entry<Integer, DocumentType> entry = srcDocTypeIdsMapItr.next();
				DocumentType documentType = entry.getValue();
				if (documentType != null) {
					logBuf = logMergeDocs(logBuf,
							"moving documents of documentTypeId=" + documentType.getDocumentTypeId());
					List<Document> documents = documentType.getDocumentList();
					for (Document document : documents) {
						logBuf = logMergeDocs(logBuf, "moving docId=" + document.getDocumentId());
						document = this.copyDocument(document, targetDocumentType, smeId, 1);
						srcDocContainerList.add(document);
					}
				}
			}

			if (srcDocContainerList.size() > 0) {
				targetDocumentType.setDocumentList(srcDocContainerList);

				// save 'targetDocumentType' here
				List<DocumentType> toUpdateDocumentTypes = new ArrayList<>();
				toUpdateDocumentTypes.add(targetDocumentType);
				documentWrapper.setDocType(toUpdateDocumentTypes);
				logBuf = logMergeDocs(logBuf, "Final Update Payload=" + documentWrapper);
				ResponseDto updateResponseDto = updateDocument(documentWrapper);
				logBuf = logMergeDocs(logBuf, "Final Update Response=" + updateResponseDto);
			} else {
				logBuf = logMergeDocs(logBuf,
						"Src doc_type_id(s) not found in smeId=" + smeId + " , so skipping update");
			}
		}

		updateMergeDocStatus(mergedocSmeIdEntity, logBuf.toString());

		return logBuf.toString();
	}

	private Document copyDocument(Document document, DocumentType targetDocumentType, String smeId, Integer systemId) {
		document.setId(null);
		document.setDocumentId(null);
		document.setVersionNo(null);
		document.setDocumentTypeId(targetDocumentType.getDocumentTypeId());
		document.setDocumentName(targetDocumentType.getDocumentTypeName());
		document.setSmeId(smeId);
		document.setSystemId(systemId);
		if (document.getVersionList() != null && document.getVersionList().size() > 0) {
			if (document.getVersionList().get(0).getFiles() != null) {
				List<File> verifiedFiles = new ArrayList<>();
				for (File file : document.getVersionList().get(0).getFiles()) {
					if (file.getFileId() != null && file.getFileId().length() > 0) {
						verifiedFiles.add(file);
					}
				}
				document.setFiles(verifiedFiles);
			}

			if (document.getVersionList().get(0).getDigitalInfo() != null) {
				List<DigitizedInfo> verifiedDigitizedInfo = new ArrayList<>();
				for (DigitizedInfo digInfo : document.getVersionList().get(0).getDigitalInfo()) {
					if (!(digInfo.getKey() == null || digInfo.getKey().isEmpty() || digInfo.getType() == null
							|| digInfo.getType().isEmpty())) {
						verifiedDigitizedInfo.add(digInfo);
					}
				}
				document.setDigitalInfoList(verifiedDigitizedInfo);
			}

			if (document.getVersionList().get(0).getVerification() != null) {
				Verification verif = document.getVersionList().get(0).getVerification();
				if (!(verif.getStatus() == null || verif.getStatus().isEmpty() || verif.getUpdatedBy() == null
						|| verif.getUpdatedBy().isEmpty())) {
					List<Verification> verificationList = new ArrayList<>();
					verificationList.add(document.getVersionList().get(0).getVerification());
					document.setVerification(verificationList);
				}
			}
		}
		return document;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void updateMergeDocStatus(MergedocSmeIdEntity mergedocSmeIdEntity, String logs) {
		if (mergedocSmeIdEntity != null) {
			mergedocSmeIdEntity.setStatus(1);
			mergedocSmeIdEntity.setCreatedDate(new java.util.Date());
			mergedocSmeIdEntity.setLog(logs);
			mergedocSmeIdRepository.save(mergedocSmeIdEntity);
		}
	}

	@Override
	public ResponseDto getDocMetadat(String smeId) {
		ResponseDto responseDto = new ResponseDto();
		List<DocumentsMetadataProjection> docsMetadata = fetchDocumentsViewRepository.getDocMetadata(smeId);
		List<DocumentsMetadataListItemDto> docsMetadataList = docsMetadata.stream().map(d -> {
			DocumentsMetadataListItemDto dto = new DocumentsMetadataListItemDto();
			dto.setDocumentTypeName(d.getDocTypeName());
			dto.setDocumentTypeId(d.getDocTypeId());
			dto.setFilesCount(d.getFileCount());
			dto.setRecentUploadedOn(d.getFileCreatedDate());
			return dto;
		}).collect(Collectors.toList());
		responseDto.setData(docsMetadataList);
		responseDto.setMessage("Documents metadata fetched successfully");
		return responseDto;
	}

	@Override
	public List<ResponseDto> copyDocuments(List<String> sourceSmeId, List<String> targetSmeId, String systemName)
			throws Exception {
		List<ResponseDto> responseDtos = new ArrayList<>();
		for (int i = 0; i < sourceSmeId.size(); i++) {
			ResponseDto responseDto = new ResponseDto();
			GetDocumentsRequestDto req = new GetDocumentsRequestDto();
			req.setSmeId(sourceSmeId.get(i));
			req.setSystemId(systemNameIdMap.get(systemName));
			ResponseDto getDocApiResponseDto = this.getDocuments(req);
			if (getDocApiResponseDto != null && getDocApiResponseDto.getData() instanceof DocumentWrapper) {
				DocumentWrapper documentWrapper = (DocumentWrapper) getDocApiResponseDto.getData();
				List<DocumentType> documentTypes = documentWrapper.getDocType();
				if (documentTypes != null && documentTypes.size() > 0) {
					List<DocumentType> toUpdateDocumentTypes = new ArrayList<>();
					for (DocumentType documentType : documentTypes) {
						if (documentType != null) {
							List<Document> documents = new ArrayList<>();
							for (Document document : documentType.getDocumentList()) {
								document = this.copyDocument(document, documentType, targetSmeId.get(i),
										systemNameIdMap.get(systemName));
								documents.add(document);
							}
							if (documents.size() > 0) {
								documentType.setDocumentList(documents);
								toUpdateDocumentTypes.add(documentType);
							}
						}
					}
					documentWrapper.setDocType(toUpdateDocumentTypes);
					documentWrapper.setSystemName(systemName);
					documentWrapper.setSmeId(targetSmeId.get(i));
					responseDto = this.updateDocument(documentWrapper);
				} else {
					responseDto.setMessage("No Documents found for source sme id.");
					responseDto.setStatus("Failed");
					responseDto.setErrorCode(400);
				}
			} else {
				responseDto = getDocApiResponseDto;
			}
			responseDtos.add(responseDto);
		}
		return responseDtos;
	}
}