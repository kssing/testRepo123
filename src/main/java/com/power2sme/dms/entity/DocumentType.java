package com.power2sme.dms.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DocumentType {
	
	private Integer documentTypeId;
	private String documentTypeName;
	private Integer categoryId;
	private String applicableCompType;
	private String systemName;
	private Integer maxDocuments;
	private List<Document> documentList;
	
	
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
	public String getApplicableCompType() {
		return applicableCompType;
	}
	public void setApplicableCompType(String applicableCompType) {
		this.applicableCompType = applicableCompType;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public List<Document> getDocumentList() {
		return documentList;
	}
	public void setDocumentList(List<Document> documentList) {
		this.documentList = documentList;
	}
	
	public Integer getMaxDocuments() {
		return maxDocuments;
	}
	public void setMaxDocuments(Integer maxDocuments) {
		this.maxDocuments = maxDocuments;
	}
	@Override
	public String toString() {
		return "DocumentType [documentTypeId=" + documentTypeId + ", documentTypeName=" + documentTypeName
				+ ", categoryId=" + categoryId + ", applicableCompType=" + applicableCompType + ", systemName="
				+ systemName + ", maxDocuments=" + maxDocuments + ", documentList=" + documentList + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentTypeId == null) ? 0 : documentTypeId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentType other = (DocumentType) obj;
		if (documentTypeId == null) {
			if (other.documentTypeId != null)
				return false;
		} else if (!documentTypeId.equals(other.documentTypeId))
			return false;
		return true;
	}
	
	
	
}
