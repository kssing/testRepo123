package com.power2sme.dms.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;

@JsonInclude(Include.NON_NULL)
@ApiModel(value="Document Wrapper", description="Model for information of Document for SME.")
public class DocumentWrapper {
	private String smeId;
	private String systemName;
	private String updatedBy;
	private List<DocumentType> docType;

	public String getSmeId() {
		return smeId;
	}

	public void setSmeId(String smeId) {
		this.smeId = smeId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public List<DocumentType> getDocType() {
		return docType;
	}

	public void setDocType(List<DocumentType> docType) {
		this.docType = docType;
	}

	@Override
	public String toString() {
		return "updateDocumentWrapper [smeId=" + smeId + ", systemName=" + systemName + ", updatedBy=" + updatedBy
				+ ", docType=" + docType + "]";
	}

}
