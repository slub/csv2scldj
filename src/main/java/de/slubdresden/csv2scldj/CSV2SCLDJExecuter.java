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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.slubdresden.csv2scldj.constants.CSV2SCLDJParams;
import de.slubdresden.csv2scldj.constants.Constants;

/**
 * @author tgaengler
 */
public final class CSV2SCLDJExecuter extends AbstractExecuter {

	private static final Logger LOG = LoggerFactory.getLogger(CSV2SCLDJExecuter.class);

	private static final StringBuilder HELP_SB = new StringBuilder();

	static {

		HELP_SB.append("\n")
				.append("this is a csv 2 schema conform line-delimited JSON converter").append("\n\n")
				.append("\t").append("this tool is intended for converting CSV records into schema conform line-delimited JSON records").append("\n\n")
				.append("following parameters are available for configuration at the moment:").append("\n\n")
				.append("\t").append(CSV2SCLDJParams.CSV_INPUT_FILE_NAME).append(" : the (absolute) file path to the input CSV file").append("\n")
				.append("\t").append(CSV2SCLDJParams.LDJ_OUTPUT_FILE_NAME).append(" : the (absolute) file path to the output line-delimited JSON file").append("\n")
				.append("\t").append(CSV2SCLDJParams.CELL_VALUE_DELIMITER_PARAM).append(" : the value delimiter in CSV cells that include multiple values").append("\n")
				.append("\t").append(CSV2SCLDJParams.HELP_PARAM).append(" : prints this help").append("\n\n")
				.append("\t").append("you can also run this application without setting ").append(CSV2SCLDJParams.CSV_INPUT_FILE_NAME).append(" and ").append(CSV2SCLDJParams.LDJ_OUTPUT_FILE_NAME).append(", i.e., then you can simply rely on stdin for input and stdout for output").append("\n\n")
				.append("have fun with this tool!").append("\n\n")
				.append("if you observe any problems with this tool or have questions about handling this tool etc. don't hesitate to contact us").append("\n")
				.append("(you can find our contact details at http://slub-dresden.de)").append("\n");

		HELP = HELP_SB.toString();
	}

	public static void convertCSV2SCLDJ(final Reader reader,
	                                    final BufferedWriter writer,
	                                    final String cellValueDelimiter) throws IOException, CSV2SCLDJException {

		CSV2SCLDJConverter.convert(reader, writer, cellValueDelimiter);
	}

	public static void main(final String[] args) {

		if (args.length == 1 && CSV2SCLDJParams.HELP_PARAM.equals(args[0])) {

			printHelp();

			System.exit(1);

			return;
		}

		final Optional<Map<String, String>> optionalArgMap;

		try {

			optionalArgMap = parseArgs(args);
		} catch (final Exception e) {

			printHelp();

			System.exit(1);

			return;
		}

		try {

			final Map<String, String> argMap = checkOptionalParameters(optionalArgMap.orElse(new HashMap<>()));

			final Optional<String> optionalCSVInputFileName = Optional.ofNullable(argMap.get(CSV2SCLDJParams.CSV_INPUT_FILE_NAME));
			final Optional<String> optionalLDJOutputFileName = Optional.ofNullable(argMap.get(CSV2SCLDJParams.LDJ_OUTPUT_FILE_NAME));
			final String cellValueDelimiter = argMap.get(CSV2SCLDJParams.CELL_VALUE_DELIMITER_PARAM);

			final BufferedReader reader;

			if (optionalCSVInputFileName.isPresent()) {

				final String csvInputFileName = optionalCSVInputFileName.get();

				LOG.info("CSV input file name = '{}'", csvInputFileName);

				final Path csvInputFilePath = Paths.get(csvInputFileName);

				reader = Files.newBufferedReader(csvInputFilePath, StandardCharsets.UTF_8);
			} else {

				LOG.info("try to read CSV input from stdin");

				reader = new BufferedReader(new InputStreamReader(System.in, Constants.UTF_8_ENCODING));
			}

			final BufferedWriter writer;

			if (optionalLDJOutputFileName.isPresent()) {

				final String ldjOutputFileName = optionalLDJOutputFileName.get();

				LOG.info("LDJ output file name = '{}'", ldjOutputFileName);

				final Path ldjOutputFilePath = Paths.get(ldjOutputFileName);

				writer = Files.newBufferedWriter(ldjOutputFilePath, StandardCharsets.UTF_8);
			} else {

				LOG.info("try to write schema conform line-delimited JSON to stdout");

				writer = new BufferedWriter(new OutputStreamWriter(System.out, Constants.UTF_8_ENCODING));
			}

			LOG.info("cell value delimiter = '{}'", cellValueDelimiter);

			convertCSV2SCLDJ(reader, writer, cellValueDelimiter);
		} catch (final Exception e) {

			LOG.error("something went wrong at converting CSV 2 schema conform line-delimited JSON.", e);

			System.out.println("\n" + HELP);

			System.exit(1);
		}

	}

	private static Map<String, String> checkOptionalParameters(final Map<String, String> argMap) {

		final String cellValueDelimiter = argMap.get(CSV2SCLDJParams.CELL_VALUE_DELIMITER_PARAM);

		final boolean cellValueDelimiterAvailable = cellValueDelimiter != null && !cellValueDelimiter.isEmpty();

		if (!cellValueDelimiterAvailable) {

			final StringBuilder sb = new StringBuilder();

			sb.append("some of the optional properties are not available, i.e., we'll set some default values for them.");

			argMap.put(CSV2SCLDJParams.CELL_VALUE_DELIMITER_PARAM, CSV2SCLDJParams.DEFAULT_CELL_VALUE_DELIMITER_PARAM);

			sb.append("\n\t").append(CSV2SCLDJParams.CELL_VALUE_DELIMITER_PARAM).append(Constants.BLANK_EQUALS_DELIMITER).append(CSV2SCLDJParams.DEFAULT_CELL_VALUE_DELIMITER_PARAM);

			LOG.info(sb.toString());
		}

		return argMap;
	}
}
