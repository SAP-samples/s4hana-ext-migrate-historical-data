package com.sap.cloud.s4hana.migratehistoricaldata.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;

import java.util.Optional;

@Repository
public interface SalesOrderRepository extends CrudRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByLegacySalesDocument(String legacySalesDocument);

}
