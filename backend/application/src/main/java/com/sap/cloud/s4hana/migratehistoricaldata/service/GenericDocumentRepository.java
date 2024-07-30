package com.sap.cloud.s4hana.migratehistoricaldata.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sap.cloud.s4hana.migratehistoricaldata.model.GenericDocument;

import java.util.Optional;

@Repository
public interface GenericDocumentRepository extends CrudRepository<GenericDocument, Long> {

    Optional<GenericDocument> findByTypeAndDocument(String type, String document);

}
