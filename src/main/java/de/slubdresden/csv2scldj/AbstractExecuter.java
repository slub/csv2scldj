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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.slubdresden.csv2scldj.constants.Constants;

/**
 * Abstract executer - includes methods for parsing the arguments and printing the help.
 *
 * @author tgaengler
 */
public abstract class AbstractExecuter {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractExecuter.class);

	protected static String HELP;

	protected static void printHelp() {

		System.out.println(HELP);
	}

	protected static Optional<Map<String, String>> parseArgs(final String[] args) {

		if (args == null || args.length <= 0) {

			LOG.debug("no commandline parameters are given - no commandline parameters to parse");

			return Optional.empty();
		}

		return Optional.of(Arrays.asList(args)
				.stream()
				.map(arg -> {
					try {

						return parseArg(arg);
					} catch (final CSV2SCLDJException e) {

						throw CSV2LDJError.wrap(e);
					}
				})
				.collect(Collectors.toMap(Tuple2::_1, Tuple2::_2)));
	}

	protected static Tuple2<String, String> parseArg(final String arg) throws CSV2SCLDJException {

		if (!arg.contains(Constants.EQUALS)) {

			final String message = String.format("argument '%s' is in a wrong format; argument format should be -[KEY]=[VALUE]", arg);

			LOG.error(message);

			throw new CSV2SCLDJException(message);
		}

		final String[] split = arg.split(Constants.EQUALS);

		return Tuple.of(split[0], split[1]);
	}
}
