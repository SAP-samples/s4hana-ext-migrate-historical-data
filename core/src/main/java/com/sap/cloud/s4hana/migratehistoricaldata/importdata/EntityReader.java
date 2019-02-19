package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.deltaspike.data.api.EntityRepository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Defines how entity of type T is imported from rows of a certain sheet.
 * <p>
 * Concrete usage examples can be found in
 * {@code com.sap.cloud.s4hana.migratehistoricaldata.importdata.listeners}
 * package.
 *
 * @param <T>
 *            the type of entity being read. Can be JPA Entity or data transfer
 *            object which is then converted to a JPA Entity
 */
@RequiredArgsConstructor(staticName = "of")
public class EntityReader<T> {

	@Getter
	private String sheetName;
	
	@Getter
	private final Supplier<T> entityConstructor;
	
	@Getter
	private Consumer<T> entitySaver;
	
	@Getter
	private int indexOfFirstRowToParse = 0;
	
	@Getter
	private boolean skipRestCells = false;
	
	@Getter @Setter(AccessLevel.PROTECTED)
	private List<BiConsumer<T, Object>> setters = Collections.emptyList();
	
	/**
	 * Specify the name of a spreadsheet from which the entity will be read
	 * 
	 * @param sheetName
	 * @return {@code this}
	 */
	public EntityReader<T> fromSheet(@NonNull String sheetName) {
		this.sheetName = sheetName;
		return this;
	}
	
	/**
	 * Allows to skip a number of first rows in the sheet (e.g. header rows) and
	 * start parsing from the row with index {@code indexOfFirstRowToParse}
	 * 
	 * @param indexOfFirstRowToParse
	 *            index of the first row to parse starting from {@code 1}
	 * @return {@code this}
	 */
	public EntityReader<T> startFromRow(int indexOfFirstRowToParse) {
		this.indexOfFirstRowToParse = indexOfFirstRowToParse;
		return this;
	}
	
	public EntityReader<T>.SettersBuilder first(@NonNull BiConsumer<T, Object> setter) {
		return this.new SettersBuilder(this, setter);
	}
	
	/**
	 * Sets only the very first cell
	 * 
	 * @return {@code this}
	 */
	public EntityReader<T> only(@NonNull BiConsumer<T, Object> setter) {
		return first(setter).buildAndGetOwner();
	}
	
	/**
	 * Specifies a method that will be used to save the constructed entity, e.g.
	 * {@link EntityRepository#save(Object)}
	 * 
	 * @return {@code this}
	 */
	public EntityReader<T> saveWith(@NonNull Consumer<T> entitySaver) {
		this.entitySaver = entitySaver;
		return this;
	}
	
	/* helper fluent API methods that convert values retrieved from XML import file to JPA value types */
	
	/**
	 * @param <T> the type of the entity being parsed
	 * @return no op dummy setter that skips current cell.
	 *         <p>
	 *         The returned setter can be passed to the following methods:
	 *         <ul>
	 *         <li>{@link EntityReader#first(BiConsumer)}</li>
	 *         <li>{@link EntityReader#only(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer, int)}</li>
	 *         </ul>
	 */
	public static <T> BiConsumer<T, Object> skipCell() {
		return (entity, value) -> { /* nothing to do here */ };
	}
	
	/**
	 * @param <T>
	 *            the type of the entity being parsed
	 * @param bigDecimalSetter
	 *            the setter that will be used to set the converted value to the
	 *            entity being parsed.
	 * @param scale
	 *            the scale of the {@link BigDecimal} value to which the cell
	 *            will be converted. See {@link BigDecimal#scale()} for details.
	 * @return setter that converts current cell's value to {@link BigDecimal}
	 *         type using scale specified in {@code scale} parameter and
	 *         {@link RoundingMode#HALF_UP} rounding mode and then sets it to
	 *         the entity being parsed using its setter specified in
	 *         {@code bigDecimalSetter} parameter.
	 *         <p>
	 *         The returned setter can be passed to the following methods:
	 *         <ul>
	 *         <li>{@link EntityReader#first(BiConsumer)}</li>
	 *         <li>{@link EntityReader#only(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer, int)}</li>
	 *         </ul>
	 * 
	 * @see BigDecimal#scale()
	 * @see BigDecimal#setScale(int, RoundingMode)
	 * @see RoundingMode#HALF_UP
	 */
	public static <T> BiConsumer<T, Object> setBigDecimal(BiConsumer<T, BigDecimal> bigDecimalSetter, int scale) {		
		return (entity, value) -> {
			// Xelem returns value of type Double
			final Double doubleValue = (Double) value;
			
			// Convert it to BigDecimal value using scale
			final BigDecimal bigDecimalValue = BigDecimal.valueOf(doubleValue)
					.setScale(scale, RoundingMode.HALF_UP);
			
			bigDecimalSetter.accept(entity, bigDecimalValue);
		};
	}
	
	/**
	 * @param <T>
	 *            the type of the entity being parsed
	 * @param longSetter
	 *            the setter that will be used to set the converted value to the
	 *            entity being parsed.
	 * @return setter that converts current cell's value to {@link Long} type
	 *         and sets it to the entity being parsed using its setter specified
	 *         in {@code longSetter} parameter.
	 *         <p>
	 *         The returned setter can be passed to the following methods:
	 *         <ul>
	 *         <li>{@link EntityReader#first(BiConsumer)}</li>
	 *         <li>{@link EntityReader#only(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer, int)}</li>
	 *         </ul>
	 * 
	 */
	public static <T> BiConsumer<T, Object> setLong(BiConsumer<T, Long> longSetter) {		
		return (entity, value) -> {
			// Xelem returns all numbers as Double
			final Double doubleValue = (Double) value;
			
			// Convert double to Long
			final Long longValue = Long.valueOf(Math.round(doubleValue));
			
			longSetter.accept(entity, longValue);
		};
	}
	
	/**
	 * @param <T>
	 *            the type of the entity being parsed
	 * @param stringSetter
	 *            the setter that will be used to set the converted value to the
	 *            entity being parsed.
	 * @return setter that converts current cell's value to {@link String} type
	 *         and sets it to the entity being parsed using its setter specified
	 *         in {@code stringSetter} parameter.
	 *         <p>
	 *         The returned setter can be passed to the following methods:
	 *         <ul>
	 *         <li>{@link EntityReader#first(BiConsumer)}</li>
	 *         <li>{@link EntityReader#only(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer, int)}</li>
	 *         </ul>
	 * 
	 */
	public static <T> BiConsumer<T, Object> setString(BiConsumer<T, String> stringSetter) {
		return (entity, value) -> stringSetter.accept(entity, (String) value);
	}
	
	/**
	 * @param <T>
	 *            the type of the entity being parsed
	 * @param calendarSetter
	 *            the setter that will be used to set the converted value to the
	 *            entity being parsed.
	 * @return setter that converts current cell's value to {@link Calendar}
	 *         type using the default time zone and locale and then sets it to
	 *         the entity being parsed using its setter specified in
	 *         {@code calendarSetter} parameter.
	 *         <p>
	 *         The returned setter can be passed to the following methods:
	 *         <ul>
	 *         <li>{@link EntityReader#first(BiConsumer)}</li>
	 *         <li>{@link EntityReader#only(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer)}</li>
	 *         <li>{@link EntityReader.SettersBuilder#then(BiConsumer, int)}</li>
	 *         </ul>
	 * 
	 */
	public static <T> BiConsumer<T, Object> setCalendar(BiConsumer<T, Calendar> calendarSetter) {
		return (entity, value) -> {
			// Xelem returns legacy Date value
			final Date dateValue = (Date) value;
			
			// Simply transform it into the Calendar using the default time zone and locale
			// If you need more sophisticated transformation, you can do it here
			final Calendar calendarValue = Calendar.getInstance();
			calendarValue.setTime(dateValue);
			
			calendarSetter.accept(entity, calendarValue);
		};
	}
	
	/**
	 * Dummy helper method that enables Fluent API to increase readability. Compare:
	 * <p>
	 * Without:<br> 
	 * 	{@code reader.first(skipCell(), 3);}
	 * <p>
	 * With:<br>
	 * 	{@code reader.first(skipCell(), times(3));}<br>
	 * 
	 * @param n
	 *            value to be returned
	 * @return {@code n}
	 */
	public static int times(int n) {
		return n;
	}
	
	/**
	 * Builder that enables fluent API to define how cells are parsed in a row.
	 * Call  
	 *
	 */
	public class SettersBuilder {
		
		private final EntityReader<T> owner;
		
		private List<BiConsumer<T, Object>> setters = new ArrayList<>();

		protected SettersBuilder(EntityReader<T> owner, BiConsumer<T, Object> firstSetter) {
			this.owner = owner;
			setters.add(firstSetter);
		}
		
		public EntityReader<T>.SettersBuilder then(BiConsumer<T, Object> setter) {
			setters.add(setter);
			return this;
		}
		
		public EntityReader<T>.SettersBuilder then(BiConsumer<T, Object> setter, int n) {
			for (int i = 0; i < n; i++) {
				setters.add(setter);
			};
			
			return this;
		}
		
		public EntityReader<T> andFinally(BiConsumer<T, Object> setter) {
			setters.add(setter);
			return buildAndGetOwner();
		}
		
		public EntityReader<T> andSkipRestCells() {
			owner.skipRestCells = true;
			return buildAndGetOwner();
		}
		
		protected EntityReader<T> buildAndGetOwner() {
			owner.setSetters(Collections.unmodifiableList(setters));
			return owner;
		}
		
	}
	
	/**
	 * @return {@code true} if the row with {@code rowIndex} should be parsed
	 *         and {@code false} if the row should be skipped without parsing
	 *         (e.g. in case of header rows)
	 */
	public boolean shouldParse(int rowIndex) {
		return rowIndex >= indexOfFirstRowToParse;
	}
	
	public EntityReaderListener<T> listener() {
		return EntityReaderListener.with(this);
	}
	
}
