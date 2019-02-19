package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;

import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPADefaultProcessor;

/**
 * {@link ODataSingleProcessor} that ensures that all its methods run in a
 * transaction and simply delegates all its method calls to
 * {@link ODataJPADefaultProcessor}. This class MUST be listed under
 * {@code alternatives} element of the {@code beans.xml} file so that CDI
 * container knows which implementation should be injected in
 * {@link ODataJPAServiceFactoryCDI}.
 * <p>
 * {@link Transcational} class-level annotation ensures that all methods of the
 * CDI-managed bean run in a transaction. The annotation can also be placed on
 * particular methods. It is introduced in Contexts and Dependency Injection API
 * (CDI) v1.1 that is a part of Java EE 7 Web Profile API implemented by the SAP
 * Cloud Platform Neo Java EE 7 Web Profile TomEE runtime. Methods of CDI
 * managed beans can also be declaratively secured with CDI interceptor
 * annotations (e.g. Apache DeltaSpike Security).
 * 
 */
@Alternative
@RequestScoped
@Transactional
public class ODataJPAProcessorCDI extends DelegatingODataJPAProcessor {

}
