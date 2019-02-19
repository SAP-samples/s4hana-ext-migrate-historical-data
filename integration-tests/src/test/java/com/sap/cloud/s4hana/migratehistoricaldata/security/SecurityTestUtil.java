package com.sap.cloud.s4hana.migratehistoricaldata.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;

public class SecurityTestUtil {
	
	private SecurityTestUtil() {
		// prevents util class from being instantiated
	}
	
	/**
	 * Asserts that the exception {@code e} is caused by a security violation.
	 * 
	 * @param e
	 *            Exception to check
	 * @return the root cause of {@code e} retrieved by calling
	 *         {@link Throwable#getCause()} recursively
	 * @throws AssertionError
	 *             if the exception {@code e} is not caused by a security
	 *             violation or its root cause has unexpected message.
	 */
	public static Throwable assertSecurityException(Throwable e) throws AssertionError {
		// get the root cause
		while (e.getCause() != null) {
			e = e.getCause();
		}

		assertThat("Exception thrown", e, allOf(
				instanceOf(SecurityException.class),
				hasProperty("message", startsWith("Current user is not authorized to"))));
		
		return e;
	}

}
