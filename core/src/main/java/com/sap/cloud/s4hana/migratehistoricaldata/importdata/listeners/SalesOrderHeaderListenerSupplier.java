package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setBigDecimal;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setCalendar;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setString;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.skipCell;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.times;

import java.util.function.Supplier;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReaderListener;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderRepository;

/**
 * {@link Supplier} for {@link EntityReaderListener} that reads header data of
 * {@link SalesOrder}s
 */
@RequestScoped
public class SalesOrderHeaderListenerSupplier implements Supplier<EntityReaderListener<SalesOrder>> {

	@Inject
	SalesOrderRepository salesOrderRepository;
	
	@Override
	public EntityReaderListener<SalesOrder> get() {
		return EntityReader.of(SalesOrder::new)
				.fromSheet("Header")
				.startFromRow(9) // first 8 rows are headers
				.first(setString(SalesOrder::setLegacySalesDocument))
				.then(setString(SalesOrder::setSalesDocumentType))
				.then(setString(SalesOrder::setSalesOrganization))
				.then(skipCell(), times(3))
				.then(setCalendar(SalesOrder::setSalesOrderDate))
				.then(skipCell(), times(9))
				.then(setBigDecimal(SalesOrder::setNetValue, 2))
				.then(setString(SalesOrder::setDocumentCurrency))
				.andSkipRestCells()
				.saveWith(salesOrderRepository::save)
				.listener();
	}

}
