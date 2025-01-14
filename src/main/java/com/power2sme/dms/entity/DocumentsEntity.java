package com.power2sme.dms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "documents")
@Data
public class DocumentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "doc_id")
    private  Integer docId;

    @Column(name = "sme_id")
    private String smeId;

    @Column(name = "system_id")
    private Integer systemId;

    @Column(name = "doc_type_id")
    private Integer docTypeId;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "valid_till")
    private Date validTill;

    @Column(name="created_on")
    @Temporal(TemporalType.DATE)
    private Date createdOn=new Date();

    @Column(name="created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime=new Date();

    @Column(name="modified_time", nullable=true, columnDefinition="TIMESTAMP default CURRENT_TIMESTAMP on modified CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTime;

}
