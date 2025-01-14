package com.power2sme.dms.entity;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class GetDocumentsRequestListItem {
	@NotNull
	private Integer docTypeId;
	
	@NotNull
	private Integer docId;
	
	@NotNull
	private Integer versionNo;
}
