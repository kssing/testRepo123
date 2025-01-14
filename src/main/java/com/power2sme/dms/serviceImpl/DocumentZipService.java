package com.power2sme.dms.serviceImpl;

import java.util.*;

import javax.annotation.PostConstruct;

import com.power2sme.dms.entity.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

import com.power2sme.dms.config.properties.FilePathConfiguration;
import com.power2sme.dms.dao.DocumentDao;
import com.power2sme.dms.repository.DocumentsRepository;
import com.power2sme.dms.repository.DocumentsRepository.LosZipDocProjection;
import com.power2sme.dms.utils.DmsLogUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentZipService {
	
	@Autowired
	private DocumentDao documentDao;
	
	@Autowired
	DocumentsRepository documentsRepository;
	
	@Autowired
	private FilePathConfiguration filePathConfiguration;

	@Value("${dms.downloadAll.zip.ttl}")
	private Integer zipTtl;

	
	private HashMap<String, Integer> systemNameIdMap = new HashMap<>();
	private HashMap<Integer, String> systemIdNameMap = new HashMap<>();

	private HashMap<String, Integer> documentNameIdMap = new HashMap<>();
	private HashMap<Integer, String> documentIdNameMap = new HashMap<>();
	
	@PostConstruct
	public void initialize() {
		try {
			loadAllSystemDetails();
			loadAllDocumentTypeDetails();
		} catch (Exception e) {
			DmsLogUtil.logAtError(log,"Error in initialize:" + e );
		}
	}

	
	private void loadAllSystemDetails() {
		List<SystemIdentity> ls = documentDao.getSystemDetail();
		for (SystemIdentity si : ls) {
			systemNameIdMap.put(si.getSystemName(), si.getSystemId());
			systemIdNameMap.put(si.getSystemId(), si.getSystemName());

		}
	}

	
	private void loadAllDocumentTypeDetails() {
		List<DocumentType> ls = documentDao.getDocumentTypes();
		for (DocumentType dt : ls) {
			documentNameIdMap.put(dt.getDocumentTypeName(), dt.getDocumentTypeId());
			documentIdNameMap.put(dt.getDocumentTypeId(), dt.getDocumentTypeName());

		}
	}
	
	public void zipFile(ZipWrapper zipWrapper, String systemName) {
		try {
			//SINCE DMS IS NOT HAVING SME NAME WE WILL ASSUME IT TO BE SMEID
			String smeName = zipWrapper.getSmeId();
			DmsLogUtil.logAtDebug(log, "Inside zipFile method");
			String zipFolderPath = filePathConfiguration.getFile()+"zipdocument/"+systemName+"/"+zipWrapper.getSmeId()+"/"+smeName;
			DmsLogUtil.logAtDebug(log, "zip folder path :::" + zipFolderPath);
			String zipFilePath = zipFolderPath+".zip";	
			DmsLogUtil.logAtDebug(log, "zip file path ::" + zipFilePath);
			java.io.File zipFolder = new java.io.File(zipFolderPath);
			DmsLogUtil.logAtDebug(log, "zip folder has been created , path of zipfolder::" + zipFolder);
			if(zipFolder.exists()){
				DmsLogUtil.logAtDebug(log, "zip older already exist so deleting this zip folder ::" + zipFolder);
				FileUtils.deleteDirectory(zipFolder);
				DmsLogUtil.logAtDebug(log, "Flag for folder ::" + zipFolder.exists());
			}
			zipFolder.mkdirs();
			DmsLogUtil.logAtDebug(log, "zip folder directory has been created::" + zipFolder);
			java.io.File zipFile = new java.io.File(zipFilePath);
			if(zipFile.exists()){
				DmsLogUtil.logAtDebug(log, "zip file already exist so we are going to delte it , zip file path (before deletinging::)" + zipFilePath);
				FileUtils.forceDelete(zipFile);
				DmsLogUtil.logAtDebug(log, "Flag for file::" + zipFile.exists());
			}
			List<ZipDocumentsId> zipDocumentsList  =  zipWrapper.getZipDocumentList();
			DmsLogUtil.logAtDebug(log, "Zip document List: "+ zipDocumentsList.toString());
			for(int i=0;i<zipDocumentsList.size();i++){
				List<Document> documentList = documentDao.getParentId(zipDocumentsList.get(i),zipWrapper.getSmeId());
				if(documentList.isEmpty()){
					break;
				}
				List<File> fileList = new ArrayList<File>();
				for(int j=0;j<documentList.size();j++){
					DmsLogUtil.logAtDebug(log,"Parent id :: "+ documentList.get(j).getId());
					List<File> files = documentDao.getFiles(documentList.get(j).getId());
					fileList.addAll(files);
				}
				
				DmsLogUtil.logAtDebug(log, "File list: "+ fileList.toString());

				String docTypeName = documentIdNameMap.get(zipDocumentsList.get(i).getDocumentTypeId());
				DmsLogUtil.logAtDebug(log,"Document Type Id: "+ zipDocumentsList.get(i).getDocumentTypeId() + " Document Type Name: "+ docTypeName);
				String documentTypeFolderPath = zipFolderPath+ "/"+docTypeName.trim().replace(" ", "_");
				java.io.File documentTypeFolder = new java.io.File(documentTypeFolderPath);
				documentTypeFolder.mkdir();
				for(int j=0;j<fileList.size();j++){
					java.io.File file = new java.io.File(filePathConfiguration.getFile() +fileList.get(j).getFileId());
					java.io.File fileFolder = new java.io.File(documentTypeFolderPath+"/"+fileList.get(j).getFileName());
					FileUtils.copyFile(file, fileFolder);
					DmsLogUtil.logAtDebug(log, "File copied from ::" + file + "to ::" + fileFolder);
				}
			}
			DmsLogUtil.logAtDebug(log, "All file inside " + zipFolder + "folder " + zipFolder.listFiles());
			java.io.File[] files = zipFolder.listFiles();
	        for(java.io.File f: files){
	            DmsLogUtil.logAtDebug(log, "File name::" + f.getName());
	        }
			ZipUtil.pack(zipFolder, zipFile);
			
		} catch (Exception e) {
			DmsLogUtil.logAtDebug(log,"Exception thrown while download the zip file" + e);
		}
	}
	
	public void zipLosAndDmsFiles(DownloadDocumentRequestDto requestDto, String zipFolderName) {
		try {
			log.debug("zipLosAndDmsFiles()::start for payload="+requestDto);
			String smeName = zipFolderName;
			DmsLogUtil.logAtDebug(log, "Inside zipFile method");
			String zipFolderPath = filePathConfiguration.getFile()+"zipdocument/"+requestDto.getSystemName()+"/"+requestDto.getSmeId()+"/"+smeName;
			DmsLogUtil.logAtDebug(log, "zip folder path :::" + zipFolderPath);
			String zipFilePath = zipFolderPath+".zip";	
			DmsLogUtil.logAtDebug(log, "zip file path ::" + zipFilePath);
			java.io.File zipFolder = new java.io.File(zipFolderPath);
			DmsLogUtil.logAtDebug(log, "zip folder has been created , path of zipfolder::" + zipFolder);
			if(zipFolder.exists()){
				DmsLogUtil.logAtDebug(log, "zip older already exist so deleting this zip folder ::" + zipFolder);
				FileUtils.deleteDirectory(zipFolder);
				DmsLogUtil.logAtDebug(log, "Flag for folder ::" + zipFolder.exists());
			}
			zipFolder.mkdirs();
			DmsLogUtil.logAtDebug(log, "zip folder directory has been created::" + zipFolder);
			java.io.File zipFile = new java.io.File(zipFilePath);
			if(zipFile.exists()){
				DmsLogUtil.logAtDebug(log, "zip file already exist so we are going to delte it , zip file path (before deletinging::)" + zipFilePath);
				FileUtils.forceDelete(zipFile);
				DmsLogUtil.logAtDebug(log, "Flag for file::" + zipFile.exists());
			}
			
			List<ZipDocumentsId> zipDocumentsList  =  requestDto.getDocTypeListItem();
			DmsLogUtil.logAtDebug(log, "Zip document List: "+ zipDocumentsList.toString());
			for(int i=0;i<zipDocumentsList.size();i++){
				List<Document> documentList = documentDao.getParentId(zipDocumentsList.get(i),requestDto.getSmeId());
				if(documentList.isEmpty()){
					break;
				}
				List<File> fileList = new ArrayList<File>();
				for(int j=0;j<documentList.size();j++){
					DmsLogUtil.logAtDebug(log,"Parent id :: "+ documentList.get(j).getId());
					List<File> files = documentDao.getFiles(documentList.get(j).getId());
					fileList.addAll(files);
				}
				
				DmsLogUtil.logAtDebug(log, "File list: "+ fileList.toString());

				String docTypeName = documentIdNameMap.get(zipDocumentsList.get(i).getDocumentTypeId());//1
				DmsLogUtil.logAtDebug(log,"Document Type Id: "+ zipDocumentsList.get(i).getDocumentTypeId() + " Document Type Name: "+ docTypeName);
				String documentTypeFolderPath = zipFolderPath+ "/"+docTypeName.trim().replace(" ", "_");
				java.io.File documentTypeFolder = new java.io.File(documentTypeFolderPath);
				documentTypeFolder.mkdir();
				for(int j=0;j<fileList.size();j++){
					java.io.File file = new java.io.File(filePathConfiguration.getFile() +fileList.get(j).getFileId());
					java.io.File fileFolder = new java.io.File(documentTypeFolderPath+"/"+fileList.get(j).getFileName());
					FileUtils.copyFile(file, fileFolder);
					DmsLogUtil.logAtDebug(log, "File copied from ::" + file + "to ::" + fileFolder);
				}
			}
			
			//////////////////////////////////////////////////
			List<LosZipDocumentDto> losZipDocumentDtoList  =  requestDto.getLosTypeListItem();
			DmsLogUtil.logAtDebug(log, "Los Zip document List: "+ losZipDocumentDtoList.toString());
			
			for(LosZipDocumentDto losZipDocumentDto:losZipDocumentDtoList)
			{
				List<LosZipDocProjection> projection = documentsRepository.getLosZipDoc(losZipDocumentDto.getFileId(),losZipDocumentDto.getDocumentTypeId());
				DmsLogUtil.logAtDebug(log, "projection: "+ projection);
				if(projection!=null && projection.size()>0)
				{
					LosZipDocProjection losZipDocProjection = projection.get(0);
					DmsLogUtil.logAtDebug(log,"Los Document Type Id: "+ losZipDocProjection.getDocTypeId() + " Document Type Name: "+ losZipDocProjection.getDocTypeName());
					String losDocumentTypeFolderPath = zipFolderPath+ "/"+losZipDocProjection.getDocTypeName().trim().replace(" ", "_");
					log.debug("losDocumentTypeFolderPath="+losDocumentTypeFolderPath);
					java.io.File losDocumentTypeFolder = new java.io.File(losDocumentTypeFolderPath);
					losDocumentTypeFolder.mkdir();
					java.io.File losFile = new java.io.File(filePathConfiguration.getFile() +losZipDocProjection.getFileId());
					java.io.File losFileFolder = new java.io.File(losDocumentTypeFolderPath+"/"+losZipDocProjection.getFileName());
					FileUtils.copyFile(losFile, losFileFolder);
					DmsLogUtil.logAtDebug(log, "Los File copied from ::" + losFile + " to ::" + losFileFolder);
				}
			}
			
			DmsLogUtil.logAtDebug(log, "All file inside " + zipFolder + "folder " + zipFolder.listFiles());
			java.io.File[] files = zipFolder.listFiles();
			for(java.io.File f: files){
	            DmsLogUtil.logAtDebug(log, "File name::" + f.getName());
	        }
			DmsLogUtil.logAtDebug(log, "zipFolder ::" + zipFolder);
			DmsLogUtil.logAtDebug(log, "zipFile ::" + zipFile);
			saveUploadedFileInfo(zipFile);
			ZipUtil.pack(zipFolder, zipFile);
			
		} catch (Exception e) {
			DmsLogUtil.logAtDebug(log,"Exception thrown while download the zip file" + e);
		}
	}

	private void saveUploadedFileInfo(java.io.File zipFile) {
		String uniqueId = UUID.randomUUID().toString();
		String fileId = uniqueId + zipFile.getName().substring(zipFile.getName().lastIndexOf('.'));
		File uploadedFile = new File();
		uploadedFile.setFileId(fileId);
		uploadedFile.setFileName(zipFile.getName());
		uploadedFile.setFileSize(zipFile.length());
		uploadedFile.setTimeToLive(zipTtl);
		DmsLogUtil.logAtDebug(log, "uploaded file info :: " + uploadedFile);
		int result = documentDao.saveFileInfo(uploadedFile);
		if (result > 0) {
			DmsLogUtil.logAtDebug(log,"Successfully uploaded");
		}
	}

//	public static void main(String[] args) {
//		java.io.File file = new java.io.File("/Users/p2s/Downloads/Jindal Steels");
//		java.io.File file2 = new java.io.File("/Users/p2s/Downloads/test1.zip");
//		DmsLogUtil.logAtDebug(log, "All file inside " + file + "folder " + file.listFiles());
//		java.io.File[] files = file.listFiles();
//        for(java.io.File f: files){
//            DmsLogUtil.logAtDebug(log, "File name::" + f.getName());
//        }
//		ZipUtil.pack( file, file2);
//	}

//	@Scheduled(cron = "${dms.documents.zip.dispose.scheduler-cron}")
//	public void deletingOlderZipFiles() {
//		// Log cron job start
//		DmsLogUtil.logAtDebug(log, "cron job started for deleting older zip files");
//
//		// Directory path from where you want to read files
//		String directoryPath = filePathConfiguration.getFile() + "zipdocument/fsme";
//
//		try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
//			paths.filter(Files::isRegularFile)
//					.filter(path -> path.toString().toLowerCase().endsWith(".zip")) // Filter for only .zip files
//					.forEach(path -> {
//						try {
//							long daysDifference = TimeUnit.DAYS.convert(System.currentTimeMillis() - Files.getLastModifiedTime(path).toMillis(), TimeUnit.MILLISECONDS);
//							UploadedFIleInfoEntity fIleInfo = uploadedFileInfoRepository.findByFileName(path.getFileName().toString()).get();
//							if (fIleInfo.getTimeToLive() != 0 && daysDifference > fIleInfo.getTimeToLive()) {
//								DmsLogUtil.logAtDebug(log, "deleting " + path.toFile() + " older than " + fIleInfo.getTimeToLive()+" days ");
//								FileUtils.forceDelete(path.toFile());
//								fIleInfo.setDeletedAt(new Date());
//								DmsLogUtil.logAtDebug(log,"saving deleted at for file :: "+path.getFileName().toString());
//								uploadedFileInfoRepository.save(fIleInfo);
//							}
//						} catch (IOException e) {
//							throw new RuntimeException(e);
//						}
//					});
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

}
