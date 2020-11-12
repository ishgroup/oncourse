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
package ish.quality;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class QualityResultsContainer {

	private Map<String, Set<QualityResult>> resultMap = new ConcurrentHashMap<>();

	public synchronized Collection<QualityRuleCheckResult> getResults() {
		return resultMap.entrySet().stream()
				.map(e -> new QualityRuleCheckResult(e.getKey(), e.getValue())).collect(Collectors.toList());
	}

	public synchronized Collection<QualityResult> getAllResults() {
		return resultMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	public synchronized Collection<QualityResult> getEntityResults(String entity) {
		return resultMap.values().stream().flatMap(Collection::stream)
				.filter(qr -> qr.getEntity().equals(entity)).collect(Collectors.toList());
	}

	public synchronized Collection<QualityResult> getRecordResults(String entity, Long recordId) {
		return resultMap.values().stream().flatMap(Collection::stream)
				.filter(qr -> qr.getEntity().equals(entity) && qr.getRecords().contains(recordId)).collect(Collectors.toList());
	}

	public synchronized Collection<QualityResult> getRuleResults(String ruleCode) {
		return resultMap.get(ruleCode) != null ? resultMap.get(ruleCode) : Collections.emptySet();
	}

	public synchronized void addResult(QualityRuleCheckResult ruleCheckResult) {
		addResult(ruleCheckResult.getRuleCode(), ruleCheckResult.getResults());
	}

	public synchronized void addResult(String ruleCode, Collection<QualityResult> results) {
		if (results.stream().filter(qr -> !qr.getRuleCode().equals(ruleCode)).count() > 0) {
			throw new IllegalArgumentException("One or several passed results don't match the rule code.");
		}

		resultMap.put(ruleCode, new HashSet<>(results));
	}

	public synchronized void clear() {
		resultMap.clear();
	}
}
