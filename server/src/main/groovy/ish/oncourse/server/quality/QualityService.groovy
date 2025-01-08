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

package ish.oncourse.server.quality

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.QualityRule
import ish.oncourse.server.quality.api.QualityResultHelper
import ish.oncourse.server.scripting.GroovyScriptService
import ish.quality.QualityResult
import ish.quality.QualityResultsContainer
import ish.quality.QualityRuleCheckResult
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

/**
 * Service responsible for performing quality rule analysis.
 */
@CompileStatic
class QualityService {

	public static final String QUALITY_TOPIC = "quality"

	private ICayenneService cayenneService
	private ObjectContext context
	private ScriptEngineManager scriptEngineManager
	private PreferenceController preferenceController
	private QualityResultsContainer resultsContainer

	@Inject
	QualityService(ICayenneService cayenneService, PreferenceController preferenceController) {
		this.cayenneService = cayenneService
		this.context = cayenneService.newContext
		this.scriptEngineManager = new ScriptEngineManager()
		this.preferenceController = preferenceController
		this.resultsContainer = new QualityResultsContainer()
	}

	/**
	 * Performs rule check.
	 *
	 * @param rule rule
	 * @return quality check result
	 */
	synchronized Collection<QualityResult> performRuleCheck(QualityRule rule) {
		def groovyScriptEngine = scriptEngineManager.getEngineByName("groovy")

		def bindings = new SimpleBindings()
		bindings.put("context", context)
		bindings.put("result", QualityResultHelper.&result)
		bindings.put("builder", QualityResultHelper.&builder)

		Collection<QualityResult> results =
				groovyScriptEngine.eval(GroovyScriptService.DEFAULT_IMPORTS +
						String.format(GroovyScriptService.PREPARE_LOGGER, rule.name) +
						rule.script, bindings) as Collection<QualityResult>

		results.each { r -> r.ruleCode = rule.keyCode }

		resultsContainer.addResult(rule.keyCode, results)

		// broadcast rule check result to clients


		return results
	}

	/**
	 * Performs check using specified rule keyCode.
	 *
	 * @param ruleKeyCode rule keyCode
	 * @return quality check result
	 */
	Collection<QualityResult> performRuleCheck(String ruleKeyCode) {
		def rule = ObjectSelect.query(QualityRule).where(QualityRule.KEY_CODE.eq(ruleKeyCode)).selectOne(context)

		if (rule == null) {
			throw new IllegalArgumentException("No rule with keyCode='${ruleKeyCode}' was found.")
		}

		return performRuleCheck(rule)
	}

    Collection<QualityRuleCheckResult> getQualityCheckResults() {
		return resultsContainer.results
	}

    Collection<QualityResult> getAllResults() {
		return resultsContainer.allResults
	}

    Collection<QualityResult> getEntityQualityCheckResults(String entity) {
		return resultsContainer.getEntityResults(entity)
	}

    Collection<QualityResult> getQualityCheckResults(String entity, Long recordId) {
		return resultsContainer.getRecordResults(entity, recordId)
	}

    Collection<QualityResult> getQualityCheckResults(String ruleCode) {
		return resultsContainer.getRuleResults(ruleCode)
	}
}
