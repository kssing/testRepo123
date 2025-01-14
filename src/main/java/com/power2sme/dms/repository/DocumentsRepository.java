package com.power2sme.dms.repository;

import com.power2sme.dms.entity.DocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<DocumentsEntity, Integer> {

    @Query(value = "SELECT *, MAX(version_no) AS latest_version FROM documents where sme_id = :smeId \n" +
            "GROUP BY doc_type_id ", nativeQuery = true)
    List<DocumentsEntity> getAllLatestDocument(@Param("smeId") String smeId);

    @Query(value = "SELECT * FROM documents where sme_id =:smeId and version_no =:version", nativeQuery = true)
    List<DocumentsEntity> getAllDocumentsByVersionNo(@Param("smeId") String smeId, @Param("version") Integer versionNo);

    @Query(value = "SELECT * FROM dms.documents where doc_type_id =:docTypeId and sme_id =:smeId order by version_no desc limit 1", nativeQuery = true)
    List<DocumentsEntity> getAllDocumentsByDocTypeId(@Param("smeId") String smeId, @Param("docTypeId") Integer docTypeId);

    @Query(value = "SELECT * FROM dms.documents where doc_type_id =:docTypeId and sme_id =:smeId and  version_no =:versionNo", nativeQuery = true)
    List<DocumentsEntity> findBySmeIdAndDocTypeIdAndVersionNo(@Param("smeId") String smeId, @Param("docTypeId") Integer docTypeId, @Param("versionNo") Integer versionNo);
    
    @Query(value = "SELECT f.file_id,f.file_name,d.doc_type_id, d.doc_type_name FROM dms.uploaded_file_info as f,dms.document_type as d where f.file_id=:fileId and d.doc_type_id=:docTypeId", nativeQuery = true)
    List<LosZipDocProjection> getLosZipDoc(@Param("fileId") String fileId, @Param("docTypeId") Integer docTypeId);
    
    public interface LosZipDocProjection{
    	public String getFileId();
    	public String getFileName();
    	public String getDocTypeId();
    	public String getDocTypeName();
    }
}
