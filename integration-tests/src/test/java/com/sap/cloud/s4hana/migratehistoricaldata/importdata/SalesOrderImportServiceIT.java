package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.openejb.api.LocalClient;
import org.apache.openejb.junit.jee.EJBContainerRunner;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import com.sap.cloud.s4hana.migratehistoricaldata.importdata.SalesOrderImportService;
import com.sap.cloud.s4hana.migratehistoricaldata.model.GenericDocument;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrder;
import com.sap.cloud.s4hana.migratehistoricaldata.model.SalesOrderItem;
import com.sap.cloud.s4hana.migratehistoricaldata.security.SecurityTestUtil;
import com.sap.cloud.s4hana.migratehistoricaldata.service.GenericDocumentRepository;
import com.sap.cloud.s4hana.migratehistoricaldata.service.SalesOrderRepository;
import com.sap.cloud.s4hana.migratehistoricaldata.service.testutils.MockUtils;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLog;

@LocalClient
@RunWith(EJBContainerRunner.class)
public class SalesOrderImportServiceIT {
	
	/**
	 * Test import file located in {@code /src/test/resources/} folder
	 */
	private static final String TEST_IMPORT_FILE = "templates/Import_Test_Data.xml";

	@ClassRule
	public static MockUtils mockUtils = new MockUtils();
	
	@Inject
	SalesOrderImportService salesOrderImportService;
	
	@Inject
	SalesOrderRepository salesOrderRepository;
	
	@Inject
	GenericDocumentRepository genericDocumentRepository;
	
	@Test
	public void testImport() throws ParserConfigurationException, SAXException, IOException {
		// Given a mocked current user that has Admin authorization
		mockUtils.mockAdmin();
		final AuditLog auditLogMock = mockUtils.mockAuditLog();
		
		// When
		salesOrderImportService.importEntities(from(TEST_IMPORT_FILE));
		
		// Then sales orders are imported
		final List<SalesOrder> allSalesOrders = salesOrderRepository.findAll();
		assertThat("Number of imported sales orders", 
				allSalesOrders.size(), 
				is(4));
		
		// Here you can also assert individual properties of imported entities.
		// Please note that equals() cannot be used to test deep equality since
		// it only compares IDs		
		
		// Then sales order items are imported
		final List<SalesOrderItem> allItems = allSalesOrders.stream()
				.flatMap(order -> order.getItems().stream())
				.collect(Collectors.toList());
		assertThat("Number of imported sales order items", 
				allItems.size(), 
				is(6));
		
		// Here you can also assert individual properties of imported entities.
		// Please note that equals() cannot be used to test deep equality since
		// it only compares IDs
		
		// Then document flow is imported
		final List<GenericDocument> documentFlow = genericDocumentRepository.findAll();
		assertThat("Number of imported generic documents in the docment flow", 
				documentFlow.size(), 
				is(2));
		
		// Here you can also assert individual properties of imported entities.
		// Please note that equals() cannot be used to test deep equality since
		// it only compares IDs
		
		// Then the event is logged with the audit logger
		verify(auditLogMock).logSecurityEvent(any(), eq("Import function is called"), eq(null));
	}
	
	@Test
	public void testImportWhenNoAdminAuthorizationThenSecurityException() throws ParserConfigurationException, SAXException, IOException {
		// Given a mocked current user that does NOT have Admin authorization
		mockUtils.mockUser("Nobody");
		final AuditLog auditLogMock = mockUtils.mockAuditLog();
				
		try {
			// When
			salesOrderImportService.importEntities(from(TEST_IMPORT_FILE));
			
			// Then
			fail("Expected to throw an exception due to insufficient privileges of the current user");
		} catch (Throwable e) {
			final Throwable cause = SecurityTestUtil.assertSecurityException(e);
			
			// Then the event is logged with the audit logger
			verify(auditLogMock).logSecurityEvent(any(), eq("Import function is called"), eq(null));
			
			// Then the failed data write attempt is logged with the audit logger
			verify(auditLogMock).logDataWrite(any(), any(), any(), any(), eq(cause));
		}
	}

	/**
	 * @param path
	 *            relative path to a resource to be loaded. The path is relative
	 *            to a test resources folder (e.g. {@code /src/test/resources/})
	 * @return {@link InputStream} of the test resource in a test resources
	 *         folder.
	 */
	private static InputStream from(final String path) {
		final ClassLoader classLoader = SalesOrderImportServiceIT.class.getClassLoader();
		return classLoader.getResourceAsStream(path);
	}
	
}
