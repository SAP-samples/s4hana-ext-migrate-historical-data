package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.DocumentFlowListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.PartnerFunctionListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.RequestedQuantityListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.SalesOrderConditionsListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.SalesOrderHeaderListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.SalesOrderItemConditionsListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners.SalesOrderItemListenerSupplier;
import com.sap.cloud.s4hana.migratehistoricaldata.service.GenericDocumentRepository;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderRepository;

import lombok.extern.slf4j.Slf4j;
import nl.fountain.xelem.lex.ExcelReader;

@Slf4j
@Service
public class SalesOrderImportService {

    @Autowired
    SalesOrderHeaderListenerSupplier salesOrderHeaderListenerSupplier;

    @Autowired
    SalesOrderItemListenerSupplier salesOrderItemListenerSupplier;

    @Autowired
    PartnerFunctionListenerSupplier partnerFunctionListenerSupplier;

    @Autowired
    SalesOrderItemConditionsListenerSupplier salesOrderItemConditionsListenerSupplier;

    @Autowired
    SalesOrderConditionsListenerSupplier salesOrderConditionsListenerSupplier;

    @Autowired
    RequestedQuantityListenerSupplier requestedQuantityListenerSupplier;

    @Autowired
    DocumentFlowListenerSupplier documentFlowListenerSupplier;
    @Autowired
    SalesOrderRepository salesOrderRepository;
    @Autowired
    GenericDocumentRepository genericDocumentRepository;

    public ResponseEntity<String> importEntities(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        log.debug("importEntities() is called with inputStream = {}", inputStream);

        final ExcelReader excelReader = new ExcelReader();
        excelReader.addExcelReaderListener(salesOrderHeaderListenerSupplier.get());
        excelReader.addExcelReaderListener(salesOrderItemListenerSupplier.get());
        excelReader.addExcelReaderListener(partnerFunctionListenerSupplier.get());
        excelReader.addExcelReaderListener(salesOrderItemConditionsListenerSupplier.get());
        excelReader.addExcelReaderListener(salesOrderConditionsListenerSupplier.get());
        excelReader.addExcelReaderListener(requestedQuantityListenerSupplier.get());
        excelReader.addExcelReaderListener(documentFlowListenerSupplier.get());
       
        try {
            excelReader.read(new InputSource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    /**
     * Helper method that deletes all data from the database.
     * <p>
     * Mainly intended to be used for testing purposes.
     */
    public void deleteAllData() {
        log.info("deleteAllData() is called");
        genericDocumentRepository.deleteAll();
        salesOrderRepository.deleteAll();
    }

}
