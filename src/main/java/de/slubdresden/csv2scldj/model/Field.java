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
package de.slubdresden.csv2scldj.model;

public class Field {

	private final String fieldName;

	private final boolean multivalued;

	private final boolean required;

	public Field(final String fieldNameArg) {

		fieldName = fieldNameArg;
		multivalued = false;
		required = false;
	}

	public Field(final String fieldNameArg,
	             final Boolean multivaluedArg) {

		fieldName = fieldNameArg;
		multivalued = multivaluedArg != null && multivaluedArg;
		required = false;
	}

	public Field(final String fieldNameArg,
	             final Boolean multivaluedArg,
	             final Boolean requiredArg) {

		fieldName = fieldNameArg;
		multivalued = multivaluedArg != null && multivaluedArg;
		required = requiredArg != null && requiredArg;
	}

	public String getFieldName() {

		return fieldName;
	}

	public Boolean isMultivalued() {

		return multivalued;
	}

	public Boolean isRequired() {

		return required;
	}
}
