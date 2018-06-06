/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: akoiro
 * Date: 1/6/18
 */
public class ProcessDebug {
	private Map<String, Object> debugMap;


	public ProcessDebug debugMap(Map<String, Object> debugMap) {
		this.debugMap = debugMap;
		return this;
	}

	public ProcessDebug process() {
		NamedList<Map> list = (NamedList<Map>) debugMap.get("explain");

		List<Value> values = list.asShallowMap().entrySet().stream()
				.map((e) -> Value.valueOf(e))
				.collect(Collectors.toList());

		Value value9 = values.stream().filter(value -> value.key.equals("9")).findFirst().orElse(null);
		Value value7 = values.stream().filter(value -> value.key.equals("7")).findFirst().orElse(null);

		System.out.println(
				String.format("%s:%f:%s",
						value9.details.get(0).details.get(1).description,
						value9.details.get(0).details.get(1).value,
						value9.details.get(0).details.get(1).details)
		);
		System.out.println(String.format("%s:%f",
				value7.details.get(0).details.get(1).description,
				value7.details.get(0).details.get(1).value)
		);

		return this;
	}

	public static class Value {
		private String key;
		private Boolean match;
		private Float value;
		private String description;
		private List<Value> details;

		public String getKey() {
			return key;
		}

		public Boolean getMatch() {
			return match;
		}

		public Float getValue() {
			return value;
		}

		public String getDescription() {
			return description;
		}

		public List<Value> getDetails() {
			return details;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		public static Value valueOf(SimpleOrderedMap map) {
			Value value = new Value();
			value.match = map.getBooleanArg("match");
			value.value = (Float) map.get("value");
			value.description = (String) map.get("description");
			if (map.get("details") != null && map.get("details") instanceof List)
				value.details = ((List<SimpleOrderedMap>) map.get("details")).stream().map(
						Value::valueOf
				).collect(Collectors.toList());
			return value;
		}

		public static Value valueOf(Map.Entry<String, Map> map) {
			Value value = new Value();
			value.key = map.getKey();
			value.match = (Boolean) map.getValue().get("match");
			value.value = (Float) map.getValue().get("value");
			value.description = (String) map.getValue().get("description");
			if (map.getValue().get("details") != null && map.getValue().get("details") instanceof List)
				value.details = ((List<SimpleOrderedMap>) map.getValue().get("details")).stream()
						.map(Value::valueOf)
						.collect(Collectors.toList());
			return value;
		}

	}
}
