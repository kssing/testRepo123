package com.power2sme.dms.metadata;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface MetadataRepository extends CrudRepository<MetadataEntity, Integer> {
	
	public List<MetadataEntity> findByfileId(@RequestParam("fileId")String fileId);
}
