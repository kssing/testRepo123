package com.power2sme.dms.entity;

import lombok.Data;
@Data
public class LosDocumentType {
	private Integer documentTypeId;
	private String documentTypeName;
    private Integer categoryId;
    private String categoryName;
    private Integer maxDocuments;
}
