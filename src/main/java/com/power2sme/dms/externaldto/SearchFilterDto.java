package com.power2sme.dms.externaldto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchFilterDto {
	@ApiModelProperty(position=0)
	private String smeId;
	@ApiModelProperty(position=1)
	private Integer documentTypeId;
	@ApiModelProperty(position=2)
	private String text;
	@ApiModelProperty(position=5)
	private String systemName;
	@JsonIgnore
	private Integer systemId;
	@ApiModelProperty(position=3)
	private Integer pageId;
	@ApiModelProperty(position=4)
	private Integer pageSize;

	public String getSmeId() {
		return smeId;
	}

	public void setSmeId(String smeId) {
		this.smeId = smeId;
	}

	public Integer getDocumentTypeId() {
		return documentTypeId;
	}

	public void setDocumentTypeId(Integer documentTypeId) {
		this.documentTypeId = documentTypeId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "SearchFilterDto [smeId=" + smeId + ", documentTypeId=" + documentTypeId + ", text=" + text
				+ ", systemName=" + systemName + ", systemId=" + systemId + ", pageId=" + pageId + ", pageSize="
				+ pageSize + "]";
	}

	
}
