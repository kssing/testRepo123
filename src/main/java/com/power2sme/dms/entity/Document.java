package com.power2sme.dms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Document {

	private Integer id;
	private Integer documentId;
	private String documentName;
	private String smeId;
	private Integer documentTypeId;
	private Integer systemId;
	private Integer versionNo;
	private List<Version> versionList;
	private List<Comments> comments=new ArrayList<>();
	private List<File> files;
	private List<DigitizedInfo> digitalInfoList;
	private Date validTill;
	private Date createdOn;
	private String validTillStr;
	private String createdOnStr;
	private List<Verification> verification;
//	private Verification verification;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getSmeId() {
		return smeId;
	}

	public void setSmeId(String smeId) {
		this.smeId = smeId;
	}

	public Integer getDocumentTypeId() {
		return documentTypeId;
	}

	public void setDocumentTypeId(Integer documentTypeId) {
		this.documentTypeId = documentTypeId;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public List<DigitizedInfo> getDigitalInfoList() {
		return digitalInfoList;
	}

	public void setDigitalInfoList(List<DigitizedInfo> digitalInfoList) {
		this.digitalInfoList = digitalInfoList;
	}

	public Date getValidTill() {
		return validTill;
	}

	public void setValidTill(Date validTill) {
		this.validTill = validTill;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public List<Version> getVersionList() {
		return versionList;
	}

	public void setVersionList(List<Version> versionList) {
		this.versionList = versionList;
	}

	public List<Verification> getVerification() {
		return verification;
	}

	public void setVerification(List<Verification> verification) {
		this.verification = verification;
	}
	
	public String getValidTillStr() {
		return validTillStr;
	}

	public void setValidTillStr(String validTillStr) {
		this.validTillStr = validTillStr;
	}

	public String getCreatedOnStr() {
		return createdOnStr;
	}

	public void setCreatedOnStr(String createdOnStr) {
		this.createdOnStr = createdOnStr;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", documentId=" + documentId + ", documentName=" + documentName + ", smeId="
				+ smeId + ", documentTypeId=" + documentTypeId + ", systemId=" + systemId + ", versionNo=" + versionNo
				+ ", version=" + versionList + ", comments=" + comments + ", files=" + files + ", digitalInfoList="
				+ digitalInfoList + ", validTill=" + validTill + ", createdOn=" + createdOn + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Document other = (Document) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}