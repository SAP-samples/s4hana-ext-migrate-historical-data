package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setBigDecimal;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setLong;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setString;

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

import lombok.extern.slf4j.Slf4j;
import nl.fountain.xelem.lex.ExcelReaderListener;

/**
 * {@link Supplier} for {@link EntityReaderListener} that reads
 * {@link SalesOrderItem} conditions
 */
@RequestScoped
@Slf4j
public class SalesOrderItemConditionsListenerSupplier implements Supplier<EntityReaderListener<SalesOrderItemConditionsListenerSupplier.ConditionDTO>> {

	public static String NET_AMOUNT = "NET_AMOUNT";

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
	public EntityReaderListener<SalesOrderItemConditionsListenerSupplier.ConditionDTO> get() {
		return EntityReader.of(ConditionDTO::new)
				.fromSheet("Item Conditions")
				.startFromRow(9) // first 8 rows are headers
				.first(setString(ConditionDTO::setLegacySalesDocument))
				.then(setLong(ConditionDTO::setSalesOrderItem))
				.then(setString(ConditionDTO::setConditionType))
				.then(setBigDecimal(ConditionDTO::setAmount, 3))
				.then(setString(ConditionDTO::setConditionUnit))
				.andSkipRestCells()
				.saveWith(itemConditionImporter())
				.listener();
	}
	
	/**
	 * Imports item condition (net value and the respective currency) from the {@link ConditionDTO} data
	 * transfer object and saves it on the item level (to the respective {@link SalesOrderItem})
	 * 
	 * @return consumer function that can be passed to
	 *         {@link EntityReader#saveWith(Consumer)} to build
	 *         {@link ExcelReaderListener}
	 */
	public Consumer<ConditionDTO> itemConditionImporter() {
		return dto -> {
			if (!NET_AMOUNT.equals(dto.getConditionType())) {
				log.warn("Condition type {} is not supported on the item level", dto.getConditionType());
				return;
			}
			
			final SalesOrder salesOrder = salesOrderRepository
					.findByLegacySalesDocument(dto.getLegacySalesDocument())
					.orElseThrow(() -> new ImportException(
							"SalesOrder with LegacySalesDocument = %s is not found but has item condition %s", 
							dto.getLegacySalesDocument(), dto));
			
			final SalesOrderItem salesOrderItem = salesOrderItemRepository
					.findBySalesOrderAndSalesOrderItem(salesOrder, dto.getSalesOrderItem())
					.orElseThrow(() -> new ImportException(
							"SalesOrderItem %s is not found for SalesOrder %s but has item condition %s", 
							dto.getSalesOrderItem(), dto.getLegacySalesDocument(), dto));
				
			salesOrderItem.setNetValue(dto.getAmount().setScale(2)); // net value is stored in Amount column using scale 3
			salesOrderItem.setDocumentCurrency(dto.getConditionUnit()); // currency is stored in Condition unit column

			salesOrderItemRepository.save(salesOrderItem);
		};
	}

}
