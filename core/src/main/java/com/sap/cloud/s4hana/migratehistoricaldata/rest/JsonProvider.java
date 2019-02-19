package com.sap.cloud.s4hana.migratehistoricaldata.rest;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Optional: Custom JSON response serializer for CXF
 *
 */
public class JsonProvider extends JacksonJsonProvider {

	@Override
	public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
		final ObjectMapper om = super.locateMapper(type, mediaType);

		// Wrap entire ErrorResponse in root "error" JSON object
		om.enable(SerializationFeature.WRAP_ROOT_VALUE);

		// needed for testing to unwrap ErrorResponse
		om.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);

		// recommended: resolve conflict between Jackson naming and Java Bean
		// naming for properties which names start with two or more upper-case
		// letters
		// see https://github.com/FasterXML/jackson-databind/issues/1824
		om.enable(MapperFeature.USE_STD_BEAN_NAMING);

		// optional: include only properties with non-null values
		om.setSerializationInclusion(Include.NON_NULL);

		// optional: pretty print
		om.enable(SerializationFeature.INDENT_OUTPUT);

		return om;
	}
	
	public String toJson(Object value) {
		try {
			return locateMapper(value.getClass(), MediaType.APPLICATION_JSON_TYPE)
					.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			// should not happen
			throw new RuntimeException("Error during the convertation of an object " + value + " to JSON", e);
		}
	}
	
}
