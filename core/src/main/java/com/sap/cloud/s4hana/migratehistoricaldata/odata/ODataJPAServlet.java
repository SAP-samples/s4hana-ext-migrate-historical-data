package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.core.servlet.ODataServlet;

/**
 * Extends default {@link ODataServlet} to enable CDI injection for
 * {@link ContainerManagedODataJPAServiceFactory} via @{@link Inject} annotation
 *
 */
@WebServlet("/OData.svc/*")
public class ODataJPAServlet extends ODataServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	ContainerManagedODataJPAServiceFactory oDataServiceFactory;

	@Override
	protected ODataServiceFactory getServiceFactory(HttpServletRequest request) {
		return oDataServiceFactory;
	}

}
