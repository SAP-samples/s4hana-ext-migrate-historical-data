package com.sap.cloud.s4hana.migratehistoricaldata.security;

import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.s4hana.migratehistoricaldata.model.Identified;
import com.sap.cloud.sdk.cloudplatform.security.Authorization;
import com.sap.cloud.sdk.cloudplatform.security.Role;
import com.sap.cloud.sdk.cloudplatform.security.Scope;
import com.sap.cloud.sdk.cloudplatform.security.user.User;
import com.sap.cloud.sdk.cloudplatform.security.user.UserAccessor;
import com.sap.cloud.sdk.cloudplatform.security.user.exception.UserAccessException;
import com.sap.cloud.sdk.cloudplatform.security.user.exception.UserNotAuthenticatedException;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * JPA Entity Listener that checks if the current user has {@link Admin}
 * authorization to alter the data.
 *
 */
public class SecurityListener {
	
	/**
	 * @return platform-specific admin authorization. In case of Cloud Foundry
	 *         it is {@link Scope} while on Neo it is {@link Role}
	 */
	@Inject @Admin 
	@Getter(value = AccessLevel.PROTECTED, onMethod_={@VisibleForTesting})
	private Authorization adminAuthorization;
	
	@PrePersist
	protected void beforePersist(Object entity) {
		before("persist", entity);
	}
	
	@PreUpdate
	protected void beforeUpdate(Object entity) {
		before("update", entity);
	}
	
	@PreRemove
	protected void beforeRemove(Object entity) {
		before("remove", entity);
	}

	@VisibleForTesting
	protected void before(String operation, Object entity) {
		try {
			User currentUser = UserAccessor.getCurrentUser();
			if (!currentUser.hasAuthorization(adminAuthorization)) {
				AuditLogUtil.logDataWriteSecurityException(operation, (Identified<?>) entity);
			}
		} catch (UserNotAuthenticatedException | UserAccessException e) {
			AuditLogUtil.logDataWriteSecurityException(operation, (Identified<?>) entity);
		}
	}
	
}
