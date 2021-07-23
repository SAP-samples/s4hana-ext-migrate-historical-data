package com.sap.cloud.s4hana.migratehistoricaldata.service.testutils;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLog;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogFacade;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogger;
import com.sap.cloud.sdk.cloudplatform.security.Authorization;
import com.sap.cloud.sdk.cloudplatform.security.Role;
import com.sap.cloud.sdk.cloudplatform.security.Scope;
import com.sap.cloud.sdk.cloudplatform.security.user.User;
import com.sap.cloud.sdk.testutil.MockUtil;

import lombok.Getter;

/**
 * JUnit {@link ClassRule} that mocks SAP Business Technology Platform (BTP) environment for local
 * testing.
 * 
 * @see ClassRule @ClassRule annotation
 */
public class MockUtils extends ExternalResource {
	
	@Getter
	private MockUtil mockUtil = new MockUtil();
	
	private AuditLog auditLogMock;

	@Override
	protected void before() throws Throwable {
		mockUtil.mockDefaults();		
	}
	
	/**
	 * Mocks audit logger with no-op mock.
	 * 
	 * @see Mockito#mock(Class)
	 * @see MockUtils#mockAuditLog(AuditLog)
	 */
	public AuditLog mockAuditLog() {
		return mockAuditLog(mock(AuditLog.class));
	}

	/**
	 * Mocks audit log with the provided {@code delegate}.
	 * <p>
	 * In comparison to {@link MockUtil#mockAuditLog(AuditLog)}, this function
	 * doesn't require platform specific audit logger implementation to be
	 * provided.
	 * 
	 * @param delegate
	 *            {@link AuditLog} to be used
	 * @see MockUtil#mockAuditLog(AuditLog)
	 */
	public AuditLog mockAuditLog(AuditLog delegate) {
		auditLogMock = delegate;
		 
		final AuditLogFacade auditLogFacadeMock = mock(AuditLogFacade.class);
		doReturn(auditLogMock).when(auditLogFacadeMock).getAuditLog();
		AuditLogger.setAuditLogFacade(auditLogFacadeMock);
		
		return auditLogMock;
	}
	
	public User mockAdmin() {
		return mockUser("Admin", 
				/* Neo authorization */ new Role("Admin"), 
				/* CloudFoundry authorization */ new Scope("Admin"));
	}

	public User mockUser(final String username, final Authorization... authorizations) {
		return mockUtil.mockCurrentUser(username, 
				Locale.getDefault(), 
				Arrays.asList(authorizations), 
				Collections.emptyMap());
	}
	
}
