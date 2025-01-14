package com.power2sme.dms.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.power2sme.dms.dao.DocumentDao;
import com.power2sme.dms.entity.AccessibleTo;
import com.power2sme.dms.entity.Category;
import com.power2sme.dms.entity.Comments;
import com.power2sme.dms.entity.DigitizedInfo;
import com.power2sme.dms.entity.Document;
import com.power2sme.dms.entity.DocumentInfoResponse;
import com.power2sme.dms.entity.DocumentType;
import com.power2sme.dms.entity.DocumentsEntity;
import com.power2sme.dms.entity.File;
import com.power2sme.dms.entity.GetDocumentsRequestListItem;
import com.power2sme.dms.entity.LosDocumentType;
import com.power2sme.dms.entity.SearchResult;
import com.power2sme.dms.entity.SystemIdentity;
import com.power2sme.dms.entity.Verification;
import com.power2sme.dms.entity.ZipDocumentsId;
import com.power2sme.dms.entity.ZipFileDocuments;
import com.power2sme.dms.externaldto.DocumentTypeDto;
import com.power2sme.dms.externaldto.SearchFilterDto;
import com.power2sme.dms.metadata.FetchDocumentsViewEntity;
import com.power2sme.dms.repository.DocumentsRepository;
import com.power2sme.dms.utils.DmsLogUtil;

@Repository
public class DocumentDaoImpl implements DocumentDao {

	Logger logger = LoggerFactory.getLogger(DocumentDaoImpl.class);

	JdbcTemplate jdbcTemplate;

	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	DocumentsRepository documentsRepository;

	@Autowired
	@Qualifier("dmsDataSource")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Value("${dms.db.name}")
	private String dmsDbName;

	private class FetchDocumentsViewMapper implements RowMapper {
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			FetchDocumentsViewEntity dto = new FetchDocumentsViewEntity();
			dto.setDocumentsPk(rs.getInt("documents_pk"));
			dto.setDocumentsValidTill(rs.getDate("documents_valid_till"));
			dto.setDocumentsCreatedOn(rs.getTimestamp("documents_created_on"));
			dto.setDocId(rs.getInt("doc_id"));
			dto.setSmeId(rs.getString("sme_id"));
			dto.setDocTypeId(rs.getInt("doc_type_id"));
			dto.setDocTypeName(rs.getString("doc_type_name"));
			dto.setMaxDocuments(rs.getInt("max_documents"));
			dto.setCategoryId(rs.getInt("category_id"));
			dto.setCategoryName(rs.getString("category_name"));
			dto.setVersionNo(rs.getInt("version_no"));
			dto.setFilePk(rs.getInt("file_pk"));
			dto.setFileId(rs.getString("file_id"));
			dto.setFileName(rs.getString("file_name"));
			dto.setFileSize(rs.getLong("file_size"));
			dto.setFileModifiedAt(rs.getTimestamp("file_modified_at"));
			dto.setDigitalinfoPk(rs.getInt("digitalinfo_pk"));
			dto.setKey(rs.getString("key"));
			dto.setValue(rs.getString("value"));
			dto.setType(rs.getString("type"));
			dto.setVerificationPk(rs.getInt("verification_pk"));
			dto.setSystemId(rs.getInt("system_id"));
			dto.setSystemName(rs.getString("system_name"));
			dto.setStatus(rs.getString("status"));
			dto.setUpdatedBy(rs.getString("updated_by"));
			dto.setCommentPk(rs.getInt("comment_pk"));
			dto.setComment(rs.getString("comment"));
			dto.setCommented_by(rs.getString("commented_by"));
			dto.setCreated_time(rs.getTimestamp("created_time"));
			dto.setOn_action(rs.getString("on_action"));
			dto.setOrder(rs.getInt("order"));
			return dto;
		}
	}

	@Override
	public List<FetchDocumentsViewEntity> findAllBySmeIdAndSystemId(String smeId, Integer systemId) {
		long startTime = System.currentTimeMillis();
		String query = "SELECT * FROM dms.fetch_documents where sme_id=? and system_id=?";
		List<FetchDocumentsViewEntity> resultList = jdbcTemplate.query(query, new Object[] { smeId, systemId },
				new FetchDocumentsViewMapper());
		long endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed in findAllBySmeIdAndSystemId :: " + (endTime - startTime));
		return resultList;
	}

	@Override
	public List<FetchDocumentsViewEntity> findAllBySmeIdAndSystemIdAndDocTypeIdIn(String smeId, Integer systemId,
			List<GetDocumentsRequestListItem> getDocumentsRequestListItems) {

		long startTime = System.currentTimeMillis();

		List<Integer> docTypeIds = getDocumentsRequestListItems.stream().map(e -> e.getDocTypeId())
				.collect(Collectors.toList());
		String docTypeIdsStr = StringUtils.collectionToCommaDelimitedString(docTypeIds);
		String query = String.format(
				"SELECT * FROM dms.docs_all_versions_view where sme_id=? and system_id=? and doc_type_id in (%s)",
				docTypeIdsStr);
		List<FetchDocumentsViewEntity> resultList = jdbcTemplate.query(query, new Object[] { smeId, systemId },
				new FetchDocumentsViewMapper());

		long endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger,
				"Total time elapsed in findAllBySmeIdAndSystemIdAndDocTypeIdIn :: " + (endTime - startTime));
		return resultList;
	}

//	@Override
//	public List<FetchDocumentsViewEntity> getDocTypeBySystemId(Integer systemId)
//	{
//		long startTime = System.currentTimeMillis();
//		String query = "select b.doc_type_id, b.doc_type_name,b.category_id, b.max_documents, c.key, c.type from dms.accessible_to as a " + 
//				"left join dms.document_type as b on a.doc_type_id = b.doc_type_id " + 
//				"left join dms.digital_info_mapping as c on c.doc_type_id= a.doc_type_id " + 
//				"where a.system_id = ?";
//		List<FetchDocumentsViewEntity> resultList = jdbcTemplate.query(query, new Object[] { systemId}, new FetchDocumentsViewMapper());
//		long endTime = System.currentTimeMillis();
//		DmsLogUtil.logAtDebug(logger, "Total time elapsed in getDocTypeBySystemId :: " + (endTime - startTime));
//		return resultList;
//	}

	@Override
	public List<DocumentType> getDocumentType(Integer systemId) {
		long startTime = System.currentTimeMillis();
		String query = "select * from " + dmsDbName + ".accessible_to left join " + dmsDbName + ".document_type on "
				+ dmsDbName + ".accessible_to.doc_type_id = " + dmsDbName
				+ ".document_type.doc_type_id where accessible_to.system_id = ?";
		List<DocumentType> documentTypeList = jdbcTemplate.query(query, new Object[] { systemId },
				new DocumentTypeMapper());
		long endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed in getDocumentType :: " + (endTime - startTime));
		return documentTypeList;
	}

	@Override
	public List<LosDocumentType> getLosDocumentType(Integer systemId) {
		long startTime = System.currentTimeMillis();
//		String query = "select * from " + dmsDbName + ".accessible_to left join " + dmsDbName + ".document_type on "
//				+ dmsDbName + ".accessible_to.doc_type_id = " + dmsDbName
//				+ ".document_type.doc_type_id where accessible_to.system_id = ?";
		String query = "select c.id as categoryId,c.cat_name as categoryName, d.doc_type_id as documentTypeId, d.doc_type_name as documentTypeName, d.max_documents as maxDocuments from "
				+ dmsDbName + ".category_detail as c " + "inner join " + dmsDbName
				+ ".document_type as d on d.category_id=c.id " + "inner join " + dmsDbName
				+ ".accessible_to as a on a.doc_type_id = d.doc_type_id where a.system_id = ? order by 1";
		List<LosDocumentType> documentTypeList = jdbcTemplate.query(query, new Object[] { systemId },
				new RowMapper<LosDocumentType>() {
					@Override
					public LosDocumentType mapRow(ResultSet rs, int rowNum) throws SQLException {
						LosDocumentType dto = new LosDocumentType();
						dto.setDocumentTypeId(rs.getInt("documentTypeId"));
						dto.setDocumentTypeName(rs.getString("documentTypeName"));
						dto.setCategoryId(rs.getInt("categoryId"));
						dto.setCategoryName(rs.getString("categoryName"));
						dto.setMaxDocuments(rs.getInt("maxDocuments"));
						return dto;
					}
				});
		long endTime = System.currentTimeMillis();
		DmsLogUtil.logAtDebug(logger, "Total time elapsed in getDocumentType :: " + (endTime - startTime));
		return documentTypeList;
	}

	private class DocumentTypeMapper implements RowMapper<DocumentType> {

		@Override
		public DocumentType mapRow(ResultSet rs, int rowNum) throws SQLException {
			DocumentType documentType = new DocumentType();
			documentType.setDocumentTypeId(rs.getInt("doc_type_id"));
			documentType.setDocumentTypeName(rs.getString("doc_type_name"));
			documentType.setCategoryId(rs.getInt("category_id"));
			documentType.setMaxDocuments(rs.getInt("max_documents"));
			return documentType;
		}

	}

	@Override
	public List<Document> getAllDocumentsByDocTypeAndSmeId(Integer documentTypeId, String smeId) {
		String query = "select * from " + dmsDbName
				+ ".documents  where doc_type_id= ? and sme_id = ? order by doc_id asc, version_no desc";
		List<Document> documentList = jdbcTemplate.query(query, new Object[] { documentTypeId, smeId },
				new DocumentMapper());
		return documentList;
	}

	private class DocumentMapper implements RowMapper<Document> {

		@Override
		public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
			Document document = new Document();
			document.setDocumentId(rs.getInt("doc_id"));
			document.setId(rs.getInt("id"));
			document.setValidTill(rs.getDate("valid_till"));
			document.setVersionNo(rs.getInt("version_no"));
			document.setCreatedOn(rs.getDate("created_on"));
			return document;
		}

	}

	@Override
	public List<Comments> getComments(String smeId, Integer documentTypeId, Integer documentId) {
		String query = "select * from " + dmsDbName
				+ ".comments where document_id = ? and doc_type_id=? and sme_id = ?";
		List<Comments> commentsList = jdbcTemplate.query(query, new Object[] { documentId, documentTypeId, smeId },
				new CommentsMapper());
		return commentsList;
	}

	private class CommentsMapper implements RowMapper<Comments> {

		@Override
		public Comments mapRow(ResultSet rs, int rowNum) throws SQLException {
			Comments comments = new Comments();
			comments.setSystemId(rs.getInt("system_id"));
			comments.setAction(rs.getString("on_action"));
			comments.setComment(rs.getString("comment"));
			comments.setCommentId(rs.getInt("id"));
			comments.setCreatedBy(rs.getString("commented_by"));
			comments.setCreatedTime(rs.getDate("created_time"));
			return comments;
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createNewEntryForDocument(final String smeId, final Integer docTypeId, final Integer systemId,
			final Document document) throws DataAccessException {

		try {
			Integer parentId = createDocument(smeId, docTypeId, systemId, document);
			createCommentsIfAny(smeId, document, docTypeId, systemId);
			createDigitalInfoIfAny(smeId, document, parentId, systemId);
			insertVerificationDetail(document, parentId, systemId);
			insertFiles(document, parentId);
		} catch (Exception e) {
			logger.error("Failed to insert values in document table, Exception encountered is ::::: ", e);
			throw e;
		}
	}

	private Integer createDocument(final String smeId, final Integer docTypeId, final Integer systemId,
			final Document document) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String GENERATED_COLUMNS[] = { "id" };

		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				final String insertQuery = "INSERT INTO " + dmsDbName
						+ ".`documents`(`doc_id`,`sme_id`,`system_id`,`doc_type_id`,`version_no`,`valid_till`,`created_time`,"
						+ "`created_on`) VALUES (?,?,?,?,?,?,now(),?)";
				PreparedStatement ps = connection.prepareStatement(insertQuery.toString(), GENERATED_COLUMNS);
				ps.setInt(1, document.getDocumentId());
				ps.setString(2, smeId);
				ps.setInt(3, systemId);
				ps.setInt(4, docTypeId);
				ps.setInt(5, document.getVersionNo());
				if (document.getValidTill() != null) {
					ps.setDate(6, new java.sql.Date(document.getValidTill().getTime()));
				} else {
					ps.setDate(6, null);
				}
				if (document.getCreatedOn() != null) {
					ps.setDate(7, new java.sql.Date(document.getCreatedOn().getTime()));
				} else {
					ps.setDate(7, new java.sql.Date(new Date().getTime()));
				}
				logger.debug("statement" + ps.toString());
				return ps;
			}
		}, keyHolder);
		Integer parentId = keyHolder.getKey().intValue();
		return parentId;
	}

	private void insertVerificationDetail(final Document document, final Integer parentId, final Integer systemId) {
		final String insertQuery = "INSERT INTO " + dmsDbName + ".`verification`"
				+ "(`system_id`,`status`,`updated_by`,`created_time`,`doc_id`,`parent_id`) VALUES(?,?,?,now(),?,?)";
		try {
			if (document.getVerification() != null) {
				jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Verification verification = document.getVerification().get(i);
						ps.setInt(1, systemId);
						ps.setString(2, verification.getStatus());
						ps.setString(3, verification.getUpdatedBy());
						ps.setInt(4, document.getDocumentId());
						ps.setInt(5, parentId);
					}

					@Override
					public int getBatchSize() {
						return document.getVerification().size();
					}
				});
			}
			// Verification verification = document.getVerification();
			// jdbcTemplate.update(insertQuery, new Object [] {systemId,
			// verification.getStatus(), verification.getUpdatedBy(),
			// document.getDocumentId(), parentId});
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	private void insertFiles(final Document document, final Integer parentId) {
		final String insertQuery = "INSERT INTO " + dmsDbName + ".`file`"
				+ "(file_id,file_name,doc_id,parent_id,file_size) VALUES(?,?,?,?,?)";
		try {
			if (document.getFiles() != null) {
				jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						File file = document.getFiles().get(i);
						ps.setString(1, file.getFileId());
						File fileForSize = getUploadedFileInfo(file.getFileId());
						if (fileForSize != null) {
							ps.setString(2, fileForSize.getFileName());
						} else {
							ps.setString(2, file.getFileName());
						}
						ps.setInt(3, document.getDocumentId());
						ps.setInt(4, parentId);
						if (fileForSize != null) {
							ps.setLong(5, fileForSize.getFileSize());
						} else {
							ps.setLong(5, file.getFileSize());
						}
					}

					@Override
					public int getBatchSize() {
						return document.getFiles().size();
					}
				});
			}
		} catch (Exception e) {
			throw e;
		}

	}

	private void createDigitalInfoIfAny(String smeId, final Document document, final Integer parentId,
			Integer systemId) {
		final String insertQuery = "INSERT INTO " + dmsDbName + ".`digital_info`"
				+ "(`parent_id`,`key`,`value`,`type`) VALUES(?,?,?,?)";
		try {
			if (document.getDigitalInfoList() != null) {
				jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						DigitizedInfo digitalInfo = document.getDigitalInfoList().get(i);
						ps.setInt(1, parentId);
						ps.setString(2, digitalInfo.getKey());
						ps.setString(3, digitalInfo.getValue());
						ps.setString(4, digitalInfo.getType());
					}

					@Override
					public int getBatchSize() {
						return document.getDigitalInfoList().size();
					}
				});
			}
		} catch (Exception e) {
			throw e;
		}

	}

	private void createCommentsIfAny(final String smeId, final Document document, final Integer docTypeId,
			final Integer systemId) {
		final String insertQuery = "INSERT INTO " + dmsDbName + ".`comments`"
				+ "(`document_id`,`system_id`,`sme_id`,`comment`,`created_time`,`commented_by`,`on_action`,`on_version`,`doc_type_id`)"
				+ " VALUES(?,?,?,?,?,?,?,?,?)";
		try {
			if (document.getComments() != null) {
				jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Comments comment = document.getComments().get(i);
						ps.setInt(1, document.getDocumentId());
						ps.setInt(2, systemId);
						ps.setString(3, smeId);
						ps.setString(4, comment.getComment());
						ps.setTimestamp(5, new Timestamp(new Date().getTime()));
						ps.setString(6, comment.getCreatedBy());
						ps.setString(7, comment.getAction());
						ps.setInt(8, document.getVersionNo());
						ps.setInt(9, docTypeId);

					}

					@Override
					public int getBatchSize() {
						return document.getComments().size();
					}
				});
			}
		} catch (Exception e) {
			logger.error(":: Error in inserting comments ::", e);
			throw e;

		}
	}

	@Override
	public List<File> getFiles(Integer parentId) {
		String query = "select * from " + dmsDbName + ".file where parent_id=?";
		List<File> files = jdbcTemplate.query(query, new Object[] { parentId }, new FileMapper());
		return files;
	}

	private class FileMapper implements RowMapper<File> {
		@Override
		public File mapRow(ResultSet rs, int rowNum) throws SQLException {
			File file = new File();
			file.setFileId(rs.getString("file_id"));
			file.setFileName(rs.getString("file_name"));
			file.setFileModifiedAt(rs.getTimestamp("modified_at"));
			return file;
		}
	}

	private class UploadedFileMapper implements RowMapper<File> {
		@Override
		public File mapRow(ResultSet rs, int rowNum) throws SQLException {
			File file = new File();
			file.setFileId(rs.getString("file_id"));
			file.setFileName(rs.getString("file_name"));
			file.setFileModifiedAt(rs.getTimestamp("modified_at"));
			file.setTimeToLive(rs.getInt("time_to_live"));
			return file;
		}
	}

	@Override
	public List<DigitizedInfo> getDigitalInfo(Integer parentId) {
		String query = "select * from " + dmsDbName + ".digital_info where parent_id=?";
		List<DigitizedInfo> digitizedInfoList = jdbcTemplate.query(query, new Object[] { parentId },
				new DigitizedInfoMapper());
		return digitizedInfoList;
	}

	private class DigitizedInfoMapper implements RowMapper<DigitizedInfo> {

		@Override
		public DigitizedInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			DigitizedInfo digitizedInfo = new DigitizedInfo();
			digitizedInfo.setKey(rs.getString("key"));
			digitizedInfo.setValue(rs.getString("value"));
			digitizedInfo.setType(rs.getString("type"));
			return digitizedInfo;
		}

	}

	@Override
	public Verification getVerification(Integer parentId) {
		try {
			String query = "select * from " + dmsDbName
					+ ".verification where parent_id = ? order by created_time desc limit 1";
			Verification verification = jdbcTemplate.queryForObject(query, new Object[] { parentId },
					new VerificationMapper());
			return verification;
		} catch (EmptyResultDataAccessException e) {
			logger.debug("Verification info not found");
			Verification verification = new Verification();
			return verification;
		}
	}

	private class VerificationMapper implements RowMapper<Verification> {

		@Override
		public Verification mapRow(ResultSet rs, int rowNum) throws SQLException {
			Verification verification = new Verification();
			verification.setStatus(rs.getString("status"));
			verification.setUpdatedBy(rs.getString("updated_by"));
			verification.setSystemId(rs.getInt("system_id"));
			return verification;
		}

	}

	@Override
	public List<SystemIdentity> getSystemDetail() {
		String query = "select * from " + dmsDbName + ".system_detail";
		List<SystemIdentity> systemIdentityList = jdbcTemplate.query(query, new SystemIdentityMapper());
		return systemIdentityList;
	}

	@Override
	public List<SystemIdentity> getSystemDetailById(int id) {
		String query = "select * from " + dmsDbName + ".system_detail where system_id =:systemId ";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("systemId", id);
		return namedParameterJdbcTemplate.query(query, paramMap, new SystemIdentityMapper());

	}

	private class SystemIdentityMapper implements RowMapper<SystemIdentity> {

		@Override
		public SystemIdentity mapRow(ResultSet rs, int rowNum) throws SQLException {
			SystemIdentity systemIdentity = new SystemIdentity();
			systemIdentity.setSystemId(rs.getInt("system_id"));
			systemIdentity.setSystemName(rs.getString("system_name"));
			return systemIdentity;
		}

	}

	@Override
	public Integer updateSmeIdInDocuments(String smeId, String uniqueId) throws Exception {
		String query = "update " + dmsDbName + ".documents set sme_id=? where sme_id = ?";
		jdbcTemplate.update("SET SQL_SAFE_UPDATES = 0; ");
		int result = jdbcTemplate.update(query, new Object[] { smeId, uniqueId });
		jdbcTemplate.update("SET SQL_SAFE_UPDATES = 1; ");
		return result;
	}

	@Override
	public Integer updateSmeIdInComments(String smeId, String uniqueId) throws Exception {
		String query = "update " + dmsDbName + ".comments set sme_id=? where sme_id = ?";
		jdbcTemplate.update("SET SQL_SAFE_UPDATES = 0; ");
		int result = jdbcTemplate.update(query, new Object[] { smeId, uniqueId });
		jdbcTemplate.update("SET SQL_SAFE_UPDATES = 1; ");
		return result;
	}

	@Override
	public List<Document> getDocumentId(Integer documentTypeId, String smeId) {
		String query = "select doc_id from " + dmsDbName
				+ ".documents where doc_type_id=? and sme_id=? group by doc_id order by doc_id asc ";
		List<Document> documentList = jdbcTemplate.query(query, new Object[] { documentTypeId, smeId },
				new DocumentIdMapper());
		return documentList;
	}

	private class DocumentIdMapper implements RowMapper<Document> {

		@Override
		public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
			Document document = new Document();
			document.setDocumentId(rs.getInt("doc_id"));
			return document;
		}

	}

	@Override
	public Document getDocumentLatestDocument(Document document) {
		String query = "select * from " + dmsDbName + ".documents where doc_id = ? and doc_type_id=? and"
				+ " sme_id=? order by version_no desc limit 1";
		try {
			Document document2 = jdbcTemplate.queryForObject(query,
					new Object[] { document.getDocumentId(), document.getDocumentTypeId(), document.getSmeId() },
					new DocumentMapper());
			return document2;
		} catch (EmptyResultDataAccessException e) {
			logger.debug("No data found for" + document.getSmeId() + " doc id " + document.getDocumentId()
					+ " doc type id " + document.getDocumentTypeId());
			Document newDoc = null;
			return newDoc;
		}

	}

	@Override
	@Transactional
	public void doUpdationForDocument(String smeId, Document documentfetched, Document document, Integer systemId,
			Integer docTypeId) {
		Integer parentId = documentfetched.getId();
		try {
			if (document.getComments() != null && !document.getComments().isEmpty()) {
				createCommentsIfAny(smeId, document, docTypeId, systemId);
			}
			// if(document.getDigitalInfoList()!=null &&
			// !document.getDigitalInfoList().isEmpty()){
			// updateDigitalInfo(smeId, document, parentId, systemId);
			// }
			if (document.getVerification() != null) {
				insertVerificationDetail(document, parentId, systemId);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	// private void updateVerificationDetail(final Document document, final Integer
	// parentId,final Integer systemId) {
	// final String updateQuery = "UPDATE "+ dmsDbName+".`verification`"+
	// " set `system_id`=?,`status`=?,`updated_by`=?,`created_time`=now(),`doc_id`=?
	// where `parent_id`=?";
	// try {
	//
	// jdbcTemplate.batchUpdate(updateQuery, new BatchPreparedStatementSetter() {
	// @Override
	// public void setValues(PreparedStatement ps, int i) throws SQLException {
	// Verification verification = document.getVerification().get(i);
	// ps.setInt(1, systemId);
	// ps.setString(2, verification.getStatus());
	// ps.setString(3, verification.getUpdatedBy());
	// ps.setInt(4, document.getDocumentId());
	// ps.setInt(5, parentId);
	// logger.debug("SQL SCRIPT:"+ps.toString());
	// }
	//
	// @Override
	// public int getBatchSize() {
	// return document.getDigitalInfoList().size();
	// }
	// });
	// }
	// catch(Exception e){
	// throw e;
	// }
	// }

	// private void updateDigitalInfo(final String smeId, final Document document,
	// final Integer parentId,final Integer systemId) {
	// final String updateQuery = "UPDATE "+ dmsDbName+".`digital_info`"+
	// " set `key`=?,`value`=?,`type`=? where `parent_id`=?";
	// try {
	//
	// jdbcTemplate.batchUpdate(updateQuery, new BatchPreparedStatementSetter() {
	// @Override
	// public void setValues(PreparedStatement ps, int i) throws SQLException {
	// DigitizedInfo digitalInfo=new DigitizedInfo();
	// digitalInfo = document.getDigitalInfoList().get(i);
	// ps.setString(1, digitalInfo.getKey());
	// ps.setString(2, digitalInfo.getValue());
	// ps.setString(3, digitalInfo.getType());
	// ps.setInt(4, parentId);
	// logger.debug("SQL SCRIPT:"+ps.toString());
	// }
	//
	// @Override
	// public int getBatchSize() {
	// return document.getDigitalInfoList().size();
	// }
	// });
	// }
	// catch(Exception e){
	// throw e;
	// }
	//
	// }

	@Override
	public Integer getLatestDocumentId(Integer docTypeId, String smeId) {
		String query = "select doc_id from " + dmsDbName + ".documents order by doc_id desc limit 1";
		List<Document> documentList = jdbcTemplate.query(query, new Object[] {}, new DocumentIdMapper());
		if (documentList != null && !documentList.isEmpty()) {
			return documentList.get(0).getDocumentId();
		}
		return 0;
	}

	@Override
	public List<DocumentType> getDocumentTypes() {
		String query = "select * from " + dmsDbName + ".document_type";
		List<DocumentType> documentTypeList = jdbcTemplate.query(query, new DocumentTypeMapper());
		return documentTypeList;
	}

	@Override
	public List<Category> getCategoryDetail() {
		String query = "select * from " + dmsDbName + ".category_detail";
		List<Category> categoryList = jdbcTemplate.query(query, new CategoryMapper());
		return categoryList;
	}

	private class CategoryMapper implements RowMapper<Category> {

		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			Category category = new Category();
			category.setCategoryId(rs.getInt("id"));
			category.setCategoryName(rs.getString("cat_name"));
			return category;
		}

	}

	@Override
	public List<Document> getParentId(ZipDocumentsId zipDocumentsId, String smeId) {
		String query = "select * from " + dmsDbName
				+ ".documents where doc_type_id=? and doc_id=? and version_no=? and sme_id=?";
		List<Document> documentList = jdbcTemplate.query(query, new Object[] { zipDocumentsId.getDocumentTypeId(),
				zipDocumentsId.getDocumentId(), zipDocumentsId.getVersionNo(), smeId }, new DocumentMapper());
		return documentList;
	}

	@Override
	public Integer saveFileInfo(File file) {
		String query = "insert into " + dmsDbName
				+ ".uploaded_file_info (file_id, file_name, file_size, time_to_live) values(?,?,?,?)";
		int result = jdbcTemplate.update(query,
				new Object[] { file.getFileId(), file.getFileName(), file.getFileSize(), file.getTimeToLive() });
		return result;
	}

	@Override
	public File getFileInfo(String fileId) {
		try {
			String query = "select * from " + dmsDbName + ".uploaded_file_info where file_id=?";
			File file = jdbcTemplate.queryForObject(query, new Object[] { fileId }, new UploadedFileMapper());
			return file;
		} catch (EmptyResultDataAccessException e) {
			logger.debug("File info not found");
			return null;
		}
	}

	@Override
	public List<DigitizedInfo> getDigitalInfoMapper(Integer docTypeId) {
		String query = "select * from " + dmsDbName + ".digital_info_mapping where doc_type_id=?";
		List<DigitizedInfo> digitalInfoMapping = jdbcTemplate.query(query, new Object[] { docTypeId },
				new DigitalInfoMapper());
		return digitalInfoMapping;
	}

	private class DigitalInfoMapper implements RowMapper<DigitizedInfo> {

		@Override
		public DigitizedInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			DigitizedInfo digitizedInfo = new DigitizedInfo();
			digitizedInfo.setKey(rs.getString("key"));
			digitizedInfo.setType(rs.getString("type"));
			return digitizedInfo;
		}

	}

	@Override
	public boolean isAccessabletoSystem(Integer systemId, Integer docTypeId) {
		String query = "select * from " + dmsDbName + ".accessible_to where doc_type_id=? and system_id=?";
		boolean accTo = false;
		try {
			accTo = jdbcTemplate.queryForObject(query, new Object[] { docTypeId, systemId }, new AccessibleToMapper());
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return accTo;
	}

	private class AccessibleToMapper implements RowMapper<Boolean> {

		@Override
		public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
			AccessibleTo accTo = new AccessibleTo();
			accTo.setDocTypeId(rs.getInt("doc_type_id"));
			accTo.setSystemId(rs.getInt("system_id"));
			return true;
		}
	}

	@Override
	public boolean isFileUploadedInDms(String fileId) {
		String query = "select * from " + dmsDbName + ".uploaded_file_info where file_id=?";
		boolean isFileUploadedInDms = false;
		try {
			isFileUploadedInDms = jdbcTemplate.queryForObject(query, new Object[] { fileId }, new uploadedFileMapper());
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return isFileUploadedInDms;
	}

	private class uploadedFileMapper implements RowMapper<Boolean> {

		@Override
		public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
			return true;
		}

	}

	public File getUploadedFileInfo(String fileId) {
		String query = "select * from " + dmsDbName + ".uploaded_file_info where file_id=?";
		File file = null;
		try {
			file = jdbcTemplate.queryForObject(query, new Object[] { fileId }, new uploadedFileInfoMapper());
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return file;
	}

	private class uploadedFileInfoMapper implements RowMapper<File> {

		@Override
		public File mapRow(ResultSet rs, int rowNum) throws SQLException {
			File file = new File();
			file.setFileId(rs.getString("file_id"));
			file.setFileName(rs.getString("file_name"));
			file.setFileSize(rs.getLong("file_size"));
			return file;
		}

	}

	@Override
	public boolean doesDocumentExist(String smeId, Integer documentId, Integer versionNo, Integer docTypeId) {
		String query = "select * from " + dmsDbName + ".documents where sme_id=? and doc_type_id=? and doc_id=?";
		if (versionNo != null) {
			query = "select * from " + dmsDbName
					+ ".documents where sme_id=? and doc_type_id=? and doc_id=? and version_no=?";
		}
		List<Document> docList = null;
		try {
			if (versionNo != null) {
				docList = jdbcTemplate.query(query, new Object[] { smeId, docTypeId, documentId, versionNo },
						new DocumentMapper());
			} else {
				docList = jdbcTemplate.query(query, new Object[] { smeId, docTypeId, documentId },
						new DocumentMapper());
			}
			if (docList != null && !docList.isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return false;
	}

	@Override
	public SystemIdentity saveSystemDetail(SystemIdentity details) {
		String query = "insert into " + dmsDbName + ".system_detail (system_name) values(:systemName)";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("systemName", details.getSystemName());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(query, paramMap, keyHolder, new String[] { "system_id" });
		Integer systemId = keyHolder.getKey().intValue();
		details.setSystemId(systemId);
		return details;
	}

	@Override
	public Integer updateSystemDetail(SystemIdentity details) {
		final String query = "update " + dmsDbName
				+ ".system_detail SET system_name = :systemName  WHERE system_id = :systemId";

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("systemName", details.getSystemName());
		paramMap.addValue("systemId", details.getSystemId());
		Integer rowsAffected = namedParameterJdbcTemplate.update(query, paramMap);
		logger.debug("Rows affected :: " + rowsAffected);
		return rowsAffected;
	}

	@Override
	public DocumentTypeDto saveDocumentType(DocumentTypeDto documentType) {
		String query = "insert ignore into " + dmsDbName
				+ ".document_type (doc_type_name,category_id,max_documents) values(:docTypeName,:categoryId,:maxDocuments)";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("docTypeName", documentType.getDocumentTypeName());
		paramMap.addValue("categoryId", documentType.getCategoryId());
		if (documentType.getMaxDocuments() != null)
			paramMap.addValue("maxDocuments", documentType.getMaxDocuments());
		else
			paramMap.addValue("maxDocuments", Integer.MAX_VALUE);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(query, paramMap, keyHolder, new String[] { "doc_type_id" });
		Integer documentTypeId = keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
		documentType.setDocumentTypeId(documentTypeId);
		return documentType;
	}

	@Override
	public boolean addAccessibilityMapping(AccessibleTo mapping) {
		String query = "insert ignore into " + dmsDbName
				+ ".accessible_to (system_id,doc_type_id) values(:systemId,:docTypeId)";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("systemId", mapping.getSystemId());
		paramMap.addValue("docTypeId", mapping.getDocTypeId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(query, paramMap, keyHolder, new String[] { "id" });
		Integer id = keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
		return id != 0 ? true : false;
	}

	@Override
	public DocumentTypeDto updateDocumentType(DocumentTypeDto documentType) {
		List<DocumentType> fetchedDataList = getDocumentTypeById(documentType.getDocumentTypeId());
		DocumentType fetchedDocumentTypeInfo = fetchedDataList.get(0);
		final String query = "update " + dmsDbName + ".document_type "
				+ "SET doc_type_name = :documentDescription, category_id = :categoryId, max_documents = :maxDocuments WHERE doc_type_id = :docTypeId";

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		if (documentType.getDocumentTypeName() == null || (documentType.getDocumentTypeName() != null
				&& documentType.getDocumentTypeName().equalsIgnoreCase(fetchedDocumentTypeInfo.getDocumentTypeName())))
			paramMap.addValue("documentDescription", fetchedDocumentTypeInfo.getDocumentTypeName());
		else
			paramMap.addValue("documentDescription", documentType.getDocumentTypeName());
		if (documentType.getCategoryId() == null)
			paramMap.addValue("categoryId", fetchedDocumentTypeInfo.getCategoryId());
		else
			paramMap.addValue("categoryId", documentType.getCategoryId());
		if (documentType.getMaxDocuments() == null)
			paramMap.addValue("maxDocuments", fetchedDocumentTypeInfo.getMaxDocuments());
		else
			paramMap.addValue("maxDocuments", documentType.getMaxDocuments());

		paramMap.addValue("docTypeId", documentType.getDocumentTypeId());
		Integer rowsAffected = namedParameterJdbcTemplate.update(query, paramMap);
		logger.debug("Rows affected :: " + rowsAffected);
		return documentType;

	}

	@Override
	public List<DocumentType> getDocumentTypeByName(String docTypeName) {

		String query = "select * from " + dmsDbName + ".document_type WHERE doc_type_name = :docTypeName";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("docTypeName", docTypeName);
		return namedParameterJdbcTemplate.query(query, paramMap, new DocumentTypeMapper());

	}

	@Override
	public List<DocumentType> getDocumentTypeById(int id) {
		String query = "select * from " + dmsDbName + ".document_type WHERE doc_type_id = :docTypeId";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("docTypeId", id);
		return namedParameterJdbcTemplate.query(query, paramMap, new DocumentTypeMapper());
	}

	@Override
	public List<SearchResult> search(SearchFilterDto searchFilter) {
		final String staticQuery = "select info.key, info.value, docs.doc_id, docs.version_no ,docs.doc_type_id, docs.sme_id \n"
				+ "from   dms.documents docs  inner join dms.digital_info info on info.parent_id= docs.id where ";
		StringBuilder sb = new StringBuilder(staticQuery);
		MapSqlParameterSource paramMap = prepareParamMap(searchFilter, sb);
		sb.append("LIMIT ").append(searchFilter.getPageId() - 1).append(",").append(searchFilter.getPageSize());
		return namedParameterJdbcTemplate.query(sb.toString(), paramMap, new SearchResultMapper());
	}

	@Override
	public Integer getSearchResultCount(SearchFilterDto searchFilter) {
		final String query = "select count(*) \n"
				+ "from   dms.documents docs  inner join dms.digital_info info on info.parent_id= docs.id where ";
		StringBuilder sb = new StringBuilder(query);
		MapSqlParameterSource paramMap = prepareParamMap(searchFilter, sb);
		return namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
	}

	private MapSqlParameterSource prepareParamMap(SearchFilterDto searchFilter, StringBuilder sb) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		if (searchFilter.getSmeId() != null) {
			sb.append("docs.sme_id = :smeId");
			sb.append(" and ");
			paramMap.addValue("smeId", searchFilter.getSmeId());
		}
		if (searchFilter.getDocumentTypeId() != null) {
			sb.append("docs.doc_type_id = :docTypeId");
			sb.append(" and ");
			paramMap.addValue("docTypeId", searchFilter.getDocumentTypeId());
		}
		if (searchFilter.getText() != null) {
			sb.append("info.value like ").append(":text ");
			sb.append(" and ");
			paramMap.addValue("text", "%" + searchFilter.getText() + "%");
		}
		// sb.append("docs.system_id = :systemId ");
		sb.append("docs.doc_type_id in (select doc_type_id from dms.accessible_to where system_id= :systemId )");
		paramMap.addValue("systemId", searchFilter.getSystemId());
		return paramMap;
	}

	private class SearchResultMapper implements RowMapper<SearchResult> {

		@Override
		public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
			SearchResult searchResult = new SearchResult();
			searchResult.setDocId(rs.getLong("doc_id"));
			searchResult.setDocTypeId(rs.getInt("doc_type_id"));
			searchResult.setKey(rs.getString("key"));
			searchResult.setSmeId(rs.getString("sme_id"));
			searchResult.setValue(rs.getString("value"));
			searchResult.setVersionNo(rs.getInt("version_no"));
			return searchResult;
		}

	}

	@Override
	public List<DocumentInfoResponse> getDocumentInfo(String smeId, Integer systemId) {
		String query = "select document.* , document_type.doc_type_name, document_type.category_id,\n"
				+ "document_type.max_documents,\n"
				+ " file.file_id, file.file_name,  digital_info.key, digital_info.value from \n"
				+ " ( select system_id, doc_type_id from  dms.accessible_to where system_id=:systemId) access \n"
				+ "inner join\n"
				+ "(select sme_id, system_id , doc_type_id,  max(id) as id, max(doc_id) as doc_id, max(version_no) as version_no\n"
				+ " from dms.documents  where  sme_id=:smeId \n" + " group by sme_id, system_id , doc_type_id\n"
				+ " order by doc_type_id) document  \n" + " on access.doc_type_id =document.doc_type_id \n"
				+ " inner join dms.document_type  on document.doc_type_id=document_type.doc_type_id\n"
				+ " inner join dms.file on file.parent_id=document.id\n"
				+ " inner join dms.digital_info on document.id=digital_info.parent_id\n"
				+ " order by system_id, doc_type_id;";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("smeId", smeId);
		paramMap.addValue("systemId", systemId);

		return namedParameterJdbcTemplate.query(query, paramMap, new DocumentInfoResponsetMapper());

	}

	private class DocumentInfoResponsetMapper implements RowMapper<DocumentInfoResponse> {

		@Override
		public DocumentInfoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
			DocumentInfoResponse response = new DocumentInfoResponse();
			response.setSmeId(rs.getString("sme_id"));
			response.setSystemId(rs.getInt("system_id"));
			response.setDocTypeId(rs.getInt("doc_type_id"));
			response.setDocId(rs.getInt("doc_id"));
			response.setVersionNo(rs.getInt("version_no"));
			response.setDocTypeName(rs.getString("doc_type_name"));
			response.setCategory(rs.getInt("category_id"));
			response.setMaxDocuments(rs.getInt("max_documents"));
			response.setFileId(rs.getString("file_id"));
			response.setFileName(rs.getString("file_name"));
			response.setKey(rs.getString("key"));
			response.setValue(rs.getString("value"));
			return response;
		}

	}

	@Override
	public List<String> getCorsAllowedOrigins() {
		String query = "select * from " + dmsDbName + ".lut_allowed_origin";
		List<String> allowedOrigins = jdbcTemplate.query(query, new AllowedOriginMapper());
		return allowedOrigins;
	}

	private class AllowedOriginMapper implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("allowed_origin");
		}
	}

	@Override
	public List<DocumentsEntity> getDocumentsList(ZipFileDocuments zipFileDocuments, String smeId) throws Exception {
		logger.debug("inside getDocumentsList method");
		try {
			List<DocumentsEntity> documentsEntities = new ArrayList<>();

			// 1. no param=> show all doc for all docType of latest version
			if (zipFileDocuments.getDocumentTypeId() == null && zipFileDocuments.getVersionNo() == null) {
				documentsEntities = documentsRepository.getAllLatestDocument(smeId);
			}
			// 2. only version available=> show docs for all docType of given version
			else if (zipFileDocuments.getDocumentTypeId() == null && zipFileDocuments.getVersionNo() != null) {
				documentsEntities = documentsRepository.getAllDocumentsByVersionNo(smeId,
						zipFileDocuments.getVersionNo());
			}
			// 3. only docType available=> show docs for given docType of latest version
			else if (zipFileDocuments.getDocumentTypeId() != null && zipFileDocuments.getVersionNo() == null) {
				documentsEntities = documentsRepository.getAllDocumentsByDocTypeId(smeId,
						zipFileDocuments.getDocumentTypeId());
			}
			// 4. both docTYpe and version available=> show docs for given docTYpe and
			// versionNumber
			else {
				documentsEntities = documentsRepository.findBySmeIdAndDocTypeIdAndVersionNo(smeId,
						zipFileDocuments.getDocumentTypeId(), zipFileDocuments.getVersionNo());
			}
			return documentsEntities;
		} catch (Exception e) {
			throw new EntityNotFoundException("Exception while getting documents list");
		}
	}
}