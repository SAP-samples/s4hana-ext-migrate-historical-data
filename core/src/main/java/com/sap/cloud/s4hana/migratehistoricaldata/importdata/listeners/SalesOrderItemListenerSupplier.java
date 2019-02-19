package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setLong;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setString;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.skipCell;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReaderListener;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.ImportException;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrderItem;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderItemRepository;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderRepository;

/**
 * {@link Supplier} for {@link EntityReaderListener} that reads
 * {@link SalesOrderItem}s
 */
@RequestScoped
public class SalesOrderItemListenerSupplier implements Supplier<EntityReaderListener<SalesOrderItem>> {

	@Inject
	SalesOrderRepository salesOrderRepository;
	
	@Inject
	SalesOrderItemRepository salesOrderItemRepository;
	
	@Override
	public EntityReaderListener<SalesOrderItem> get() {
		return EntityReader.of(SalesOrderItem::new)
				.fromSheet("Item")
				.startFromRow(9) // first 8 rows are headers
				.first(findAndSetSalesOrder(SalesOrderItem::setSalesOrder))
				.then(setLong(SalesOrderItem::setSalesOrderItem))
				.then(setString(SalesOrderItem::setMaterial))
				.then(skipCell())
				.then(setString(SalesOrderItem::setPlant))
				.then(setString(SalesOrderItem::setItemCategory))
				.then(setString(SalesOrderItem::setIncotermsVersion))
				.then(setString(SalesOrderItem::setIncotermsPart1))
				.then(setString(SalesOrderItem::setIncotermsLocation1))
				.then(setString(SalesOrderItem::setIncotermsPart2))
				.then(setString(SalesOrderItem::setIncotermsLocation2))
				.then(setString(SalesOrderItem::setTermsOfPaymentKey))
				.then(setString(SalesOrderItem::setRequestedQuantityUnit))
				.then(setString(SalesOrderItem::setMaterialDescription))
				.andSkipRestCells()
				.saveWith(salesOrderItemRepository::save)
				.listener();
	}
	
	public BiConsumer<SalesOrderItem, Object> findAndSetSalesOrder(BiConsumer<SalesOrderItem, SalesOrder> salesOrderSetter) throws ImportException {
		return (salesOrderItem, value) -> {
			// get the id of entity-owner
			final String legacySalesDocument = (String) value;
			
			// find SalesOrder entity by its id
			SalesOrder salesOrder= salesOrderRepository
					.findByLegacySalesDocument(legacySalesDocument)
					.orElseThrow(() -> new ImportException(
							"SalesOrder with LegacySalesDocument = %s is not found but has SalesOrderItem", 
							legacySalesDocument));

			// developers are responsible for keeping both sides of a JPA relationship in sync
			salesOrder.getItems().add(salesOrderItem);
			salesOrderSetter.accept(salesOrderItem, salesOrder);
		};
	}

}
