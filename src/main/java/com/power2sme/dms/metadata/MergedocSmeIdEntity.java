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
@Data
@Table(name="mergedoc_smeids")
public class MergedocSmeIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	private Integer id;
	
	@Column(name="sme_id")
	private String smeId;
	
	@Column(name="status")
	private Integer status;
	
	@Column(name="log")
	private String log;
	
	@Column(name="created_date")
	private Date createdDate;
}
