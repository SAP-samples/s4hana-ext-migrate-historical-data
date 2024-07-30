package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import lombok.extern.slf4j.Slf4j;
import nl.fountain.xelem.lex.ExcelReaderListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReaderListener;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.ImportException;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderRepository;

import jakarta.enterprise.context.RequestScoped;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setBigDecimal;
import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.setString;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link Supplier} for {@link EntityReaderListener} that reads total net amount
 * from header conditions of {@link SalesOrder}s
 */
@RequestScoped
@Slf4j
@Component
public class SalesOrderConditionsListenerSupplier implements Supplier<EntityReaderListener<SalesOrderConditionsListenerSupplier.ConditionDTO>> {

    public static String TOTAL_NET_AMOUNT = "TOTAL_NET_AMOUNT";
    @Autowired
    SalesOrderRepository salesOrderRepository;

    @Override
    public EntityReaderListener<SalesOrderConditionsListenerSupplier.ConditionDTO> get() {
        return EntityReader.of(ConditionDTO::new)
                .fromSheet("Header Conditions")
                .startFromRow(9) // first 8 rows are headers
                .first(setString(ConditionDTO::setLegacySalesDocument))
                .then(setString(ConditionDTO::setConditionType))
                .then(setBigDecimal(ConditionDTO::setAmount, 3))
                .then(setString(ConditionDTO::setConditionUnit))
                .andSkipRestCells()
                .saveWith(headerConditionImporter())
                .listener();
    }

    /**
     * Imports item condition (net value and the respective currency) from the {@link ConditionDTO} data
     * transfer object and saves it on the header level (to the respective {@link SalesOrder})
     *
     * @return consumer function that can be passed to
     * {@link EntityReader#saveWith(Consumer)} to build
     * {@link ExcelReaderListener}
     */
    public Consumer<ConditionDTO> headerConditionImporter() {
        return dto -> {
            if (!TOTAL_NET_AMOUNT.equals(dto.getConditionType())) {
                log.debug("Condition type {} is not supported on the header level", dto.getConditionType());
                return;
            }

            final SalesOrder salesOrder = salesOrderRepository
                    .findByLegacySalesDocument(dto.getLegacySalesDocument())
                    .orElseThrow(() -> new ImportException(
                            "SalesOrder with LegacySalesDocument = %s is not found but has header condition %s",
                            dto.getLegacySalesDocument(), dto));

            salesOrder.setNetValue(dto.getAmount().setScale(2)); // net value is stored in Amount column using scale 3
            salesOrder.setDocumentCurrency(dto.getConditionUnit()); // currency is stored in Condition unit column

            salesOrderRepository.save(salesOrder);
        };
    }

    @lombok.Data
    protected static class ConditionDTO {
        private String legacySalesDocument;
        private Long salesOrderItem;
        private String conditionType;
        private BigDecimal amount;
        private String conditionUnit;
    }

}
