package com.sap.cloud.s4hana.migratehistoricaldata.service;

import java.util.Optional;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import com.sap.cloud.s4hana.migratehistoricaldata.model.GenericDocument;

@Repository
public interface GenericDocumentRepository extends EntityRepository<GenericDocument, Long> {
	
	Optional<GenericDocument> findByTypeAndDocument(String type, String document);
	
}
