package com.power2sme.dms.entity;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class LosZipDocumentDto {
	@NotNull
	private Integer documentTypeId;
	
	@NotNull
	private String fileId;

	@Override
	public String toString() {
		return "LosZipDocumentDto [documentTypeId=" + documentTypeId + ", fileId=" + fileId + "]";
	}

	
}
