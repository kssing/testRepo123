package com.power2sme.dms.entity;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
@Data
public class DownloadDocumentRequestDto {
	private String emailId;
	
	@NotNull
	@NotEmpty
	private String smeId;
	
	@NotNull
	@NotEmpty
	private String smeName;
	
	@NotNull
	@NotEmpty
	private String systemName;
	
	//private Integer docTypeId;
	
	private List<ZipDocumentsId> docTypeListItem;
	
	private List<LosZipDocumentDto> losTypeListItem;
}
