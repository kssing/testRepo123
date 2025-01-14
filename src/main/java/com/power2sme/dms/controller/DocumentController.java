package com.power2sme.dms.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.power2sme.dms.entity.AccessibleTo;
import com.power2sme.dms.entity.DocumentWrapper;
import com.power2sme.dms.entity.GetDocumentsRequestDto;
import com.power2sme.dms.entity.SmeIdMap;
import com.power2sme.dms.entity.SystemIdentity;
import com.power2sme.dms.entity.ZipFileWrapper;
import com.power2sme.dms.entity.ZipWrapper;
import com.power2sme.dms.externaldto.DocumentTypeDto;
import com.power2sme.dms.externaldto.ResponseDto;
import com.power2sme.dms.externaldto.SearchFilterDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "Document Collection", description = " ")
@RequestMapping(value = { "/api/v1/", "/api/v2" })
public interface DocumentController {

	@RequestMapping(value = "documentsinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get All Documents for SME", notes = "Returns the list of all Documents and a particular Document along with latest version, its digital info, its verification status and comments provided by the different system for SME.")
	ResponseDto getDocuments(
			@ApiParam(value = "SME Id", required = true, defaultValue = "ACC0008") @RequestParam(value = "smeId", required = true, defaultValue = "") final String smeId,
			@ApiParam(value = "Document Type Id", required = false, defaultValue = "") @RequestParam(value = "docTypeId", required = false, defaultValue = "") final Integer docTypeId,
			@ApiParam(value = "Document Id", required = false, defaultValue = "") @RequestParam(value = "docId", required = false, defaultValue = "") final Integer docId,
			@ApiParam(value = "Version No", required = false, defaultValue = "") @RequestParam(value = "versionNo", required = false, defaultValue = "") final Integer versionNo,
			@ApiParam(value = "System Name", required = true, defaultValue = "fsme") @RequestParam(value = "systemName", required = true, defaultValue = "") final String systemName)
			throws Exception;

	@RequestMapping(value = "uploadfile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Upload file in the DMS System", notes = "In this you can upload the file int the DMS System and it will return the unique file id.")
	ResponseDto uploadDocument(@RequestParam("file") MultipartFile file, @RequestParam("data") Integer data);

	@RequestMapping(value = "mapsmeid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Update unique identifier with SME ID", notes = ". You have to provide the unique identifier and sme id and it will upadte the unique identifier with sme id in the DMS System.")
	ResponseDto updateSme(@RequestBody SmeIdMap smeIdMap);

	@RequestMapping(value = "documentsinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Update documents for list of Document types", notes = "Creates List of Document Types consisting of documents which in turn consists of files, comments,digitized info.")
	ResponseDto updateDocument(@RequestBody DocumentWrapper documentWrapper) throws Exception;

	@RequestMapping(value = "documenttype", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get All Document Type", notes = "Returns the list of all Document type accesible to an application.")
	ResponseDto getDocumentType(
			@ApiParam(value = "System Name", required = true, defaultValue = "fsme") @RequestParam(value = "systemName", required = true, defaultValue = "") String systemName);

	@GetMapping(value = "documents/type", produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get All Document Type for LOS", notes = "Returns the list of all Document type accesible to an application.")
	ResponseDto getLosDocumentType(
			@ApiParam(value = "System Name", required = true, defaultValue = "fsme") @RequestParam(value = "systemName", required = true, defaultValue = "") String systemName);

	@RequestMapping(value = "downloadfile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Download file from DMS System", notes = "In this you can download a file by providing unique file id from the DMS System.")
	void downloadFile(
			@ApiParam(value = "File Id", required = true, defaultValue = "") @RequestParam("fileId") String fileId);

	@RequestMapping(value = "zipfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Download Zip file from DMS System", notes = "In this you can download all the files of a particular document or all document type in a document type name folder in a zip file.")
	void zipFiles(@RequestBody ZipWrapper zipWrapper);

	@RequestMapping(value = "zipfileByDocType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Download Zip file from DMS System", notes = "In this you can download all the files of a particular document or all document type in a document type name folder in a zip file.")
	void zipfileByDocType(@RequestBody ZipFileWrapper zipFileWrapper);

	@RequestMapping(value = "fetchdocuments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get All Documents for SME", notes = "Returns the list of all Documents and a particular Document along with latest version, its digital info, its verification status and comments provided by the different system for SME.")
	ResponseDto fetchDocuments(@RequestBody DocumentWrapper docRequestWrapper);

	@RequestMapping(value = "filemetadata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Show  meta data for requested file from DMS System", notes = "In this you can get metadata of file by providing unique file id from the DMS System.")
	ResponseDto fileMetaData(
			@ApiParam(value = "File Id", required = true, defaultValue = "") @RequestParam("fileId") String fileId);

	@RequestMapping(value = "addsystem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a new system", notes = ". You have to provide the unique system name and it will insert it in the DMS System.")
	ResponseDto addSystemDetails(@RequestBody SystemIdentity identity);

	@RequestMapping(value = "updatesystemdetails", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Updates the details of a system registered with DMS", notes = ". You have to provide the unique system id along with the details that you wish to update and it will update it in the DMS System.")
	ResponseDto updateSystemDetails(@RequestBody SystemIdentity identity);

	@RequestMapping(value = "adddocumenttype", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a new document type", notes = ". You have to provide the document type name along with the category id and max number of documents(optional) and it will add the document type in the DMS System.")
	ResponseDto addDocumentType(@RequestBody DocumentTypeDto documentType);

	@RequestMapping(value = "updatedocumenttype", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Updates the details of a document type registered with DMS", notes = ". You have to provide the unique document type id along with the details that you wish to update and it will update it in the DMS System.")
	ResponseDto updateSystemDetails(@RequestBody DocumentTypeDto documentType);

	@RequestMapping(value = "addaccessibility", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a accessibility mapping", notes = ". You have to provide the system id and doc type id and it will insert the mapping in the DMS System.")
	ResponseDto addAccessibilityMapping(@RequestBody AccessibleTo details);

	@RequestMapping(value = "getcategories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Returns the list of different categories of document types supported by DMS", notes = ".")
	ResponseDto getCategories();

	@RequestMapping(value = "search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Returns the list of documents that match the search criteria", notes = ".")
	ResponseDto search(@RequestBody SearchFilterDto searchFilter);

	@RequestMapping(value = "latestdocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the latest Documents for SME", notes = "Returns the list of latest Documents for a particular SME along with its version and digital info, provided by the different systems for the SME.")
	ResponseDto getDocuments(@RequestParam(value = "smeId", required = true, defaultValue = "") final String smeId,
			@RequestParam(value = "systemName", required = true, defaultValue = "") final String systemName)
			throws Exception;

	@RequestMapping(value = "getdocuments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get All Documents for SME", notes = "Returns the list of all Documents and a particular Document along with latest version, its digital info, its verification status and comments provided by the different system for SME.")
	ResponseDto getDocuments(@RequestBody GetDocumentsRequestDto requestDto);

	@RequestMapping(value = "defaultDocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get default document structure", notes = "Get default structure of documents for new customer")
	ResponseDto getDefaultDocuments(@RequestParam(value = "systemId", required = true) Integer systemId);

	@GetMapping("mergedocs")
	public String mergeDocs(@RequestParam(value = "smeId", required = false) String smeId,
			@RequestParam("targetDocTypeId") Integer targetDocTypeId,
			@RequestParam("srcDocTypeIds") List<Integer> srcDocTypeIds);

	@RequestMapping(value = "documents/metadata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get doc Metadata", notes = "Get file count and last modification of documents ")
	ResponseDto getDocMetadat(@RequestParam("smeId") String smeId);

	@GetMapping("copydocs")
	@ApiOperation(value = "Copy documents of source smeId to target smeId", notes = "Copy documents of source smeId to target smeId and return target smeId documents.")
	public List<ResponseDto> copyDocuments(@RequestParam("sourceSmeId") List<String> sourceSmeId,
			@RequestParam("targetSmeId") List<String> targetSmeId, @RequestParam("systemName") String systemName)
			throws Exception;
}