package com.power2sme.dms.externaldto;

public class DocumentTypeDto {

	private Integer documentTypeId;
	private String documentTypeName;
	private Integer categoryId;
	private Integer maxDocuments;
	public Integer getDocumentTypeId() {
		return documentTypeId;
	}
	public void setDocumentTypeId(Integer documentTypeId) {
		this.documentTypeId = documentTypeId;
	}
	public String getDocumentTypeName() {
		return documentTypeName;
	}
	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public Integer getMaxDocuments() {
		return maxDocuments;
	}
	public void setMaxDocuments(Integer maxDocuments) {
		this.maxDocuments = maxDocuments;
	}
	@Override
	public String toString() {
		return "DocumentTypeDto [documentTypeId=" + documentTypeId + ", documentTypeName=" + documentTypeName
				+ ", categoryId=" + categoryId + ", maxDocuments=" + maxDocuments + "]";
	}
	
	
}
