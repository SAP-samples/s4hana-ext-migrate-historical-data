package com.sap.cloud.s4hana.migratehistoricaldata.rest;

import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.SalesOrderImportService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rest/SalesOrders")
public class SalesOrderImportController {

    @Autowired
    SalesOrderImportService salesOrderImportService;

    /**
     * Imports data from the file attached to the request
     *
     * @param attachment a SpreadsheetML file from which the data is imported
     */

    @PostMapping("/import")
    public ResponseEntity<String> handleUpload(HttpServletRequest request) throws Exception {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        FileItemInputIterator iterStream = upload.getItemIterator(request);
        FileItemInput item = iterStream.next();
        return salesOrderImportService.importEntities(item.getInputStream());
    }

    /**
     * Helper method that deletes all data from the database.
     * <p>
     * Mainly intended to be used for testing purposes.
     */
    @DeleteMapping
    public void deleteAllData() {
        log.info("DELETE /SalesOrders REST endpoint is called");

        salesOrderImportService.deleteAllData();
    }

}
