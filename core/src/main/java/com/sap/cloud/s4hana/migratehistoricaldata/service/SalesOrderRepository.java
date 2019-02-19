package com.sap.cloud.s4hana.migratehistoricaldata.service;

import java.util.Optional;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;

@Repository
public interface SalesOrderRepository extends EntityRepository<SalesOrder, Long> {
	
	Optional<SalesOrder> findByLegacySalesDocument(String legacySalesDocument); 

}
