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
package ish.oncourse.server.scripting;

import java.util.*;

public class ScriptParameters {

	private List<ScriptParameter> parameters = new ArrayList<>();

	public ScriptParameters() { }

	public ScriptParameters fillDefaultParameters(Object entity) {
		parameters.add(new ScriptParameter<>(GroovyScriptService.ENTITY_PARAM_NAME, entity));
		parameters.add(new ScriptParameter<>(GroovyScriptService.RECORD_PARAM_NAME, entity));
		parameters.add(new ScriptParameter<>(GroovyScriptService.RECORDS_PARAM_NAME, Collections.singletonList(entity)));

		return this;
	}

	public ScriptParameters(ScriptParameters parameters) { this.parameters = parameters.parameters; }

	public static ScriptParameters empty() {
		return new ScriptParameters();
	}

	public static <T> ScriptParameters from(String name, T value) {
		return new ScriptParameters().add(name, value);
	}

	public <T> ScriptParameters add(String name, T value) {
		parameters.add(new ScriptParameter<>(name, value));

		return this;
	}

	public List<ScriptParameter> asList() {
		return Collections.unmodifiableList(parameters);
	}

	public Map<String, Object> asMap() {
		Map<String, Object> map = new HashMap<>(parameters.size());

		for (var parameter : parameters) {
			map.put(parameter.getName(), parameter.getValue());
		}

		return map;
	}

	public static class ScriptParameter<T> {
		private String name;
		private T value;

		public ScriptParameter(String name, T value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public T getValue() {
			return value;
		}
	}
}
