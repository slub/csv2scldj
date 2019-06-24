/**
 * Copyright © 2017 SLUB Dresden (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.slubdresden.csv2scldj;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.slubdresden.csv2scldj.model.Field;

/**
 * @author tgaengler
 */
public final class CSV2SCLDJConverter {

	private static final Logger LOG = LoggerFactory.getLogger(CSV2SCLDJConverter.class);

	private static final ObjectMapper MAPPER;

	private static final JsonFactory FACTORY;

	private static final char DELIMITER = ',';
	private static final char QUOTE_CHAR = '"';
	private static final char ESCAPE_CHARACTER = '\\';
	private static final CSVFormat CSV_FORMAT = CSVFormat.newFormat(DELIMITER)
			.withQuote(QUOTE_CHAR)
			.withEscape(ESCAPE_CHARACTER)
			.withIgnoreSurroundingSpaces();

	static {

		final ObjectMapper objectMapper = new ObjectMapper();
		MAPPER = objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		// enable this, if it will be required again
		//.setSerializationInclusion(Include.NON_EMPTY);

		FACTORY = MAPPER.getFactory();
	}


	public static void convert(final Reader reader,
	                           final Map<String, Field> schema,
	                           final BufferedWriter writer,
	                           final String cellValueDelimiter) throws IOException, CSV2SCLDJException {

		final AtomicInteger inputRecordCounter = new AtomicInteger(0);
		final AtomicInteger outputRecordCounter = new AtomicInteger(0);

		final CSVParser csvInputParser = CSVParser.parse(reader, CSV_FORMAT);

		final Iterator<CSVRecord> iterator = csvInputParser.iterator();

		final CSVRecord header = iterator.next();

		final Map<String, Integer> csvInputHeaderMap = initializeHeader(header);

		checkHeader(csvInputHeaderMap, schema);

		final Map<String, Field> fieldMap = generateFieldMap(csvInputHeaderMap.keySet(), schema);
		final Optional<Set<String>> requiredFields = getRequiredFields(schema);

		while (iterator.hasNext()) {

			inputRecordCounter.incrementAndGet();

			final CSVRecord csvInputRecord = iterator.next();

			writeRecord(csvInputRecord, csvInputHeaderMap, fieldMap, requiredFields, cellValueDelimiter, writer, outputRecordCounter);

			writer.newLine();
		}

		reader.close();
		writer.flush();
		writer.close();

		LOG.info("read '{}' records", inputRecordCounter.get());
		LOG.info("wrote '{}' records", outputRecordCounter.get());

		final StringBuilder sb = new StringBuilder();

		sb.append("tried to write following fields (i.e. there is no guarantee that they occur in all target records):").append("\n");

		for (final String field : fieldMap.keySet()) {

			sb.append("\t\t")
					.append(field)
					.append("\n");
		}

		LOG.info(sb.toString());
	}

	private static void writeRecord(final CSVRecord csvInputRecord,
	                                final Map<String, Integer> csvInputHeaderMap,
	                                final Map<String, Field> fieldMap,
	                                final Optional<Set<String>> optionalRequiredFields,
	                                final String cellValueDelimiter,
	                                final Writer writer,
	                                final AtomicInteger outputRecordCounter) throws IOException {

		final JsonGenerator jg = FACTORY.createGenerator(writer);

		jg.writeStartObject();

		final java.util.Set<String> writtenFields = new HashSet<>();

		csvInputHeaderMap.forEach((headerName, columnNumber) -> {

			final boolean isMultivalued = fieldMap.get(headerName).get().isMultivalued();

			final String inputValue = csvInputRecord.get(columnNumber);

			try {

				final boolean writeResult;

				if (isMultivalued) {

					writeResult = writeValues(headerName, inputValue, cellValueDelimiter, jg);
				} else {

					writeResult = writeValue(headerName, inputValue, cellValueDelimiter, jg);
				}

				if(writeResult) {

					writtenFields.add(headerName);
				}
			} catch (final IOException | CSV2SCLDJException e) {

				throw CSV2LDJError.wrap(new CSV2SCLDJException(String.format("something went wrong at writing record '%s' in CSV to schema conform line-delimited JSON converting", csvInputRecord.toString()), e));
			}
		});

		// check, whether all required fields are set
		optionalRequiredFields
				.filter(requiredFields -> !writtenFields.isEmpty())
				.flatMap(requiredFields1 -> checkRequiredFields(requiredFields1, writtenFields))
				.ifPresent(notWrittenRequiredFields -> {

					throw CSV2LDJError.wrap(new CSV2SCLDJException(String.format("the fields '%s' are marked as required in the schema definition, but are not contained in the record '%s', please fill em up properly before converting it.", notWrittenRequiredFields.mkString(), csvInputRecord.toString())));
				});

		jg.writeEndObject();
		jg.flush();

		outputRecordCounter.incrementAndGet();
	}

	/**
	 * returns true, if values has been written
	 *
	 * @param headerName         the field name
	 * @param inputCell          the input value
	 * @param cellValueDelimiter the cell value delimiter
	 * @param jg                 the JSON generator
	 * @return true, if values has been written
	 * @throws IOException
	 */
	private static boolean writeValues(final String headerName,
	                                   final String inputCell,
	                                   final String cellValueDelimiter,
	                                   final JsonGenerator jg) throws IOException {

		if (valueNotExists(inputCell)) {

			// write only valid values

			return false;
		}

		final String[] inputValues = inputCell.split(cellValueDelimiter);

		jg.writeArrayFieldStart(headerName);

		for (final String inputValue : inputValues) {

			if (valueNotExists(inputValue)) {

				// write only valid values

				continue;
			}

			jg.writeString(inputValue);
		}

		jg.writeEndArray();

		return true;
	}

	/**
	 * returns true, if value has been written
	 *
	 * @param headerName         the field name
	 * @param inputValue         the input value
	 * @param cellValueDelimiter the cell value delimiter
	 * @param jg                 the JSON generator
	 * @return true, if value has been written
	 * @throws IOException
	 * @throws CSV2SCLDJException
	 */
	private static boolean writeValue(final String headerName,
	                                  final String inputValue,
	                                  final String cellValueDelimiter,
	                                  final JsonGenerator jg) throws IOException, CSV2SCLDJException {

		if (valueNotExists(inputValue)) {

			// write only valid values

			return false;
		}

		final String[] inputValues = inputValue.split(cellValueDelimiter);

		if (inputValues.length > 1) {

			throw new CSV2SCLDJException(String.format("field '%s' contains multiple values, but is not multivalued", headerName));
		}

		jg.writeStringField(headerName, inputValue);

		return true;
	}

	private static Map<String, Integer> initializeHeader(final CSVRecord headerRecord) {

		if (headerRecord == null) {

			return null;
		}

		final java.util.Map<String, Integer> hdrMap = new HashMap<>();

		for (int i = 0; i < headerRecord.size(); i++) {

			final String header = headerRecord.get(i);
			final boolean containsHeader = hdrMap.containsKey(header);
			final boolean emptyHeader = header == null || header.trim().isEmpty();

			if (containsHeader && !emptyHeader) {

				throw new IllegalArgumentException(String.format("The header contains a duplicate name: \"%s\" in %s", header, headerRecord.toString()));
			}

			hdrMap.put(header, i);
		}

		return io.vavr.collection.HashMap.ofAll(hdrMap);
	}

	private static void checkHeader(final Map<String, Integer> csvInputHeaderMap,
	                                final Map<String, Field> schema) throws CSV2SCLDJException {

		if (csvInputHeaderMap == null || csvInputHeaderMap.isEmpty()) {

			throw new CSV2SCLDJException("no headers in CSV input available, add (schema conform) headers first");
		}

		final java.util.Set<String> invalidHeaders = new HashSet<>();

		csvInputHeaderMap.keySet().forEach(header -> {

			if(!schema.containsKey(header)) {

				invalidHeaders.add(header);
			}
		});

		if (!invalidHeaders.isEmpty()) {

			final StringBuilder sb = new StringBuilder();

			sb.append("there are invalid headers in CSV input (please fix them); these are:\n");

			invalidHeaders.forEach(invalidHeader -> sb.append("\t\t\"")
					.append(invalidHeader)
					.append("\"\n"));

			final String message = sb.toString();

			throw new CSV2SCLDJException(message);
		}
	}

	private static Map<String, Field> generateFieldMap(final Set<String> headers,
	                                                   final Map<String, Field> schema) {

		final java.util.HashMap<String, Field> fieldMap = new HashMap<>();

		headers.forEach(header -> {

			try {

				if (!schema.containsKey(header)) {

					throw new CSV2SCLDJException(String.format("header '%s' is not included in the schema, please define it schema first", header));
				}

				final Field field = schema.get(header).get();

				fieldMap.put(header, field);
			} catch (final CSV2SCLDJException e) {

				throw CSV2LDJError.wrap(e);
			}
		});

		return io.vavr.collection.HashMap.ofAll(fieldMap);
	}

	private static Optional<Set<String>> getRequiredFields(final Map<String, Field> schema) {

		final Set<String> requiredFields = schema
				.filter(fieldTuple -> fieldTuple._2.isRequired())
				.keySet();

		if (requiredFields.isEmpty()) {

			return Optional.empty();
		}

		return Optional.of(requiredFields);
	}

	/**
	 * returns true, if value does not exists, i.e., if its null or empty
	 *
	 * @param value value to check
	 * @return true, if value does not exists, i.e., if its null or empty
	 */
	private static boolean valueNotExists(final String value) {

		return value == null || value.trim().isEmpty();
	}

	private static Optional<Set<String>> checkRequiredFields(final Set<String> requiredFields,
	                                                         final java.util.Set<String> writtenFields) {

		final Set<String> notWrittenRequiredField = requiredFields
				.filter(requiredField -> !writtenFields.contains(requiredField));

		if (notWrittenRequiredField.isEmpty()) {

			return Optional.empty();
		}

		return Optional.of(notWrittenRequiredField);
	}
}
