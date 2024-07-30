package com.sap.cloud.s4hana.migratehistoricaldata.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.EJBException;


/**
 * Optional: Custom {@link ExceptionMapper} for CXF to be used with exception
 * types for which no other specific mapper exists.
 * <p>
 * The mapper overrides {@link ODataExceptionMapperImpl} which in turn overrides
 * the default CXF mapper that always returns HTTP 500 Error code and the body
 * with the exception's message in HTML format.
 * <p>
 * Returns an error response with the default HTTP error code 500 and the JSON
 * body in {@link DetailedErrorResponse} format. ErrorResponse.code is the exception's
 * class name and ErrorResponse.message is the exception's message.
 */
@Slf4j
@Provider
public class DetailedExceptionMapper implements ExceptionMapper<Exception> {

    @Autowired
    JsonProvider jsonProvider;

    protected static DetailedErrorResponse toErrorResponse(Throwable exception) {
        // Optional: Unwrap EJB exceptions
        if (exception instanceof EJBException && exception.getCause() != null) {
            exception = exception.getCause();
        }

        final DetailedErrorResponse errorResponse = DetailedErrorResponse.of(
                /* code = */ exception.getClass().getSimpleName(),
                exception.getMessage());

        // Optional: fill inner errors recursively
        if (exception.getCause() != null) {
            errorResponse.setInnerError(toErrorResponse(exception.getCause()));
        }

        return errorResponse;
    }

    @Override
    public Response toResponse(Exception exception) {
        log.debug("toResponse() is called", exception);

        final DetailedErrorResponse errorResponse = toErrorResponse(exception);

        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(jsonProvider.toJson(errorResponse))
                .build();
    }

}
