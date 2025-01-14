package com.power2sme.dms.entity;

public class DocumentInfoResponse {

	private String smeId;
	private Integer systemId;
	private Integer docTypeId;
	private Integer docId;
	private Integer versionNo;
	private String docTypeName;
	private Integer category;
	private Integer maxDocuments;
	private String fileId;
	private String fileName;
	private String key;
	private String value;
	public String getSmeId() {
		return smeId;
	}
	public void setSmeId(String smeId) {
		this.smeId = smeId;
	}
	public Integer getSystemId() {
		return systemId;
	}
	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}
	public Integer getDocTypeId() {
		return docTypeId;
	}
	public void setDocTypeId(Integer docTypeId) {
		this.docTypeId = docTypeId;
	}
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	public String getDocTypeName() {
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public Integer getMaxDocuments() {
		return maxDocuments;
	}
	public void setMaxDocuments(Integer maxDocuments) {
		this.maxDocuments = maxDocuments;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "DocumentInfoResponse [smeId=" + smeId + ", systemId=" + systemId + ", docTypeId=" + docTypeId
				+ ", docId=" + docId + ", versionNo=" + versionNo + ", docTypeName=" + docTypeName + ", category="
				+ category + ", maxDocuments=" + maxDocuments + ", fileId=" + fileId + ", fileName=" + fileName
				+ ", key=" + key + ", value=" + value + "]";
	}
	
	
}
