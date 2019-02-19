package com.sap.cloud.s4hana.migratehistoricaldata.security;

import javax.enterprise.inject.Produces;

import com.sap.cloud.sdk.cloudplatform.security.Authorization;
import com.sap.cloud.sdk.cloudplatform.security.Scope;

/**
 * CDI producer for CloudFoundry specific admin {@link Scope} that is injected
 * in {@link SecurityListener}.
 *
 * @see SecurityListener#getAdminAuthorization()
 */
public class AuthorizationsProvider {
	
	@Produces @Admin
	Authorization admin = new Scope("Admin");

}
