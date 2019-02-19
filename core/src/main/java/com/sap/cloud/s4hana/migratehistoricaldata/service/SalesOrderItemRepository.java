package com.sap.cloud.s4hana.migratehistoricaldata.service;

import java.util.Optional;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrderItem;

@Repository
public interface SalesOrderItemRepository extends EntityRepository<SalesOrderItem, Long> {
	
	public Optional<SalesOrderItem> findBySalesOrderAndSalesOrderItem(SalesOrder salesOrder, Long salesOrderItem);

}
