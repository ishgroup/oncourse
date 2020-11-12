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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class QualityRuleCheckResult implements Serializable {

	private String ruleCode;
	private Set<QualityResult> results;

	public QualityRuleCheckResult(String ruleCode, Collection<QualityResult> results) {
		this.ruleCode = ruleCode;
		this.results = new HashSet<>(results);
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public Set<QualityResult> getResults() {
		return results;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
