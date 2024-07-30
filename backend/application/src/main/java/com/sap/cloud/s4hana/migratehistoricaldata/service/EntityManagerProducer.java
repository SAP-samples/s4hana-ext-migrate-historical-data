package com.sap.cloud.s4hana.migratehistoricaldata.service;

import lombok.Getter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceContext
    @Getter(onMethod = @__({@Produces}))
    private EntityManager entityManager;

}
