package com.power2sme.dms.entity;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class ZipDocumentsId {
	@NotNull
	private Integer documentTypeId;
	
	@NotNull
	private Integer documentId;
	
	@NotNull
	private Integer versionNo;
	
	
	@Override
	public String toString() {
		return "ZipDocumentsId [documentTypeId=" + documentTypeId + ", documentId=" + documentId + ", versionNo="
				+ versionNo + "]";
	}
	
	

}
