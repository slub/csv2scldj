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
package de.slubdresden.csv2scldj.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.vavr.collection.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.slubdresden.csv2scldj.CSV2LDJError;
import de.slubdresden.csv2scldj.CSV2SCLDJException;
import de.slubdresden.csv2scldj.model.Field;

public final class SchemaUtils {

	private static final Logger LOG = LoggerFactory.getLogger(SchemaUtils.class);

	private final static char DELIMITER = ',';
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String MULTIVALUED = "multivalued";
	private static final String REQUIRED = "required";

	public static Map<String, Field> readSchema(final String schemaFileName) throws CSV2SCLDJException {

		final HashMap<String, Field> schema = new LinkedHashMap<>();

		try {

			final Reader schemaReader = new FileReader(schemaFileName);
			final CSVParser schemaCSVParser = new CSVParser(schemaReader, CSVFormat.newFormat(DELIMITER));

			schemaCSVParser.forEach(fieldCSVRecord -> {

				final int fieldCSVRecordSize = fieldCSVRecord.size();

				if (fieldCSVRecordSize < 1) {

					// only parse, none empty lines

					return;
				}

				final String fieldName = fieldCSVRecord.get(0);

				try {

					final boolean multivalued;

					if (fieldCSVRecordSize >= 2) {

						multivalued = parseBooleanValue(schemaFileName, fieldName, MULTIVALUED, fieldCSVRecord.get(1));
					} else {

						// return default (false), if multivalued is not set

						multivalued = false;
					}

					final boolean required;

					if (fieldCSVRecordSize >= 3) {

						required = parseBooleanValue(schemaFileName, fieldName, REQUIRED, fieldCSVRecord.get(2));
					} else {

						// return default (false), if required is not set

						required = false;
					}

					final Field field = new Field(fieldName, multivalued, required);

					schema.put(fieldName, field);

				} catch (final CSV2SCLDJException e) {

					throw CSV2LDJError.wrap(e);
				}

			});
		} catch (final FileNotFoundException e) {

			LOG.error(String.format("could not find schema file at '%s': ", schemaFileName), e);

			throw new CSV2SCLDJException(String.format("could not find schema file at path '%s', please provide a valid schema file path", schemaFileName));
		} catch (final Exception io) {

			LOG.error(String.format("error while parsing schema file '%s': ", schemaFileName), io);

			throw new CSV2SCLDJException(String.format("something went wrong, while parsing the the schema file at '%s', please check the logs for further details", schemaFileName));
		}

		if (schema.isEmpty()) {

			throw new CSV2SCLDJException(String.format("could not parse anything from the schema file at '%s', please provide a proper schema file first", schemaFileName));
		}

		LOG.info(String.format("successfully parsed schema from schema file at '%s'", schemaFileName));

		return io.vavr.collection.HashMap.ofAll(schema);
	}

	private static boolean parseBooleanValue(final String schemaFileName,
	                                         final String fieldName,
	                                         final String parameter,
	                                         final String csvValue) throws CSV2SCLDJException {

		if (csvValue == null) {

			// return default (false), if value is not available

			return false;
		}

		if (csvValue.trim().isEmpty()) {

			// return default (false), if value is not set

			return false;
		}

		if (!(TRUE.equals(csvValue) || FALSE.equals(csvValue))) {

			throw new CSV2SCLDJException(String.format("please use either 'true' or 'false' to define the value of the parameter '%s' for field '%s' in the schema file at '%s'", fieldName, parameter, schemaFileName));
		}

		return Boolean.valueOf(csvValue);
	}
}
