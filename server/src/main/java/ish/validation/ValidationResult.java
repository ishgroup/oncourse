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

package ish.validation;

import ish.util.RuntimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * <p>
 * This extension to the Cayenne validation result adds a few useful functions and also adds support for custom validation warnings.
 * </p>
 * <p>
 * Note: custom validations, especially validation warnings, need to be kept separate from cayenne's validation result as they are simply warnings and are not
 * meant to prevent the committing of a context.
 * </p>
 */
public class ValidationResult extends org.apache.cayenne.validation.ValidationResult {

	private static final Logger logger = LogManager.getLogger();
	private static final long serialVersionUID = 1L;

	private final Object lock;
	private final Map<Object, Set<org.apache.cayenne.validation.ValidationFailure>> standardFailures;
	private final Map<Object, Map<String, ValidationFailure>> validationFailures;
	private final Map<Object, Map<String, ValidationWarning>> validationWarnings;
	private final Map<Class<?>, Set<Object>> validationObjects;

	/**
	 * The constructor.
	 */
	public ValidationResult() {
		super();
		this.lock = new Object();
		this.validationFailures = new HashMap<>();
		this.validationWarnings = new HashMap<>();
		this.standardFailures = new HashMap<>();
		this.validationObjects = new HashMap<>();
	}

	/**
	 * Add another set of validation results to this one.
	 *
	 * @param otherResult The other set of validation results to add to this one.
	 */
	public void addAll(ValidationResult otherResult) {
		if (otherResult != null) {
			synchronized (this.lock) {
				for (org.apache.cayenne.validation.ValidationFailure failure : otherResult.getFailures()) {
					addFailure(failure);
				}

				for (ValidationFailure warning : otherResult.getWarnings()) {
					addFailure(warning);
				}
			}
		}
	}

	/**
	 * @see org.apache.cayenne.validation.ValidationResult#addFailure(org.apache.cayenne.validation.ValidationFailure)
	 */
	@Override
	public void addFailure(org.apache.cayenne.validation.ValidationFailure failure) {
		if (failure != null) {
			logger.debug("Add failure: {}", failure.getDescription());

			synchronized (this.lock) {
				if (failure instanceof ValidationWarning) {
					ValidationWarning vw = (ValidationWarning) failure;
					Map<String, ValidationWarning> sourceFailures = this.validationWarnings.get(vw.getSource());
					if (sourceFailures == null) {
						sourceFailures = new HashMap<>();
						this.validationWarnings.put(vw.getSource(), sourceFailures);
					}
					sourceFailures.put(vw.getProperty(), vw);

				} else if (failure instanceof ValidationFailure) {
					ValidationFailure vf = (ValidationFailure) failure;
					Map<String, ValidationFailure> sourceFailures = this.validationFailures.get(vf.getSource());
					if (sourceFailures == null) {
						sourceFailures = new HashMap<>();
						this.validationFailures.put(vf.getSource(), sourceFailures);
					}
					sourceFailures.put(vf.getProperty(), vf);

				} else {
					Set<org.apache.cayenne.validation.ValidationFailure> failures = this.standardFailures.get(failure.getSource());
					if (failures == null) {
						failures = new HashSet<>();
						this.standardFailures.put(failure.getSource(), failures);
					}
					failures.add(failure);
				}
			}
		}
	}

	/**
	 * @param warning - the warning to add
	 */
	public void addWarning(ValidationWarning warning) {
		if (warning != null) {
			addFailure(warning);
		}
	}

	/**
	 * @return a string representation of the result failures.
	 */
	public String failuresToString() {
		StringBuilder buff = new StringBuilder();
		for (org.apache.cayenne.validation.ValidationFailure vf : getFailures()) {
			buff.append(vf.getClass()).append(" ").append(vf.getError()).append(" ").append(vf.getDescription()).append(RuntimeUtil.LINE_SEPARATOR);
		}
		return buff.toString();
	}

	/**
	 * Note that any validation warnings are stripped out of the result from this function.
	 *
	 * @see org.apache.cayenne.validation.ValidationResult#getFailures()
	 */
	@Override
	public List<org.apache.cayenne.validation.ValidationFailure> getFailures() {
		Set<org.apache.cayenne.validation.ValidationFailure> results = new HashSet<>();
		synchronized (this.lock) {
			for (Set<org.apache.cayenne.validation.ValidationFailure> someResults : this.standardFailures.values()) {
				if (someResults != null) {
					results.addAll(someResults);
				}
			}
			for (Map<String, ValidationFailure> sourceResults : this.validationFailures.values()) {
				results.addAll(sourceResults.values());
			}
		}
		return new ArrayList<>(results);
	}

	/**
	 * Note that any validation warnings are stripped out of the result from this function.
	 *
	 * @see org.apache.cayenne.validation.ValidationResult#getFailures(Object)
	 */
	@Override
	public List<org.apache.cayenne.validation.ValidationFailure> getFailures(Object source) {
		Set<org.apache.cayenne.validation.ValidationFailure> results = new HashSet<>();
		synchronized (this.lock) {
			Set<org.apache.cayenne.validation.ValidationFailure> someResults = this.standardFailures.get(source);
			if (someResults != null) {
				results.addAll(someResults);
			}
			Map<String, ValidationFailure> otherResults = this.validationFailures.get(source);
			if (otherResults != null && otherResults.size() > 0) {
				results.addAll(otherResults.values());
			}
		}
		return new ArrayList<>(results);
	}

	/**
	 * Return the first validation failure found for a particular key. This will include validation warnings.
	 *
	 * @param propertyKey entity model attribute property key
	 * @return the found validation failure or warning
	 */
	public ValidationFailure getIshFailureForKey(String propertyKey) {
		ValidationFailure result;
		if (propertyKey != null) {
			synchronized (this.lock) {
				for (Map<String, ValidationFailure> failure : this.validationFailures.values()) {
					result = failure.get(propertyKey);
					if (result != null) {
						return result;
					}
				}
				for (Map<String, ValidationWarning> warning : this.validationWarnings.values()) {
					result = warning.get(propertyKey);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get any validation warnings in this result.
	 *
	 * @return validation warnings
	 */
	public List<ValidationWarning> getWarnings() {
		Set<ValidationWarning> results = new HashSet<>();
		synchronized (this.lock) {
			for (Map<String, ValidationWarning> sourceResults : this.validationWarnings.values()) {
				if (sourceResults.size() > 0) {
					results.addAll(sourceResults.values());
				}
			}
		}
		return new ArrayList<>(results);
	}

	/**
	 * @see org.apache.cayenne.validation.ValidationResult#hasFailures()
	 */
	@Override
	public boolean hasFailures() {
		return getFailures().size() > 0;
	}

	/**
	 * @see org.apache.cayenne.validation.ValidationResult#hasFailures(Object)
	 */
	@Override
	public boolean hasFailures(Object source) {
		boolean result;
		synchronized (this.lock) {
			Map<String, ValidationFailure> sourceFailures = this.validationFailures.get(source);
			Set<org.apache.cayenne.validation.ValidationFailure> otherFailures = this.standardFailures.get(source);
			result = sourceFailures != null && sourceFailures.size() > 0 || otherFailures != null && otherFailures.size() > 0;
		}
		return result;
	}

	/**
	 * @return true if there are any validation warnings in this result
	 */
	public boolean hasWarnings() {
		return getWarnings().size() > 0;
	}

	/**
	 * Tests to see if validation for the source object is needed. This can be useful for speeding up validation calls by reducing the number of times an object
	 * is validated. i.e., surround validateForSave with an if statement.
	 *
	 * @param source the object to test
	 * @return false if validation has not been performed on the given object.
	 */
	public boolean isValidated(Object source, Class<?> validatingType) {
		Set<Object> objects = this.validationObjects.get(validatingType);
		if (objects == null) {
			objects = new HashSet<>();
			this.validationObjects.put(validatingType, objects);
		}
		return !objects.add(source);
	}

	@Override
	public String toString() {
		return failuresToString();
	}
}
