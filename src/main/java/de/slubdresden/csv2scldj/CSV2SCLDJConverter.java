/**
 * Copyright © 2017 SLUB Dresden (<code@dswarm.org>)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.slubdresden.csv2scldj.model.Field;
import de.slubdresden.csv2scldj.schema.finc.FincSolrSchema;

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

		checkHeader(csvInputHeaderMap);

		final Map<String, Field> fieldMap = generateFieldMap(csvInputHeaderMap.keySet(), schema);

		while (iterator.hasNext()) {

			inputRecordCounter.incrementAndGet();

			final CSVRecord csvInputRecord = iterator.next();

			writeRecord(csvInputRecord, csvInputHeaderMap, fieldMap, cellValueDelimiter, writer, outputRecordCounter);

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
	                                final String cellValueDelimiter,
	                                final Writer writer,
	                                final AtomicInteger outputRecordCounter) throws IOException {

		final JsonGenerator jg = FACTORY.createGenerator(writer);

		jg.writeStartObject();

		csvInputHeaderMap.forEach((headerName, columnNumber) -> {

			final boolean isMultivalue = fieldMap.get(headerName).isMultivalued();

			final String inputValue = csvInputRecord.get(columnNumber);

			try {

				if (isMultivalue) {

					writeValues(headerName, inputValue, cellValueDelimiter, jg);

					return;
				}

				writeValue(headerName, inputValue, cellValueDelimiter, jg);

			} catch (final IOException | CSV2SCLDJException e) {

				throw CSV2LDJError.wrap(new CSV2SCLDJException(String.format("something went wrong at writing record '%s' in CSV to schema conform line-delimited JSON converting", csvInputRecord.toString()), e));
			}
		});

		jg.writeEndObject();
		jg.flush();

		outputRecordCounter.incrementAndGet();
	}

	private static void writeValues(final String headerName,
	                                final String inputCell,
	                                final String cellValueDelimiter,
	                                final JsonGenerator jg) throws IOException {

		if (valueNotExists(inputCell)) {

			// write only valid values

			return;
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
	}

	private static void writeValue(final String headerName,
	                               final String inputValue,
	                               final String cellValueDelimiter,
	                               final JsonGenerator jg) throws IOException, CSV2SCLDJException {

		if (valueNotExists(inputValue)) {

			// write only valid values

			return;
		}

		final String[] inputValues = inputValue.split(cellValueDelimiter);

		if (inputValues.length > 1) {

			throw new CSV2SCLDJException(String.format("field '%s' contains multiple values, but is not multivalued", headerName));
		}

		jg.writeStringField(headerName, inputValue);
	}

	private static Map<String, Integer> initializeHeader(final CSVRecord headerRecord) {

		if (headerRecord == null) {

			return null;
		}

		final Map<String, Integer> hdrMap = new HashMap<>();

		for (int i = 0; i < headerRecord.size(); i++) {

			final String header = headerRecord.get(i);
			final boolean containsHeader = hdrMap.containsKey(header);
			final boolean emptyHeader = header == null || header.trim().isEmpty();

			if (containsHeader && !emptyHeader) {

				throw new IllegalArgumentException(String.format("The header contains a duplicate name: \"%s\" in %s", header, headerRecord.toString()));
			}

			hdrMap.put(header, i);
		}

		return hdrMap;
	}

	private static void checkHeader(final Map<String, Integer> csvInputHeaderMap) throws CSV2SCLDJException {

		if (csvInputHeaderMap == null || csvInputHeaderMap.isEmpty()) {

			throw new CSV2SCLDJException("no headers in CSV input available, add (schema conform) headers first");
		}

		final Set<String> invalidHeaders = new HashSet<>();

		csvInputHeaderMap.keySet().forEach(header -> {

			try {

				FincSolrSchema.getFieldByFieldName(header);
			} catch (final CSV2SCLDJException e) {

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

		final Map<String, Field> fieldMap = new HashMap<>();

		headers.forEach(header -> {

			try {

				if (!schema.containsKey(header)) {

					throw new CSV2SCLDJException(String.format("header '%s' is not included in the schema, please define it schema first", header));
				}

				final Field field = schema.get(header);

				fieldMap.put(header, field);
			} catch (final CSV2SCLDJException e) {

				throw CSV2LDJError.wrap(e);
			}
		});

		return fieldMap;
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
}
