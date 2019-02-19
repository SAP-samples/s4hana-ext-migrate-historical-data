package com.sap.cloud.s4hana.migratehistoricaldata.security;

import javax.enterprise.inject.Produces;

import com.sap.cloud.sdk.cloudplatform.security.Authorization;
import com.sap.cloud.sdk.cloudplatform.security.Role;

/**
 * CDI producer for Neo specific admin {@link Role} that is injected
 * in {@link SecurityListener}.
 *
 * @see SecurityListener#getAdminAuthorization()
 */
public class AuthorizationsProvider {
	
	@Produces @Admin 
	Authorization admin = new Role("Admin");

}
