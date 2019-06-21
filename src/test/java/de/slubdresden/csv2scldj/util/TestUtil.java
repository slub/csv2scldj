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
package de.slubdresden.csv2scldj.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import static com.google.common.base.Charsets.UTF_8;

public class TestUtil {

	/**
	 * Retrieves a resource by the give path and converts its content to a string.
	 *
	 * @param resource a resource path
	 * @return a string representation fo the content of the resource
	 * @throws IOException
	 */
	public static String getResourceAsString(final String resource) throws IOException {

		final URL url = Resources.getResource(resource);
		return Resources.toString(url, UTF_8);
	}

	public static Reader getResourceAsReader(final String resource) throws IOException {

		final URL url = Resources.getResource(resource);
		final ByteSource byteSource = Resources.asByteSource(url);

		return new InputStreamReader(byteSource.openBufferedStream(), StandardCharsets.UTF_8);
	}
}
