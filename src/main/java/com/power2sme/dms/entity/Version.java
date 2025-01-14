package com.power2sme.dms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Version {
	
	private Integer versionNo;
	private List<File> files=new ArrayList<>();
	private List<DigitizedInfo> digitalInfo=new ArrayList<>();
	private Verification verification=new Verification();
	
	@JsonFormat(pattern="MMM dd, yyyy hh:mm:ss a")
	private Date createdOn;
	
	private String createdOnStr;
	
	
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
	public List<DigitizedInfo> getDigitalInfo() {
		return digitalInfo;
	}
	public void setDigitalInfo(List<DigitizedInfo> digitalInfo) {
		this.digitalInfo = digitalInfo;
	}
	public Verification getVerification() {
		return verification;
	}
	public void setVerification(Verification verification) {
		this.verification = verification;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	
	public String getCreatedOnStr() {
		return createdOnStr;
	}
	public void setCreatedOnStr(String createdOnStr) {
		this.createdOnStr = createdOnStr;
	}
	@Override
	public String toString() {
		return "Version [versionNo=" + versionNo + ", files=" + files + ", digitalInfo=" + digitalInfo
				+ ", verification=" + verification + ", createdOn=" + createdOn + "]";
	}
}
