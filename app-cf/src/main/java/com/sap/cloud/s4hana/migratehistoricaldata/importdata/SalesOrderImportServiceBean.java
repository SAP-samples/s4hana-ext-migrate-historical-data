package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;

@Alternative
@Stateless // make all methods transactional
public class SalesOrderImportServiceBean extends SalesOrderImportService {

}
