package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;

@Alternative
@Transactional
public class SalesOrderImportServiceCDI extends SalesOrderImportService {

}
