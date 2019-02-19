package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setLong;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setString;

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
 * {@link Supplier} for {@link EntityReaderListener} that reads Sold-to-Party
 * and Ship-to-Party partner functions of {@link SalesOrder}s and Ship-to-Party
 * partner function of {@link SalesOrderItem}s
 */
@RequestScoped
@Slf4j
public class PartnerFunctionListenerSupplier implements Supplier<EntityReaderListener<PartnerFunctionListenerSupplier.PartnerFunctionDTO>> {

	public static String SOLD_TO_PARTY = "AG";
	public static String SHIP_TO_PARTY = "WE";

	@lombok.Data
	protected static class PartnerFunctionDTO {
		private String legacySalesDocument;
		private Long salesOrderItem;
		private String partnerFunction;
		private String partner;
	}
	
	@Inject
	SalesOrderRepository salesOrderRepository;
	
	@Inject
	SalesOrderItemRepository salesOrderItemRepository;
	
	@Override
	public EntityReaderListener<PartnerFunctionListenerSupplier.PartnerFunctionDTO> get() {
		return EntityReader.of(PartnerFunctionDTO::new)
				.fromSheet("Partner")
				.startFromRow(9) // first 8 rows are headers
				.first(setString(PartnerFunctionDTO::setLegacySalesDocument))
				.then(setLong(PartnerFunctionDTO::setSalesOrderItem))
				.then(setString(PartnerFunctionDTO::setPartnerFunction))
				.then(setString(PartnerFunctionDTO::setPartner))
				.andSkipRestCells()
				.saveWith(partnerFunctionImporter())
				.listener();
	}
	
	/**
	 * Imports a partner function from the {@link PartnerFunctionDTO} data
	 * transfer object and saves it either on the header level of the respecive
	 * {@link SalesOrder} or on the item level (to the respective
	 * {@link SalesOrderItem})
	 * 
	 * @return consumer function that can be passed to
	 *         {@link EntityReader#saveWith(Consumer)} to build
	 *         {@link ExcelReaderListener}
	 */
	protected Consumer<PartnerFunctionDTO> partnerFunctionImporter() {
		return dto -> {
			final SalesOrder salesOrder = salesOrderRepository
					.findByLegacySalesDocument(dto.getLegacySalesDocument())
					.orElseThrow(() -> new ImportException(
							"SalesOrder with LegacySalesDocument = %s is not found but has partner function %s",
							dto.getLegacySalesDocument(), dto));
			
			if (dto.getSalesOrderItem() == 0) {
				// header level
				if (SOLD_TO_PARTY.equals(dto.getPartnerFunction())) {
					salesOrder.setSoldToParty(dto.getPartner());
				} else if (SHIP_TO_PARTY.equals(dto.getPartnerFunction())) {
					salesOrder.setShipToParty(dto.getPartner());
				} else {
					log.warn("Partner function {} is not supported on the header level", dto.getPartnerFunction());
					return;
				}
				
				salesOrderRepository.save(salesOrder);
			} else {
				// item level
				final SalesOrderItem salesOrderItem = salesOrderItemRepository
						.findBySalesOrderAndSalesOrderItem(salesOrder, dto.getSalesOrderItem())
						.orElseThrow(() -> new ImportException(
								"SalesOrderItem %s is not found for SalesOrder %s but has partner function %s", 
								dto.getSalesOrderItem(), dto.getLegacySalesDocument(), dto));
				
				if (SHIP_TO_PARTY.equals(dto.getPartnerFunction())) {
					salesOrderItem.setShipToParty(dto.getPartner());
				} else {
					log.warn("Partner function {} is not supported on the item level", dto.getPartnerFunction());
					return;
				}
				
				salesOrderItemRepository.save(salesOrderItem);
			}
		};
	}

}
