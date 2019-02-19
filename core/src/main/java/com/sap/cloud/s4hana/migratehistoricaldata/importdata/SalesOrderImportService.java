package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

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
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogger;

import lombok.extern.slf4j.Slf4j;
import nl.fountain.xelem.lex.ExcelReader;

@Slf4j
public class SalesOrderImportService {
	
	@Inject
	SalesOrderHeaderListenerSupplier salesOrderHeaderListenerSupplier;
	
	@Inject
	SalesOrderItemListenerSupplier salesOrderItemListenerSupplier;
	
	@Inject
	PartnerFunctionListenerSupplier partnerFunctionListenerSupplier;

	@Inject
	SalesOrderItemConditionsListenerSupplier salesOrderItemConditionsListenerSupplier;
	
	@Inject
	SalesOrderConditionsListenerSupplier salesOrderConditionsListenerSupplier;
	
	@Inject
	RequestedQuantityListenerSupplier requestedQuantityListenerSupplier;
	
	@Inject
	DocumentFlowListenerSupplier documentFlowListenerSupplier;
	
	public void importEntities(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		log.debug("importEntities() is called with inputStream = {}", inputStream);
		AuditLogger.logSecurityEvent("Import function is called", /* error = */ null);
		
		final ExcelReader excelReader = new ExcelReader();
		excelReader.addExcelReaderListener(salesOrderHeaderListenerSupplier.get());
		excelReader.addExcelReaderListener(salesOrderItemListenerSupplier.get());
		excelReader.addExcelReaderListener(partnerFunctionListenerSupplier.get());
		excelReader.addExcelReaderListener(salesOrderItemConditionsListenerSupplier.get());
		excelReader.addExcelReaderListener(salesOrderConditionsListenerSupplier.get());
		excelReader.addExcelReaderListener(requestedQuantityListenerSupplier.get());
		excelReader.addExcelReaderListener(documentFlowListenerSupplier.get());
		
		excelReader.read(new InputSource(inputStream));
	}
	
	@Inject
	SalesOrderRepository salesOrderRepository;
	
	@Inject
	GenericDocumentRepository genericDocumentRepository;
	
	/**
	 * Helper method that deletes all data from the database.
	 * <p>
	 * Mainly intended to be used for testing purposes.
	 */
	public void deleteAllData() {
		log.info("deleteAllData() is called");
		AuditLogger.logSecurityEvent("Delete all data is called", /* error = */ null);

		genericDocumentRepository.findAll().forEach(genericDocumentRepository::remove);
		salesOrderRepository.findAll().forEach(salesOrderRepository::remove);
	}

}
