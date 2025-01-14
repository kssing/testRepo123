package com.power2sme.dms.metadata;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Data;

@Entity
@Immutable
@Data
@Table(name="fetch_documents")
public class FetchDocumentsViewEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="documents_pk")
	private Integer documentsPk;
	
	@Column(name="documents_valid_till")
	private Date documentsValidTill;
	
	@Column(name="documents_created_on")
	private Date documentsCreatedOn;
	
    @Column(name="doc_id")
	private Integer docId;
	
    @Column(name="sme_id")
	private String smeId;
	
    @Column(name="doc_type_id")
	private Integer docTypeId;
    
    @Column(name="doc_type_name")
	private String docTypeName;
    
    @Column(name="max_documents")
	private Integer maxDocuments;
    
    @Column(name="category_id")
	private Integer categoryId;
    
    @Column(name="category_name")
	private String categoryName;
	
    @Column(name="version_no")
	private Integer versionNo;
	
    @Column(name="file_pk")
	private Integer filePk;
	
    @Column(name="file_id")
	private String fileId;
    
    @Column(name="file_name")
	private String fileName;
    
    @Column(name="file_size")
	private Long fileSize;

	@Column(name="file_modified_at")
	private Date fileModifiedAt;
    
    @Column(name="digitalinfo_pk")
	private Integer digitalinfoPk;
	
    @Column(name="key")
	private String key;
    
    @Column(name="value")
	private String value;
    
    @Column(name="type")
	private String type;
    
    @Column(name="verification_pk")
	private Integer verificationPk;
	
    @Column(name="system_id")
	private Integer systemId;
    
    @Column(name="system_name")
	private String systemName;
	
    @Column(name="status")
	private String status;
    
    @Column(name="updated_by")
	private String updatedBy;
    
    @Column(name="comment_pk")
	private Integer commentPk;
	
    @Column(name="comment")
	private String comment;
    
    @Column(name="commented_by")
	private String commented_by;
    
    @Column(name="created_time")
	private Date created_time;
    
    @Column(name="on_action")
	private String on_action;

	@Column(name ="order")
	private Integer order;
}
