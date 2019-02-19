package com.sap.cloud.s4hana.migratehistoricaldata.rest;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.xml.sax.SAXException;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.SalesOrderImportService;

import lombok.extern.slf4j.Slf4j;

@Path("/SalesOrders")
@Slf4j
public class SalesOrderImportController {
	
	@Inject
	SalesOrderImportService salesOrderImportService;
	
	/**
	 * Imports data from the file attached to the request
	 * 
	 * @param attachment
	 *            a SpreadsheetML file from which the data is imported
	 */
	@POST
	@Path("/import")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void importSalesOrders(@Multipart(value = "file") Attachment attachment) throws ParserConfigurationException, SAXException, IOException {
		log.info("POST /SalesOrders/import REST endpoint is called with attachment={}", attachment);
		
		salesOrderImportService.importEntities(attachment.getObject(InputStream.class));
	}
	
	/**
	 * Helper method that deletes all data from the database.
	 * <p>
	 * Mainly intended to be used for testing purposes.
	 */
	@DELETE
	public void deleteAllData() {
		log.info("DELETE /SalesOrders REST endpoint is called");

		salesOrderImportService.deleteAllData();
	}
	
}
