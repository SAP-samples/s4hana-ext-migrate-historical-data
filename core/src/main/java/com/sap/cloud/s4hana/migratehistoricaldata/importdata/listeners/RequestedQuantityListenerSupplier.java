package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setBigDecimal;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setLong;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setString;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.skipCell;

import java.math.BigDecimal;
import java.util.function.Consumer;
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

import nl.fountain.xelem.lex.ExcelReaderListener;

/**
 * {@link Supplier} for {@link EntityReaderListener} that reads and aggregates
 * requested quantity from schedule lines of {@link SalesOrderItem}s
 */
@RequestScoped
public class RequestedQuantityListenerSupplier implements Supplier<EntityReaderListener<RequestedQuantityListenerSupplier.ConditionDTO>> {

	@lombok.Data
	protected static class ConditionDTO {
		private String legacySalesDocument;
		private Long salesOrderItem;
		private String conditionType;
		private BigDecimal amount;
		private String conditionUnit;
	}
	
	@Inject
	SalesOrderRepository salesOrderRepository;
	
	@Inject
	SalesOrderItemRepository salesOrderItemRepository;
	
	@Override
	public EntityReaderListener<RequestedQuantityListenerSupplier.ConditionDTO> get() {
		return EntityReader.of(ConditionDTO::new)
				.fromSheet("Schedule Line Data")
				.startFromRow(9) // first 8 rows are headers
				.first(setString(ConditionDTO::setLegacySalesDocument))
				.then(setLong(ConditionDTO::setSalesOrderItem))
				.then(skipCell()) // ignore schedule lines
				.then(setBigDecimal(ConditionDTO::setAmount, 3))
				.andSkipRestCells()
				.saveWith(requestedQuantityImporter())
				.listener();
	}
	
	/**
	 * Imports requested quantities from the {@link ConditionDTO} data transfer object and saves it on the header level 
	 * (to the respective {@link SalesOrder})
	 * 
	 * @return consumer function that can be passed to
	 *         {@link EntityReader#saveWith(Consumer)} to build
	 *         {@link ExcelReaderListener}
	 */
	public Consumer<ConditionDTO> requestedQuantityImporter() throws ImportException {
		return dto -> {
			final SalesOrder salesOrder = salesOrderRepository
					.findByLegacySalesDocument(dto.getLegacySalesDocument())
					.orElseThrow(() -> new ImportException(
							"SalesOrder with LegacySalesDocument = %s is not found but has requested quantity at item level: %s",
							dto.getLegacySalesDocument(), dto));
			
			final SalesOrderItem salesOrderItem = salesOrderItemRepository
					.findBySalesOrderAndSalesOrderItem(salesOrder, dto.getSalesOrderItem())
					.orElseThrow(() -> new ImportException(
							"SalesOrderItem %s is not found for SalesOrder %s but has requested quantity %s",
							dto.getSalesOrderItem(), dto.getLegacySalesDocument(), dto));
				
			if (dto.getAmount() == null) {
				throw new ImportException("Requested quantity is not specified in the import file");
			}
			
			if (salesOrderItem.getRequestedQuantity() == null) {
				salesOrderItem.setRequestedQuantity(dto.getAmount());
			} else {
				final BigDecimal cumulativeQuantity = salesOrderItem.getRequestedQuantity().add(dto.getAmount());
				salesOrderItem.setRequestedQuantity(cumulativeQuantity);
			}

			salesOrderItemRepository.save(salesOrderItem);
		};
	}

}
