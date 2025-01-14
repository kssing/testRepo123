package com.power2sme.dms.entity;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ZipWrapper {
	
	@NotNull
	private String smeName;
	
	@NotNull
	private String smeId;
	
	@NotNull
	private List<ZipDocumentsId> zipDocumentList;

	@Override
	public String toString() {
		return "ZipWrapper [smeName=" + smeName + ", smeId=" + smeId + ", zipDocumentList=" + zipDocumentList + "]";
	}
	
	

}
