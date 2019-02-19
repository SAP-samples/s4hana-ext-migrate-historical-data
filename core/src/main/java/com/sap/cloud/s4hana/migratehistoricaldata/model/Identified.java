package com.sap.cloud.s4hana.migratehistoricaldata.model;

import java.io.Serializable;

import com.sap.cloud.s4hana.migratehistoricaldata.security.SecurityListener;
import com.sap.cloud.sdk.cloudplatform.auditlog.AuditLogger;

/**
 * Optional: needed to enable audit logging via {@link SecurityListener}.
 * <p>
 * Common interface for all JPA entities.
 * <p>
 * Enables to get the identifier of the entity for audit logging. The retrieved
 * identifier can be passed as {@code objectId} parameter to
 * {@link AuditLogger}'s methods, e.g.
 * {@link AuditLogger#logDataWriteAttempt(String, String, Iterable, String)} or
 * {@link AuditLogger#logDataReadAttempt(com.sap.cloud.sdk.cloudplatform.auditlog.AccessRequester, String, String, Iterable, String)}.
 * <p>
 * By default, JPA uses the same sequence for all kinds of objects which ensures
 * that their IDs are globally unique within the database schema and allows to
 * use them for audit logging.
 * 
 */
public interface Identified<T extends Serializable> {

	T getId();
	
}
