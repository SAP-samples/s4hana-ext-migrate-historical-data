package com.sap.cloud.s4hana.migratehistoricaldata.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;


/**
 * Optional: Custom JSON response serializer for CXF
 */
@Provider
public class JsonProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonProvider() {
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        objectMapper.setConfig(objectMapper.getSerializationConfig().with(MapperFeature.USE_STD_BEAN_NAMING));       
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
}
