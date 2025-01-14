package com.power2sme.dms.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.power2sme.dms.config.properties.FilePathConfiguration;
import com.power2sme.dms.emails.DmsEmailService;
import com.power2sme.dms.entity.DocumentType;
import com.power2sme.dms.entity.DocumentWrapper;
import com.power2sme.dms.entity.DownloadDocumentRequestDto;
import com.power2sme.dms.entity.ZipDocumentsId;
import com.power2sme.dms.entity.ZipWrapper;
import com.power2sme.dms.externaldto.ResponseDto;
import com.power2sme.dms.externaldto.ZipDocumentResponseDto;
import com.power2sme.dms.externaldto.ZippingResponseDto;
import com.power2sme.dms.utils.DmsLogUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DownloadZipService {

	private final DocumentServiceImpl documentServiceImpl;

	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	private final Map<String, Future<Boolean>> zipProcessStorage = new HashMap<>();

	private final DocumentZipService documentZipService;

	private final FilePathConfiguration filePathConfiguration;

	private ExecutorService zipDocumentExecutor = Executors.newSingleThreadExecutor();

	private DmsEmailService dmsEmailService;

	@Autowired
	public DownloadZipService(DocumentServiceImpl documentServiceImpl, FilePathConfiguration filePathConfiguration,
			DocumentZipService documentZipService, DmsEmailService dmsEmailService) {
		this.documentServiceImpl = documentServiceImpl;
		this.filePathConfiguration = filePathConfiguration;
		this.documentZipService = documentZipService;
		this.dmsEmailService = dmsEmailService;
	}

	/**
	 * this method will return zipurl for downloading zipfile
	 * 
	 * @param smeId
	 * @param systemName
	 * @param httpServletRequest
	 * @return zipurl
	 * @throws Exception
	 */
	public String zipUrl(final String smeId, final String systemName, HttpServletRequest httpServletRequest)
			throws Exception {
		// String smeNameReplaced = smeName.replace(" ", "_").replace(".", "");
//		String zipFolderPath = filePathConfiguration.getFile() + "zip/" + systemName + "/" + smeId + "/" + smeId;
		String zipFolderPath = filePathConfiguration.getFile() + "zipdocument/" + systemName + "/" + smeId + "/"
				+ smeId;
		DmsLogUtil.logAtDebug(log, "Zip folder path ::" + zipFolderPath);
		String zipFilePath = zipFolderPath + ".zip";
		DmsLogUtil.logAtDebug(log, "Zip file path::" + zipFilePath);
		if (Objects.isNull(zipProcessStorage.get(smeId + "-" + systemName))) {
			java.io.File zipFile = new java.io.File(zipFilePath);
			if (!zipFile.exists()) {
				DmsLogUtil.logAtDebug(log,
						"zipProcessStorage is null and zip file does not exist so going to update zip file at path ::"
								+ zipFile);
				updateZipAsync(smeId, systemName);
				zipProcessStorage.get(smeId + "-" + systemName).get();
			}
		} else {
			zipProcessStorage.get(smeId + "-" + systemName).get();
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(httpServletRequest.getScheme());
		stringBuilder.append("://");
		stringBuilder.append(httpServletRequest.getLocalName() + ":");
		stringBuilder.append(httpServletRequest.getLocalPort());
		stringBuilder.append(httpServletRequest.getContextPath());
		stringBuilder.append("/files/zip/");
		stringBuilder.append(systemName);
		stringBuilder.append("/");
		stringBuilder.append(smeId);
		stringBuilder.append("/");
		stringBuilder.append(smeId);
		stringBuilder.append(".zip");
		DmsLogUtil.logAtDebug(log, "Final url of zip downlaod::" + stringBuilder.toString());
		return stringBuilder.toString();
	}

	public void updateZipAsync(final String smeId, final String systemName) throws Exception {
		Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				DmsLogUtil.logAtDebug(log, "Inside updateZipAsync method");
				updateZip(smeId, systemName);
				DmsLogUtil.logAtDebug(log, "Zip has been completed!!");
				return true;
			}
		});
		zipProcessStorage.put(smeId + "-" + systemName, future);
	}

	private void updateZip(String smeId, String systemName) throws Exception {
		ResponseDto rdto = documentServiceImpl.getDocuments(smeId, null, null, null, systemName);
		DmsLogUtil.logAtDebug(log, "response of getDocuments ::" + rdto);
		List<DocumentType> documentTypeList = new ArrayList<>();
		ZipWrapper zipWrapper = new ZipWrapper();
		Object object = rdto.getData();
		DocumentWrapper documentWrapper = null;
		if (object instanceof DocumentWrapper) {
			documentWrapper = (DocumentWrapper) object;
			documentTypeList = documentWrapper.getDocType();
			DmsLogUtil.logAtDebug(log, "Document Type List::" + documentTypeList);
			List<ZipDocumentsId> zipDocumentList = new ArrayList<>();

			// Here we have implemented logic for creating zipWrapper first we
			// checked value of documentId if this is greater than zero then it will
			// be possible that it contains some file so for this we check the
			// length of fileList if it is not empty then add file's version number
			// in zipWrapper along with documentTypeId and documentId.
			Integer i = 0;
			for (DocumentType docTypeList : documentTypeList) {
				DmsLogUtil.logAtDebug(log, "Size of doxumentList :: " + docTypeList.getDocumentList().size());
				while (i < docTypeList.getDocumentList().size()) {
					if ((docTypeList.getDocumentList().get(i).getDocumentId() > 0)
							&& (!docTypeList.getDocumentList().get(i).getVersionList().get(0).getFiles().isEmpty())) {
						ZipDocumentsId zipDocumentsId = new ZipDocumentsId();
						zipDocumentsId.setDocumentTypeId(docTypeList.getDocumentTypeId());
						zipDocumentsId.setDocumentId(docTypeList.getDocumentList().get(i).getDocumentId());
						zipDocumentsId.setVersionNo(
								docTypeList.getDocumentList().get(i).getVersionList().get(0).getVersionNo());
						zipDocumentList.add(zipDocumentsId);
						DmsLogUtil.logAtDebug(log,
								"Zip Document id which has been added to list of zipDocumentList::" + zipDocumentsId);
					}
					i++;
				}
				i = 0;
			}
			zipWrapper.setSmeId(smeId);
			zipWrapper.setZipDocumentList(zipDocumentList);
			DmsLogUtil.logAtDebug(log, "ZipWrapper:::" + zipWrapper);
			documentZipService.zipFile(zipWrapper, systemName);
		} else {
			throw new Exception("Incompatible response type");
		}
	}

	public ZippingResponseDto zipDocuments(String smeId, String systemName, String emailId,
			HttpServletRequest httpServletRequest) throws Exception {
		String zipPath = generateZipPath(smeId, systemName, emailId, httpServletRequest);
		Future<ZipDocumentResponseDto> futureResponseDto = processZippingDocs(smeId, systemName, emailId, zipPath);
		return ZippingResponseDto.builder()
				.message("Your request to download documents zip of customer '" + smeId
						+ "' received successfully, we will notify you by email on your email id '" + emailId
						+ "' when zip will be ready to download.")
				.build();
	}

	private Future<ZipDocumentResponseDto> processZippingDocs(String smeId, String systemName, String emailId,
			String zipPath) {
		return zipDocumentExecutor.submit(() -> {
			log.debug("zipDocuments::zipping started");
			updateZip(smeId, systemName);
			log.debug("zipDocuments::zip completed");

			ZipDocumentResponseDto responseDto = new ZipDocumentResponseDto();
			responseDto.setEmailId(emailId);
			responseDto.setZipLocation(zipPath);
			responseDto.setSmeId(smeId);
			dmsEmailService.sendMailOnZippingCompleted(responseDto);
			return responseDto;
		});
	}

	private String generateDownloadDocumentsZipPath(DownloadDocumentRequestDto requestDto,String zipFolderName,
			HttpServletRequest httpServletRequest) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(httpServletRequest.getScheme());
		stringBuilder.append("://");
		stringBuilder.append(httpServletRequest.getLocalName() + ":");
		stringBuilder.append(httpServletRequest.getLocalPort());
		stringBuilder.append(httpServletRequest.getContextPath());
		stringBuilder.append("/files/zipdocument/");
		stringBuilder.append(requestDto.getSystemName());
		stringBuilder.append("/");
		stringBuilder.append(requestDto.getSmeId());
		stringBuilder.append("/");
		stringBuilder.append(zipFolderName);
		stringBuilder.append(".zip");
		log.debug("Final url of zip downlaod::" + stringBuilder.toString());
		return stringBuilder.toString();
	}
	
	private String generateZipPath(String smeId, String systemName, String emailId,
			HttpServletRequest httpServletRequest) {
//		String zipFolderPath = filePathConfiguration.getFile() + "zip/" + systemName + "/" + smeId + "/" + smeId;
		String zipFolderPath = filePathConfiguration.getFile() + "zipdocument/" + systemName + "/" + smeId + "/"
				+ smeId;
		log.debug("Zip folder path ::" + zipFolderPath);
		String zipFilePath = zipFolderPath + ".zip";
		log.debug("Zip file path::" + zipFilePath);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(httpServletRequest.getScheme());
		stringBuilder.append("://");
		stringBuilder.append(httpServletRequest.getLocalName() + ":");
		stringBuilder.append(httpServletRequest.getLocalPort());
		stringBuilder.append(httpServletRequest.getContextPath());
		stringBuilder.append("/files/zipdocument/");
		stringBuilder.append(systemName);
		stringBuilder.append("/");
		stringBuilder.append(smeId);
		stringBuilder.append("/");
		stringBuilder.append(smeId);
		stringBuilder.append(".zip");
		log.debug("Final url of zip downlaod::" + stringBuilder.toString());
		return stringBuilder.toString();
	}

	public ZippingResponseDto downloadDocuments(@RequestBody DownloadDocumentRequestDto requestDto,
			HttpServletRequest httpServletRequest) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
		String formattedDateTime = sdf.format(new Date());
		String zipFolderName = requestDto.getSmeId()+"_"+formattedDateTime;
		
		String zipPath = generateDownloadDocumentsZipPath(requestDto, zipFolderName, httpServletRequest);

		if (requestDto.getEmailId() != null && requestDto.getEmailId().trim().length() > 0) {
			Future<ZippingResponseDto> futureRes = zipDocumentExecutor.submit(() -> {
				log.debug("zipDocuments::async zipping started");

				documentZipService.zipLosAndDmsFiles(requestDto, zipFolderName);

				log.debug("zipDocuments::async zip completed");
				ZippingResponseDto.ZippingResponseDtoBuilder zippingResponseDtoBuilder = ZippingResponseDto.builder();
				ZipDocumentResponseDto responseDto = new ZipDocumentResponseDto();
				responseDto.setEmailId(requestDto.getEmailId());
				responseDto.setZipLocation(zipPath);
				responseDto.setSmeId(requestDto.getSmeId());
				dmsEmailService.sendMailOnZippingCompleted(responseDto);

				zippingResponseDtoBuilder
						.message("Your request to download documents zip of customer '" + requestDto.getSmeId()
								+ "' received successfully, we will notify you by email on your email id '"
								+ requestDto.getEmailId() + "' when zip will be ready to download.");
				return zippingResponseDtoBuilder.build();
			});
			return futureRes.get();
		} else {
			log.debug("zipDocuments::sync zipping started");

			documentZipService.zipLosAndDmsFiles(requestDto, zipFolderName);

			log.debug("zipDocuments::sync zip completed");
			ZippingResponseDto.ZippingResponseDtoBuilder zippingResponseDtoBuilder = ZippingResponseDto.builder();
			zippingResponseDtoBuilder.message(zipPath);
			return zippingResponseDtoBuilder.build();
		}
	}
}
