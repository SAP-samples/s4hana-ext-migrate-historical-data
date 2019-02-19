package com.sap.cloud.s4hana.migratehistoricaldata.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;

@ApplicationScoped
public class EntityManagerProducer {
	
	@PersistenceContext @Getter(onMethod = @__({@Produces}))
	private EntityManager entityManager;
	
}
