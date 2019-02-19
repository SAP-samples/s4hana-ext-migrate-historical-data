package com.sap.cloud.s4hana.migratehistoricaldata.odata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataProcessor;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetComplexPropertyUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityLinkCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityLinkUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetLinksCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetLinksUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetFunctionImportUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetMediaResourceUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetMetadataUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetServiceDocumentUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetSimplePropertyUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPADefaultProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ODataJPAProcessorCMTTest {
	
	@Mock
	private ODataJPAContext odataJPAContextMock;
	
	@Test
	public void testSetODataJPAContext() {
		// Given a testee object
		final DelegatingODataJPAProcessor testee = new DelegatingODataJPAProcessor() {};
		
		// When setODataJPAContext() is called 
		testee.setODataJPAContext(odataJPAContextMock);
		
		// Then the delegate is of ODataJPADefaultProcessor class
		assertThat("delegate",
				testee.getDelegate(), 
				instanceOf(ODataJPADefaultProcessor.class));
		
		// Then the delegate is of ODataJPADefaultProcessor class
		assertThat("delegate's context should be the same "
				+ "as the one passed to setODataJPAContext()", 
				((ODataJPADefaultProcessor) testee.getDelegate()).getOdataJPAContext(), 
				is(odataJPAContextMock));
	}
	
	@Mock
	ODataSingleProcessor delegate;
	
	DelegatingODataJPAProcessor testee;
	
	@Before
	public void setUp() {
		testee = new DelegatingODataJPAProcessor() {};
		testee.setDelegate(delegate);
	}
	
	@Test
	public void testGetContext() {
		testee.getContext();
		verify(delegate).getContext();
	}
	
	@Mock
	BatchHandler batchHandlerMock;
	
	@Mock
	InputStream inputStreamMock;
	
	final String contentType = "application/html";

	@Test
	public void testExecuteBatch() throws ODataException {		
		testee.executeBatch(batchHandlerMock, contentType, inputStreamMock);
		
		verify(delegate).executeBatch(batchHandlerMock, contentType, inputStreamMock);
	}

	@Mock
	List<ODataRequest> listODataRequestMock;
	
	@Test
	public void testExecuteChangeSet() throws ODataException {
		testee.executeChangeSet(batchHandlerMock, listODataRequestMock);
		
		verify(delegate).executeChangeSet(batchHandlerMock, listODataRequestMock);
	}
	
	@Mock
	GetFunctionImportUriInfo getFunctionImportUriInfoMock;

	@Test
	public void testExecuteFunctionImport() throws ODataException {
		testee.executeFunctionImport(getFunctionImportUriInfoMock, contentType);
		
		verify(delegate).executeFunctionImport(getFunctionImportUriInfoMock, contentType);
	}

	@Test
	public void testExecuteFunctionImportValue() throws ODataException {
		testee.executeFunctionImportValue(getFunctionImportUriInfoMock, contentType);
		
		verify(delegate).executeFunctionImportValue(getFunctionImportUriInfoMock, contentType);
	}
	
	@Mock
	DeleteUriInfo deleteUriInfoMock;

	@Test
	public void testDeleteEntitySimplePropertyValue() throws ODataException {
		testee.deleteEntitySimplePropertyValue(deleteUriInfoMock, contentType);
		
		verify(delegate).deleteEntitySimplePropertyValue(deleteUriInfoMock, contentType);
	}

	@Test
	public void testDeleteEntityMedia() throws ODataException {
		testee.deleteEntityMedia(deleteUriInfoMock, contentType);
		
		verify(delegate).deleteEntityMedia(deleteUriInfoMock, contentType);
	}
	
	@Mock
	GetEntitySetLinksCountUriInfo getEntitySetLinksCountUriInfoMock;

	@Test
	public void countEntityLinks()throws ODataException {
		testee.countEntityLinks(getEntitySetLinksCountUriInfoMock, contentType);
		
		verify(delegate).countEntityLinks(getEntitySetLinksCountUriInfoMock, contentType);
	}
	
	@Mock
	PostUriInfo postUriInfoMock;

	@Test
	public void testCreateEntityLink() throws ODataException {
		testee.createEntityLink(postUriInfoMock, inputStreamMock, contentType, contentType);
		
		verify(delegate).createEntityLink(postUriInfoMock, inputStreamMock, contentType, contentType);
	}
	
	@Mock
	GetEntityLinkCountUriInfo mockGetEntityLinkCountUriInfo;

	@Test
	public void existsEntityLink() throws ODataException {
		testee.existsEntityLink(mockGetEntityLinkCountUriInfo, contentType);
		verify(delegate).existsEntityLink(mockGetEntityLinkCountUriInfo, contentType);
	}

	@Test
	public void testDeleteEntityLink() throws ODataException {
		testee.deleteEntityLink(deleteUriInfoMock, contentType);
		verify(delegate).deleteEntityLink(deleteUriInfoMock, contentType);
	}

	@Test
	public void countEntitySet() throws ODataException {
		final GetEntitySetCountUriInfo uriInfo = mock(GetEntitySetCountUriInfo.class);
		testee.countEntitySet(uriInfo, contentType);
		verify(delegate).countEntitySet(uriInfo, contentType);
	}

	@Test
	public void testCreateEntity() throws ODataException {
		PostUriInfo uriInfo = mock(PostUriInfo.class);
		testee.createEntity(uriInfo, inputStreamMock, contentType, contentType);
		verify(delegate).createEntity(uriInfo, inputStreamMock, contentType, contentType);
	}

	@Test
	public void testDeleteEntity() throws ODataException {
		testee.deleteEntity(deleteUriInfoMock, contentType);
		verify(delegate).deleteEntity(deleteUriInfoMock, contentType);
	}
	
	@Test
	public void existsEntity() throws ODataException {
		GetEntityCountUriInfo uriInfo = mock(GetEntityCountUriInfo.class);
		testee.existsEntity(uriInfo, contentType);
		verify(delegate).existsEntity(uriInfo, contentType);
	}

	@Test
	public void getCustomContentTypes() throws ODataException {
		testee.getCustomContentTypes(ODataProcessor.class);
		verify(delegate).getCustomContentTypes(ODataProcessor.class);
	}

	@Test
	public void testSetContext() {
		ODataContext context = mock(ODataContext.class);
		testee.setContext(context);
		verify(delegate).setContext(context);
	}
	
	@Mock
	GetSimplePropertyUriInfo getSimplePropertyUriInfoMock;

	@Test
	public void testReadEntitySimplePropertyValue() throws ODataException {
		testee.readEntitySimplePropertyValue(getSimplePropertyUriInfoMock, contentType);
		verify(delegate).readEntitySimplePropertyValue(getSimplePropertyUriInfoMock, contentType);
	}

	@Test
	public void testUpdateEntitySimplePropertyValue() throws ODataException {
		PutMergePatchUriInfo uriInfo = mock(PutMergePatchUriInfo.class);
		testee.updateEntitySimplePropertyValue(uriInfo, inputStreamMock, contentType, contentType);
		verify(delegate).updateEntitySimplePropertyValue(uriInfo, inputStreamMock, contentType, contentType);
	}

	@Test
	public void testReadEntitySimpleProperty() throws ODataException {
		testee.readEntitySimpleProperty(getSimplePropertyUriInfoMock, contentType);
		verify(delegate).readEntitySimpleProperty(getSimplePropertyUriInfoMock, contentType);
	}

	@Test
	public void testUpdateEntitySimpleProperty() throws ODataException {
		PutMergePatchUriInfo uriInfo = mock(PutMergePatchUriInfo.class);
		testee.updateEntitySimpleProperty(uriInfo, inputStreamMock, contentType, contentType);
		verify(delegate).updateEntitySimpleProperty(uriInfo, inputStreamMock, contentType, contentType);
	}

	@Test
	public void testReadEntityMedia() throws ODataException {
		GetMediaResourceUriInfo uriInfo = mock(GetMediaResourceUriInfo.class);
		testee.readEntityMedia(uriInfo, contentType);
		verify(delegate).readEntityMedia(uriInfo, contentType);
	}

	@Test
	public void testUpdateEntityMedia() throws ODataException {
		PutMergePatchUriInfo uriInfo = mock(PutMergePatchUriInfo.class);
		testee.updateEntityMedia(uriInfo, inputStreamMock, contentType, contentType);
		verify(delegate).updateEntityMedia(uriInfo, inputStreamMock, contentType, contentType);
	}

	@Test
	public void testReadEntityLinks() throws ODataException {
		GetEntitySetLinksUriInfo uriInfo = mock(GetEntitySetLinksUriInfo.class);
		testee.readEntityLinks(uriInfo, contentType);
		verify(delegate).readEntityLinks(uriInfo, contentType);
	}

	@Test
	public void testReadEntityLink() throws ODataException {
		GetEntityLinkUriInfo uriInfo = mock(GetEntityLinkUriInfo.class);
		testee.readEntityLink(uriInfo, contentType);
		verify(delegate).readEntityLink(uriInfo, contentType);
	}

	@Test
	public void testUpdateEntityLink() throws ODataException {
		PutMergePatchUriInfo uriInfo = mock(PutMergePatchUriInfo.class);
		testee.updateEntityLink(uriInfo, inputStreamMock, contentType, contentType);
		verify(delegate).updateEntityLink(uriInfo, inputStreamMock, contentType, contentType);
	}

	@Test
	public void testReadEntityComplexProperty() throws ODataException {
		GetComplexPropertyUriInfo uriInfo = mock(GetComplexPropertyUriInfo.class);
		testee.readEntityComplexProperty(uriInfo, contentType);
		verify(delegate).readEntityComplexProperty(uriInfo, contentType);
	}

	@Test
	public void testUpdateEntityComplexProperty() throws ODataException {
		boolean merge = false;
		PutMergePatchUriInfo uriInfo = mock(PutMergePatchUriInfo.class);
		testee.updateEntityComplexProperty(uriInfo, inputStreamMock, contentType, merge, contentType);
		verify(delegate).updateEntityComplexProperty(uriInfo, inputStreamMock, contentType, merge, contentType);
	}

	@Test
	public void testReadEntitySet() throws ODataException {
		GetEntitySetUriInfo uriInfo = mock(GetEntitySetUriInfo.class);
		testee.readEntitySet(uriInfo, contentType);
		verify(delegate).readEntitySet(uriInfo, contentType);
	}

	@Test
	public void testReadEntity() throws ODataException {
		GetEntityUriInfo uriInfo = mock(GetEntityUriInfo.class);
		testee.readEntity(uriInfo, contentType);
		verify(delegate).readEntity(uriInfo, contentType);
	}

	@Test
	public void testUpdateEntity() throws ODataException {
		PutMergePatchUriInfo uriInfo = mock(PutMergePatchUriInfo.class);
		boolean merge = true;
		testee.updateEntity(uriInfo, inputStreamMock, contentType, merge, contentType);
		verify(delegate).updateEntity(uriInfo, inputStreamMock, contentType, merge, contentType);
	}

	@Test
	public void testReadServiceDocument() throws ODataException {
		GetServiceDocumentUriInfo uriInfo = mock(GetServiceDocumentUriInfo.class);
		testee.readServiceDocument(uriInfo, contentType);
		verify(delegate).readServiceDocument(uriInfo, contentType);
	}

	@Test
	public void testReadMetadata() throws ODataException {
		GetMetadataUriInfo uriInfo = mock(GetMetadataUriInfo.class);
		testee.readMetadata(uriInfo, contentType);
		verify(delegate).readMetadata(uriInfo, contentType);
	}

}
