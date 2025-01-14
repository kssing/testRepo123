package com.power2sme.dms.entity;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZipFilesDetail {

	private Integer id;
	private String key;
	private String value;
	@SerializedName("created_on")
	private Date createdOn;
}
