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

/**
 * @author tgaengler
 */
public class CSV2LDJError extends RuntimeException {

	public CSV2LDJError(final Throwable cause) {

		super(cause);
	}

	public static CSV2LDJError wrap(final CSV2SCLDJException exception) {

		return new CSV2LDJError(exception);
	}
}
