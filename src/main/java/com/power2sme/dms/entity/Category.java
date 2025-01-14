package com.power2sme.dms.entity;

import java.util.List;

public class Category {

	private int categoryId;
	private String categoryName;
	private List<DocumentType> documentType;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<DocumentType> getDocumentType() {
		return documentType;
	}
	public void setDocumentType(List<DocumentType> documentType) {
		this.documentType = documentType;
	}

	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + ", documentType="
				+ documentType + "]";
	}

}
