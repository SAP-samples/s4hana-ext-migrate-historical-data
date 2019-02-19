package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

public class ImportException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Convenience constructor method similar to
	 * {@link Throwable#Throwable(String, Throwable)} except the {@code message}
	 * is constructed by calling {@link String#format(String, Object...)} with
	 * {@code messageFormat} and {@code messageArgs} parameters
	 * 
	 * @param messageFormat
	 *            the message format which will be passed as the {@code format}
	 *            parameter to {@link String#format(String, Object...)}
	 * @param messageArgs
	 *            the message arguments which will be passed as the {@code args}
	 *            parameter to {@link String#format(String, Object...)}
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A null value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public ImportException(String messageFormat, Throwable cause, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs), cause);
	}

	/**
	 * Convenience constructor method similar to
	 * {@link Throwable#Throwable(String)} except the {@code message}
	 * is constructed by calling {@link String#format(String, Object...)} with
	 * {@code messageFormat} and {@code messageArgs} parameters
	 * 
	 * @param messageFormat
	 *            the message format which will be passed as the {@code format}
	 *            parameter to {@link String#format(String, Object...)}
	 * @param messageArgs
	 *            the message arguments which will be passed as the {@code args}
	 *            parameter to {@link String#format(String, Object...)}
	 */
	public ImportException(String messageFormat, Object... messageArgs) {
		super(String.format(messageFormat, messageArgs));
	}

}
