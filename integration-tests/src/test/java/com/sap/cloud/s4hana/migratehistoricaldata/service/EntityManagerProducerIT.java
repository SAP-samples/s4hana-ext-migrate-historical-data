package com.sap.cloud.s4hana.migratehistoricaldata.service;

import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.openejb.api.LocalClient;
import org.apache.openejb.junit.jee.EJBContainerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@LocalClient
@RunWith(EJBContainerRunner.class)
public class EntityManagerProducerIT {

	@Inject
	EntityManager em;
	
	@Test
	public void testEntityManagerInjection() {
		assertThat("Entity manager should get injected via @Inject annotation", em != null);
	}
	
}
