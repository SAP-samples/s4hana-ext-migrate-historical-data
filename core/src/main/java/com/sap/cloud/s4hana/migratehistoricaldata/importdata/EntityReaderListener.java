package com.sap.cloud.s4hana.migratehistoricaldata.importdata;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import nl.fountain.xelem.excel.Cell;
import nl.fountain.xelem.excel.Row;
import nl.fountain.xelem.excel.Worksheet;
import nl.fountain.xelem.lex.DefaultExcelReaderListener;
import nl.fountain.xelem.lex.ExcelReader;

/**
 * See <a href=
 * "http://xelem.sourceforge.net/javadoc/nl/fountain/xelem/lex/ExcelReaderListener.html">
 * ExcelReaderListenerjavadoc</a>
 * <p>
 * Constructs entities on the fly while Xelem library reads the import file
 * after {@link ExcelReader#read(String)} or
 * {@link ExcelReader#read(org.xml.sax.InputSource)} is called
 * <p>
 * Methods are called in the following order:
 * <ol>
 * <li>{@link EntityReaderListener#startWorksheet(int, Worksheet)}</li>
 * <li>{@link EntityReaderListener#setCell(int, String, int, Cell)}</li>
 * <li>{@link EntityReaderListener#setRow(int, String, Row)}</li>
 * </ol>
 * 
 * @param <T>
 *            the type of entity to be parsed
 */
@Slf4j
@RequiredArgsConstructor(staticName = "with")
@Accessors(fluent = true)
public class EntityReaderListener<T> extends DefaultExcelReaderListener {
	
	final @NonNull EntityReader<T> entityReader;
	
	private T entity;
	
	@Getter @Setter
	private Consumer<RuntimeException> exceptionHandler = loggingExceptionHandler();

	/**
	 * @return exception handler that logs an exception using the
	 *         {@link EntityReaderListener} logger and rethrows it.
	 *         <p>
	 *         It is set by default to
	 *         {@link EntityReaderListener#exceptionHandler()}
	 */
	public static Consumer<RuntimeException> loggingExceptionHandler() {
		return e -> {
			// default exception handler logs an exception and rethrows it
			log.warn(e.getMessage(), e);
			throw e;
		};
	}
	
	/**
	 * See <a href=
	 * "http://xelem.sourceforge.net/javadoc/nl/fountain/xelem/lex/ExcelReaderListener.html#startWorksheet(int,%20nl.fountain.xelem.excel.Worksheet)">
	 * Xelem javadoc</a>
	 */
	@Override
	public void startWorksheet(int sheetIndex, Worksheet sheet) {
		if (isCorrectSheet(sheet)) {
			entity = createNewEntity();
		}
	}

	/**
	 * See <a href=
	 * "http://xelem.sourceforge.net/javadoc/nl/fountain/xelem/lex/ExcelReaderListener.html#setCell(int,%20java.lang.String,%20int,%20nl.fountain.xelem.excel.Cell)">
	 * Xelem javadoc</a>
	 */
	@Override
	public void setCell(int sheetIndex, String sheetName, int rowIndex, Cell cell) throws ImportException {
		if (isCorrectSheet(sheetName) && entityReader.shouldParse(rowIndex)) {
			final int colIndex = cell.getIndex();
			
			if (colIndex > entityReader.getSetters().size()) {
				if (!entityReader.isSkipRestCells()) {
					throw new ImportException("Extra cell at pos %d in row %d of sheet #%d %s", 
							colIndex, rowIndex, sheetIndex, sheetName);
				}
				
				return;
			}
			
			final BiConsumer<T, Object> setter = entityReader.getSetters().get(colIndex - 1);
			final Object value = cell.getData();
			try {
				setter.accept(entity, value);
			} catch (ClassCastException e) {
				throw new ImportException("Value of type %s that is not supported by the respective setter "
						+ "in the cell at pos %d in row %d of sheet #%d %s", 
						/* cause = */ e,
						cell.getClass(), colIndex, rowIndex, sheetIndex, sheetName);
			}
		}
	}

	/**
	 * See <a href=
	 * "http://xelem.sourceforge.net/javadoc/nl/fountain/xelem/lex/ExcelReaderListener.html#setRow(int,%20java.lang.String,%20nl.fountain.xelem.excel.Row)">
	 * Xelem javadoc</a>
	 */
	@Override
	public void setRow(int sheetIndex, String sheetName, Row row) {
		if (isCorrectSheet(sheetName) && entityReader.shouldParse(row.getIndex())) {
			try {
				entityReader.getEntitySaver().accept(entity);
			} catch (RuntimeException e) {
				throw new ImportException("Exception when trying to persist entity from row %d of sheet #%d %s", 
						/* cause = */ e, 
						row.getIndex(), sheetIndex, sheetName);				
			}
			
			entity = createNewEntity();
		}
	}

	protected boolean isCorrectSheet(@NonNull Worksheet sheet) {
		return isCorrectSheet(sheet.getName());
	}

	protected boolean isCorrectSheet(@NonNull String sheetName) {
		return Objects.equals(sheetName, entityReader.getSheetName());
	}

	protected T createNewEntity() {
		return entityReader.getEntityConstructor().get();
	}

}
