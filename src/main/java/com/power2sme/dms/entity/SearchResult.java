package com.power2sme.dms.entity;

public class SearchResult {
	
	private String key;
	private String value;
	private Long docId;
	private Integer docTypeId;
	private String smeId;
	private Integer versionNo;
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
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public Integer getDocTypeId() {
		return docTypeId;
	}
	public void setDocTypeId(Integer docTypeId) {
		this.docTypeId = docTypeId;
	}
	public String getSmeId() {
		return smeId;
	}
	public void setSmeId(String smeId) {
		this.smeId = smeId;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	@Override
	public String toString() {
		return "SearchResult [key=" + key + ", value=" + value + ", docId=" + docId + ", docTypeId=" + docTypeId
				+ ", smeId=" + smeId + ", versionNo=" + versionNo + "]";
	}
	
}