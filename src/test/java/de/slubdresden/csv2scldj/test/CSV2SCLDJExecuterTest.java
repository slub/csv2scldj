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
package de.slubdresden.csv2scldj.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.slubdresden.csv2scldj.CSV2SCLDJException;
import de.slubdresden.csv2scldj.CSV2SCLDJExecuter;
import de.slubdresden.csv2scldj.constants.CSV2SCLDJParams;
import de.slubdresden.csv2scldj.constants.Constants;
import de.slubdresden.csv2scldj.model.Field;
import de.slubdresden.csv2scldj.util.TestUtil;
import de.slubdresden.csv2scldj.utils.SchemaUtils;

public class CSV2SCLDJExecuterTest {

	private static final Logger LOG = LoggerFactory.getLogger(CSV2SCLDJExecuterTest.class);

	private static final String PROJECT_ROOT = System.getProperty("project.root");
	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String ROOT_PATH;

	static {

		if (PROJECT_ROOT != null) {

			ROOT_PATH = PROJECT_ROOT;
		} else if (USER_DIR != null) {

			ROOT_PATH = USER_DIR;
		} else {

			CSV2SCLDJExecuterTest.LOG.error("could not determine root path - project.root and user.dir is not available");

			ROOT_PATH = "";
		}
	}

	private static final String TEST_RESOURCES_ROOT_PATH =
			ROOT_PATH + File.separator + "src" + File.separator + "test" + File.separator + "resources";
	private static final String DEFAULT_RESULTS_FOLDER = ROOT_PATH + File.separator + "target";


	@Test
	public void testCSV2SCLDJConverter() throws IOException, CSV2SCLDJException, JSONException {

		LOG.info("start CSV 2 schema conform line-delimited JSON test");

		final Reader reader = TestUtil.getResourceAsReader("input_sample.csv");
		final String schemaFilePath = TEST_RESOURCES_ROOT_PATH + File.separator + "finc_solr_schema.csv";
		final io.vavr.collection.Map<String, Field> schema = SchemaUtils.readSchema(schemaFilePath);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
		final String cellValueDelimiter = "\\/\\(\\)";

		CSV2SCLDJExecuter.convertCSV2SCLDJ(reader, schema, writer, cellValueDelimiter);

		final String actualSCJResultString = outputStream.toString(Constants.UTF_8_ENCODING);
		final String expectedSCLDJJResultString = TestUtil.getResourceAsString("output_sample.ldj");

		compareSCLDJResult(expectedSCLDJJResultString, actualSCJResultString);

		LOG.info("end CSV 2 schema conform line-delimited JSON test");
	}

	@Test
	public void testCSV2SCLDJConverter2() throws IOException, CSV2SCLDJException {

		LOG.info("start CSV 2 schema conform line-delimited JSON test 2");

		final Reader reader = TestUtil.getResourceAsReader("input_sample_2.csv");
		final String schemaFilePath = TEST_RESOURCES_ROOT_PATH + File.separator + "finc_solr_schema.csv";
		final io.vavr.collection.Map<String, Field> schema = SchemaUtils.readSchema(schemaFilePath);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
		final String cellValueDelimiter = "\\/\\(\\)";

		try {

			CSV2SCLDJExecuter.convertCSV2SCLDJ(reader, schema, writer, cellValueDelimiter);
		} catch (IOException | CSV2SCLDJException e) {

			e.printStackTrace();
		} catch (final Exception e) {

			Assert.assertTrue("this test should end up with an exception over here", true);
		}

		LOG.info("end CSV 2 schema conform line-delimited JSON test 2");
	}

	@Test
	public void testCSV2SCLDJConverterFromCommandline() throws IOException, JSONException {

		LOG.info("start CSV 2 schema conform line-delimited JSON commandline test");

		final String inputFilePath = TEST_RESOURCES_ROOT_PATH + File.separator + "input_sample.csv";
		final String schemaFilePath = TEST_RESOURCES_ROOT_PATH + File.separator + "finc_solr_schema.csv";
		final String cellValueDelimiter = "\\/\\(\\)";
		final String fileName = "output_sample.ldj";
		final String actualResultFilePath = DEFAULT_RESULTS_FOLDER + File.separator + fileName;
		final String expectedResultFilePath = TEST_RESOURCES_ROOT_PATH + File.separator + fileName;

		final String csvInputFileNameParam = CSV2SCLDJParams.CSV_INPUT_FILE_NAME + Constants.EQUALS + inputFilePath;
		final String schemaFileNameParam = CSV2SCLDJParams.SCHEMA_FILE_NAME + Constants.EQUALS + schemaFilePath;
		final String scldjOutputFileNameParam = CSV2SCLDJParams.LDJ_OUTPUT_FILE_NAME + Constants.EQUALS + actualResultFilePath;
		final String cellValueDelimiterParam = CSV2SCLDJParams.CELL_VALUE_DELIMITER_PARAM + Constants.EQUALS + cellValueDelimiter;

		final String[] args = new String[]{
				csvInputFileNameParam,
				schemaFileNameParam,
				scldjOutputFileNameParam,
				cellValueDelimiterParam
		};

		CSV2SCLDJExecuter.main(args);

		compareSCLDJResultFromFile(expectedResultFilePath, actualResultFilePath);

		LOG.info("end CSV 2 schema conform line-delimited JSON commandline test");
	}

	private static void compareSCLDJResult(final String expectedSCJResultString,
	                                       final String actualSCJResultString) throws IOException, JSONException {

		final BufferedReader expectedSCLDJReader = readContent(expectedSCJResultString);
		final BufferedReader actualSCLDJReader = readContent(actualSCJResultString);

		compareSCLDJResult(expectedSCLDJReader, actualSCLDJReader);
	}

	private static void compareSCLDJResultFromFile(final String expectedSCJResultFile,
	                                               final String actualSCJResultFile) throws IOException, JSONException {

		final BufferedReader expectedSCLDJReader = readFile(expectedSCJResultFile);
		final BufferedReader actualSCLDJReader = readFile(actualSCJResultFile);

		compareSCLDJResult(expectedSCLDJReader, actualSCLDJReader);
	}

	private static void compareSCLDJResult(final BufferedReader expectedSCLDJReader,
	                                       final BufferedReader actualSCLDJReader) throws IOException, JSONException {

		for (String expectedLine; (expectedLine = expectedSCLDJReader.readLine()) != null; ) {

			final String actuaLine = actualSCLDJReader.readLine();

			if (actuaLine == null) {

				Assert.fail("actual schema conform line-delimited JSON is already empty, but shouldn't be empty.");
			}

			JSONAssert.assertEquals(expectedLine, actuaLine, true);
		}

		if (actualSCLDJReader.readLine() != null) {

			Assert.fail("actual line-delimited JSON has more lines than the expected line-delimited JSON");
		}
	}

	private static BufferedReader readContent(final String content) throws UnsupportedEncodingException {

		final ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));

		return new BufferedReader(new InputStreamReader(inputStream));
	}

	private static BufferedReader readFile(final String filePath) throws FileNotFoundException {

		final FileReader fileReader = new FileReader(filePath);

		return new BufferedReader(fileReader);
	}
}
