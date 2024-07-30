package com.sap.cloud.s4hana.migratehistoricaldata.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrderItem;

import java.util.Optional;

@Repository
public interface SalesOrderItemRepository extends CrudRepository<SalesOrderItem, Long> {

    public Optional<SalesOrderItem> findBySalesOrderAndSalesOrderItem(SalesOrder salesOrder, Long salesOrderItem);

}
