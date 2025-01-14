package com.power2sme.dms.metadata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="file_meta_data")
public class MetadataEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id")
	private Integer id;
	
	@Column(name="sme_id")
	private String smeId;  
	
	@Column(name="doc_id")
	private Integer docId;
	
	@Column(name="doc_type_id")
	private Integer docTypeId;
	
	@Column(name="version_no")
	private Integer versionNo;
	
	@Column(name="file_id")
	private String fileId;
	
	@Column(name="doc_type_name")
	private String docTypeName;
	
	@Column(name="category_id")
	private Integer categoryId;
	
	@Column(name="cat_name")
	private String catagoryName;
	
	private String key;
	
	private String value;
	
	private String type;
	
}
