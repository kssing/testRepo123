package com.power2sme.dms.metadata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface MergedocSmeIdRepository extends JpaRepository<MergedocSmeIdEntity, Integer>{

	List<MergedocSmeIdEntity> findAllByStatus(int status);
	
	
}
