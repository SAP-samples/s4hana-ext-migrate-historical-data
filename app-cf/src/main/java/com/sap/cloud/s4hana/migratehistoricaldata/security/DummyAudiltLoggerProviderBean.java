package com.sap.cloud.s4hana.migratehistoricaldata.security;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.sap.cloud.sdk.cloudplatform.auditlog.AccessRequester;
import com.sap.cloud.sdk.cloudplatform.auditlog.AccessedAttribute;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLog;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogFacade;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogger;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditedDataObject;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditedDataSubject;
import com.sap.cloud.sdk.cloudplatform.auditlog.exception.AuditLogAccessException;

import lombok.extern.slf4j.Slf4j;

/**
 * Optional: only needed for testing purposes on Cloud Foundry accounts without
 * audit logger service instance e.g. trial accounts.
 * <p>
 * Remove the @{@link Startup} and @{@link Singleton} annotations or the entire
 * file if the audit logger functionality is mandatory.
 * <p>
 * <b>WARNING:</b> Do not use this class in case Audit Logger is mandatory since
 * it will hide exceptions when getting the audit logger.
 *
 */
@Startup
@Singleton
@Slf4j
public class DummyAudiltLoggerProviderBean {
	
	@PostConstruct
	public void onInit() {
		try {
			AuditLogger.getAuditLog();
		} catch (Exception e) {
			log.error("Exception when trying to get AuditLog. "
					+ "No audit events will be logged.", e);
			AuditLogger.setAuditLogFacade(new NoOpAuditLogFacade());
		}
	}
	
	protected static class NoOpAuditLogFacade implements AuditLogFacade {

		@Override
		public AuditLog getAuditLog() throws AuditLogAccessException {
			return new NoOpAuditLog();
		}

		@Override
		public Class<? extends AuditLog> getAuditLogClass() {
			return null;
		}
		
	}
	
	protected static class NoOpAuditLog implements AuditLog {

		@Override
		public void logConfigChange(AccessRequester initiator, AuditedDataObject object,
				Iterable<AccessedAttribute> attributesAffected, Throwable error) {
			// nothing to do here
		}

		@Override
		public void logConfigChangeBeginning(AccessRequester initiator, AuditedDataObject object,
				Iterable<AccessedAttribute> attributesAffected) {
			// nothing to do here
		}

		@Override
		public void logDataRead(AccessRequester initiator, AuditedDataObject object, AuditedDataSubject subject,
				Iterable<AccessedAttribute> attributesAffected, Throwable error) {
			// nothing to do here
		}

		@Override
		public void logDataReadAttempt(AccessRequester initiator, AuditedDataObject object, AuditedDataSubject subject,
				Iterable<AccessedAttribute> attributesAffected) {
			// nothing to do here
		}

		@Override
		public void logDataWrite(AccessRequester initiator, AuditedDataObject object, AuditedDataSubject subject,
				Iterable<AccessedAttribute> attributesAffected, Throwable error) {
			// nothing to do here
		}

		@Override
		public void logDataWriteAttempt(AccessRequester initiator, AuditedDataObject object, AuditedDataSubject subject,
				Iterable<AccessedAttribute> attributesAffected) {
			// nothing to do here
		}

		@Override
		public void logSecurityEvent(AccessRequester initiator, String message, Throwable throwable) {
			// nothing to do here
		}

		@Override
		public void logSecurityEventBeginning(AccessRequester initiator, String message) {
			// nothing to do here
		}
		
	}

}
