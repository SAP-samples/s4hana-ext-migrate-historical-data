package com.sap.cloud.s4hana.migratehistoricaldata.security;

import com.sap.cloud.s4hana.migratehistoricaldata.model.Identified;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogger;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditedDataObject;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditedDataSubject;

/**
 * Util class with helper methods that simplify usage of {@link AuditLogger}
 *
 */
public class AuditLogUtil {
	
	private AuditLogUtil() {
		// prevents util class from being instantiated
	}

	/**
	 * Creates {@link SecurityException}, calls
	 * {@link AuditLogger#logDataWrite(AuditedDataObject, AuditedDataSubject, Iterable, Throwable)}
	 * with it for the given {@code entity} and {@code operation} and throws the
	 * exception
	 * 
	 * @param operation
	 *            CUD operation (persist, update or remove)
	 * @param entity
	 *            JPA entity being persisted
	 * @return {@link SecurityException} that was passed to
	 *         {@link AuditLogger#logDataWrite(AuditedDataObject, AuditedDataSubject, Iterable, Throwable)}
	 * @throws SecurityException always
	 * 
	 */
	public static void logDataWriteSecurityException(String operation, Identified<?> entity) throws SecurityException {
		final SecurityException e = new SecurityException(String.format("Current user is not authorized to %s a %s",
				operation,
				entity.getClass().getSimpleName()));
		
		AuditLogger.logDataWrite(toAuditedDataObject(entity),
				/* auditedDataSubject - the one who owns the data */ null,
				/* attributesAffected are omitted for the sake of simplicity */ null, 
				/* exception */ e);

		throw e;
	}

	/**
	 * @param entity
	 *            JPA entity
	 * @return {@link AuditedDataObject} which type is the class name of
	 *         {@code entity} and identifier is {@link Identified#getId()}
	 */
	public static AuditedDataObject toAuditedDataObject(Identified<?> entity) {
		final String auditedObjectType = entity.getClass().getSimpleName();
		final AuditedDataObject auditedDataObject = new AuditedDataObject(auditedObjectType);
		auditedDataObject.setIdentifier("id", entity.getId().toString());
		return auditedDataObject;
	}

}
