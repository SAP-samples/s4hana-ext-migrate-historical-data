package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Optional: useful for debugging
 * <p>
 * Simply logs the exception thrown in OData service and produces the default
 * error response.
 * <p>
 * Must be registered in {@link ContainerManagedODataJPAServiceFactory#getCallback(Class)}
 */ 
@Slf4j
public class LoggingODataErrorCallback implements ODataErrorCallback {

	/**
	 * Called when an exception is catched in the OData service.
	 * <p>
	 * Simply logs the exception thrown and produces the default error response.
	 * {@link ContainerManagedODataJPAServiceFactory#setDetailErrors(boolean)} can define how
	 * detailed the error description will be.
	 * <p>
	 * You can also customize the error message by calling:
	 * <ul>
	 * <li>{@link ODataErrorContext#setInnerError(String)}</li>
	 * <li>{@link ODataErrorContext#setMessage(String)}</li>
	 * <li>{@link ODataErrorContext#setErrorCode(String)}</li>
	 * <ul>
	 * <p>
	 */
	@Override
	public ODataResponse handleError(ODataErrorContext context) throws ODataApplicationException {
		Throwable exception = context.getException();
		log.error("Handle error", exception);
		
		return EntityProvider.writeErrorDocument(context);
	}

}
