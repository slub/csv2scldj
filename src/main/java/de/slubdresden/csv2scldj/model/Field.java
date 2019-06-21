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
