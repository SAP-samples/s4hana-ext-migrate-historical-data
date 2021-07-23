package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import javax.ejb.TransactionAttribute;
import javax.interceptor.AroundInvoke;

import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPADefaultProcessor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

/**
 * {@link ODataSingleProcessor} that simply delegates all its method calls to
 * {@link ODataJPADefaultProcessor}. Platform-specific versions of the
 * application MUST implement the subclasses and annotate them with the
 * platform-specific annotations that ensure that the methods run in a
 * transaction. These subclasses should also be listed under
 * {@code alternatives} element of the {@code beans.xml} file so that CDI
 * container knows which implementation should be injected in
 * {@link ContainerManagedODataJPAServiceFactory}.
 * <p>
 * <b>Neo application</b> implements this class as a CDI-managed bean annotated
 * with {@code @javax.inject.Transcational} annotation. It ensures that all
 * methods of the annotated bean run in a transaction. The annotation can also
 * be placed on particular methods. It is introduced in Contexts and Dependency
 * Injection API (CDI) v1.1 that is a part of Java EE 7 Web Profile API
 * implemented by the SAP Business Technology Platform (BTP) Java EE 7 Web Profile TomEE
 * runtime. Methods of CDI managed beans can also be declaratively secured with
 * CDI interceptor annotations (e.g. Apache DeltaSpike Security).
 * <p>
 * <b>Cloud Foundry application</b> runs on TomEE runtime which implements Java
 * EE 6 API. It contains CDI v1.0 which doesn't support transactional methods
 * for CDI-managed beans. This is why on Cloud Foundry this class is implemented
 * as a stateless session Enterprise Java Bean (EJB) which methods run in a
 * container-managed transaction by default. EJB also supports
 * {@link TransactionAttribute} annotation that works in a similar way to
 * {@code javax.inject.Transactional} annotation from CDI and allows to override
 * the transactional behavior for an entire class or its distinct methods.
 * Unfortunately, CDI interceptors don't work for EJB session beans, however
 * declarative security can still be achieved via EJB {@link AroundInvoke}
 * interceptors.
 * 
 */
public class DelegatingODataJPAProcessor extends ODataSingleProcessor {
	
	@Getter @Setter @Delegate
	private ODataSingleProcessor delegate;
	
	public void setODataJPAContext(ODataJPAContext oDataJPAContext) {
		delegate = new ODataJPADefaultProcessor(oDataJPAContext) {};
	}

}
