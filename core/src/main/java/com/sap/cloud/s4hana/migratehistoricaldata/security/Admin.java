package com.sap.cloud.s4hana.migratehistoricaldata.security;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;

import com.sap.cloud.sdk.cloudplatform.security.Authorization;
import com.sap.cloud.sdk.cloudplatform.security.Role;
import com.sap.cloud.sdk.cloudplatform.security.Scope;

/**
 * CDI Qualifier annotation that specifies which {@link Authorization} should be
 * injected in {@link SecurityListener}.
 * <p>
 * Neo application must contain a CDI producer that returns {@link Role}.<br>
 * Cloud Foundry application must contain a CDI producer that returns {@link Scope}.
 * 
 * @see SecurityListener#getAdminAuthorization()
 * @see Produces
 * 
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
public @interface Admin {

}
