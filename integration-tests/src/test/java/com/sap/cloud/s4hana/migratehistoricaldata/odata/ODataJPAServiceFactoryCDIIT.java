package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.inject.Inject;

import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.openejb.api.LocalClient;
import org.apache.openejb.junit.jee.EJBContainerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EJBContainerRunner.class)
@LocalClient
public class ODataJPAServiceFactoryCDIIT {
	
	@Inject
	ContainerManagedODataJPAServiceFactory testee;
	
	@Test
	public void testPersistenceContextIsInjectedIntoODataServiceFactory() {
		assertThat("EntityManager is not injected", 
				testee.entityManager, is(not(nullValue())));
	}
	
	@Test
	public void testUserTransactionIsInjectedIntoODataServiceFactory() throws ODataJPARuntimeException {
		assertThat("Incorrect UserTransaction is injected", 
				testee.oDataJPAProcessorCMT, is(instanceOf(DelegatingODataJPAProcessor.class)));
	}
	
}
