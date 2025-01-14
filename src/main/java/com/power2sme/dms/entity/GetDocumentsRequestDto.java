package com.power2sme.dms.entity;

import java.util.List;

import lombok.Data;

@Data
public class GetDocumentsRequestDto {
	private Boolean isNewCustomer;
	private String smeId;
	private Integer systemId;
	private List<GetDocumentsRequestListItem> docTypeListItem;
}
