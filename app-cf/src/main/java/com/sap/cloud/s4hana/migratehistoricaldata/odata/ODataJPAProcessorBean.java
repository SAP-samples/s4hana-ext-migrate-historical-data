package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.AroundInvoke;

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
 * This class is implemented as a stateless session Enterprise Java Bean (EJB)
 * which methods run in a container-managed transaction by default. EJB also
 * supports {@link TransactionAttribute} annotation that works in a similar way
 * to {@code javax.inject.Transactional} annotation from CDI and allows to
 * override the transactional behavior for an entire class or its distinct
 * methods. Unfortunately, CDI interceptors don't work for EJB session beans,
 * however declarative security can still be achieved via EJB
 * {@link AroundInvoke} interceptors.
 * 
 */
@Alternative
@RequestScoped
@Stateful // All session EJBs are transactional by default
public class ODataJPAProcessorBean extends DelegatingODataJPAProcessor {

}
