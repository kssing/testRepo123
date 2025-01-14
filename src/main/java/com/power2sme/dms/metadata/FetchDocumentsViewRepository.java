package com.power2sme.dms.metadata;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FetchDocumentsViewRepository extends ReadOnlyRepository<FetchDocumentsViewEntity, Integer>{

	@Query(value = "select b.doc_type_id, b.doc_type_name,b.category_id, b.max_documents, c.key, c.type, c.order from dms.accessible_to as a " +
			"left join dms.document_type as b on a.doc_type_id = b.doc_type_id " + 
			"left join dms.digital_info_mapping as c on c.doc_type_id= a.doc_type_id " + 
			"where a.system_id = :systemId", nativeQuery = true)
	List<DocTypeListProjection> getDocTypeBySystemId(@Param("systemId") Integer systemId);
	
	public interface DocTypeListProjection{
		Integer getDocTypeId();
		String getDocTypeName();
		Integer getCategoryId();
		Integer getMaxDocuments();
		String getKey();
		String getType();
		Integer getOrder();
	}
	
//	@Query(value = "SELECT * FROM dms.mergedoc_smeids where id>=:startIndex and id<=:endIndex", nativeQuery = true)
//	List<String> getAllSmeIdsByRange(@Param("startIndex") Integer startIndex, @Param("endIndex") Integer endIndex);
	
	@Query(value = "SELECT distinct sme_id FROM dms.documents", nativeQuery = true)
	List<String> getAllSmeIds();

	@Query(value = "select d.id, d.doc_id, dt.doc_type_name, d.doc_type_id , count(*) as file_count, max(file.created_at) as file_created_date from " + 
			"(select max(id) as id , doc_id, doc_type_id from documents where sme_id=:smeId group by doc_id, doc_type_id) as d " + 
			"inner join file on d.id=file.parent_id " + 
			"left join document_type as dt on dt.doc_type_id=d.doc_type_id " + 
			"group by d.id, d.doc_id, d.doc_type_id", nativeQuery = true)
	List<DocumentsMetadataProjection> getDocMetadata(@Param("smeId") String smeId);
	
	public interface DocumentsMetadataProjection {
		public Integer getId();
		public Integer getDocId();
		public String getDocTypeName();
		public Integer getDocTypeId();
		public Integer getFileCount();
		public Date getFileCreatedDate();
	}
}
