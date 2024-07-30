package com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReaderListener;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.ImportException;
import com.sap.cloud.s4hana.migratehistoricaldata.model.GenericDocument;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.service.GenericDocumentRepository;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderRepository;

import jakarta.enterprise.context.RequestScoped;

import static com.sap.cloud.s4hana.migratehistoricaldata.importdata.EntityReader.*;
import static com.sap.cloud.s4hana.migratehistoricaldata.model.GenericDocument.DOCUMENT_TYPE_SALES_ORDER;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link Supplier} for {@link EntityReaderListener} that reads document flow of
 * {@link GenericDocument}s and fills successors for {@link SalesOrder}s
 */
@RequestScoped
@Component
public class DocumentFlowListenerSupplier implements Supplier<EntityReaderListener<GenericDocument>> {

    @Autowired
    SalesOrderRepository salesOrderRepository;

    @Autowired
    GenericDocumentRepository genericDocumentRepository;

    @Override
    public EntityReaderListener<GenericDocument> get() {
        return EntityReader.of(GenericDocument::new)
                .fromSheet("DocFlow")
                .startFromRow(9) // first 8 rows are headers
                .first(setString(GenericDocument::setPredecessorCode))
                .then(setString(GenericDocument::setPredecessorType))
                .then(skipCell()) // map to a document, not to an item
                .then(setString(GenericDocument::setDocument))
                .then(setString(GenericDocument::setType))
                .then(skipCell()) // map to a document, not to an item
                .then(setCalendar(GenericDocument::setDocumentDate))
                .andSkipRestCells()
                .saveWith(prepareAndThen(genericDocumentRepository::save))
                .listener();
    }

    public Consumer<GenericDocument> prepareAndThen(Consumer<GenericDocument> genericDocumentSaver) throws ImportException {
        return (parsedDocument) -> {
            // if the document already exists, we only need to refresh it with the new predecessor
            GenericDocument documentToSave = genericDocumentRepository
                    .findByTypeAndDocument(parsedDocument.getType(), parsedDocument.getDocument())
                    .orElse(parsedDocument);

            final String predecessorType = parsedDocument.getPredecessorType();
            final String predecessorCode = parsedDocument.getPredecessorCode();
            if (DOCUMENT_TYPE_SALES_ORDER.equals(predecessorType)) {
                // find and set the predecessor (sales order) by its id
                final SalesOrder predecessor = salesOrderRepository
                        .findByLegacySalesDocument(predecessorCode)
                        .orElseThrow(() -> new ImportException(
                                "SalesOrder with LegacySalesDocument = %s is not found but declared as predecessor for %s",
                                predecessorCode, documentToSave));

                // developers are responsible for keeping both sides of a JPA relationship in sync
                documentToSave.getSalesOrders().add(predecessor);
                predecessor.getSuccessors().add(documentToSave);
            } else {
                // find and set generic predecessor by its id and type
                final GenericDocument predecessor = genericDocumentRepository
                        .findByTypeAndDocument(predecessorType, predecessorCode)
                        .orElseThrow(() -> new ImportException(
                                "Predecessor with type %s and code %s is not found for %s",
                                predecessorType, predecessorCode, documentToSave));

                // developers are responsible for keeping both sides of a JPA relationship in sync
                documentToSave.getPredecessors().add(predecessor);
                predecessor.getSuccessors().add(documentToSave);
            }

            genericDocumentSaver.accept(documentToSave);
        };
    }

}
