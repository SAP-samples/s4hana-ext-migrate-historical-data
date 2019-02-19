package com.sap.cloud.s4hana.migratehistoricaldata.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.inject.Inject;

import org.apache.openejb.api.LocalClient;
import org.apache.openejb.junit.jee.EJBContainerRunner;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sap.cloud.s4hana.migratehistoricaldata.service.testutils.MockUtils;
import com.sap.cloud.sdk.cloudplatform.security.Authorization;

@LocalClient
@RunWith(EJBContainerRunner.class)
public class SecurityListenerTest {

	@ClassRule
	public static MockUtils mockUtils = new MockUtils();
	
	@Inject
	SecurityListener testee;
	
	@Test
	public void testAdminAuthorizationInjection() {
		mockUtils.mockAdmin();
		
		final Authorization adminAuthorization = testee.getAdminAuthorization();		
		
		assertThat(adminAuthorization, is(instanceOf(Authorization.class)));
		assertThat(adminAuthorization.getName(), is("Admin"));
	}

}
