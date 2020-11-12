/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.print.transformations;

import java.io.Serializable;

/**
 */
public class PrintTransformationField<T extends Object> implements Serializable {

	private String fieldCode;
	private String fieldDescription;
	private Class<T> fieldClass;
	private T defaultValue;
	private Transformer<T> valueTransformer;

	public PrintTransformationField(String fieldCode, String fieldDescription, Class<T> fieldClass, T defaultValue) {
		this.fieldCode = fieldCode;
		this.fieldDescription = fieldDescription;
		this.fieldClass = fieldClass;
		this.defaultValue = defaultValue;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public String getFieldDescription() {
		return fieldDescription;
	}

	public Class<T> getFieldClass() {
		return fieldClass;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setValueTransformer(Transformer<T> transformer) {
		this.valueTransformer = transformer;
	}

	public Transformer<T> getValueTransformer() {
		return valueTransformer;
	}
}
