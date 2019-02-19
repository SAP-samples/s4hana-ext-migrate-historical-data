package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.core.exception.ODataRuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;

import com.sap.cloud.s4hana.migratehistoricaldata.service.EntityManagerProducer;

import lombok.extern.slf4j.Slf4j;

/**
 * Detailed configuration of OData version 2 service generated from Java
 * Persistence API (JPA) model by Apache Olingo library.
 * 
 * @see ODataJPAServiceFactory
 */
@Slf4j
public class ContainerManagedODataJPAServiceFactory extends ODataJPAServiceFactory {
	
	/**
	 * The name of a file that redefines the OData service's metadata (EDM)
	 * automatically generated from JPA entities
	 * 
	 * @see <a href=
	 *      "https://olingo.apache.org/doc/odata2/tutorials/jparedefinemetadata.html">
	 *      Apache Olingo Documentation</a>
	 */
	protected static final String CUSTOM_EDM_MAPPING_FILENAME = "EdmMapping.xml";
	
	/**
	 * MUST be the same as the {@code name} attribute of the
	 * {@code persistence-unit} element declared in {@code persistence.xml} file
	 */
	protected static final String PERSISTENCE_UNIT_NAME = "MigrateHistoricalData";
	
	/**
	 * Container-Managed Entity Manager produced by {@link EntityManagerProducer}
	 */
	@Inject
	protected EntityManager entityManager;
	
	@Inject
	protected DelegatingODataJPAProcessor oDataJPAProcessorCMT;
	
	/**
	 * @return transactional ODataProcessor that ensures that every CUD DB
	 *         operations run in container-managed transaction (CMT)
	 * 
	 */
	@Override
	public ODataSingleProcessor createCustomODataProcessor(ODataJPAContext oDataJPAContext) {
		oDataJPAProcessorCMT.setODataJPAContext(oDataJPAContext);
		return oDataJPAProcessorCMT;
	}
	
	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException, ODataRuntimeException {
		try {
			ODataJPAContext oDataJPAContext = getODataJPAContext();

			oDataJPAContext.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);

			// enable container-managed transactions (CMT)
			oDataJPAContext.setEntityManager(entityManager);
			oDataJPAContext.setContainerManaged(true);
			
			// Optional: customize JPA-EDM mapping to fix automatically
			// generated names
			oDataJPAContext.setJPAEdmMappingModel(CUSTOM_EDM_MAPPING_FILENAME);
			
			// Optional: show detailed error messages, useful for debugging
			// TODO turn them off in production mode for security
			setDetailErrors(true);
			
			return oDataJPAContext;
		} catch (Exception e) {
			log.error("Exception during initializeODataJPAContext()", e);
			throw new ODataRuntimeException(e);
		}
	}
	
	/**
	 * Optional: useful for debugging
	 * <p>
	 * Registers {@link LoggingODataErrorCallback}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ODataCallback> T getCallback(Class<T> callbackInterface) {
		if (callbackInterface.isAssignableFrom(ODataErrorCallback.class)) {
			return (T) new LoggingODataErrorCallback(); 
		}
		
		return super.getCallback(callbackInterface);
	}

}