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
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.QualityRule
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

@DisallowConcurrentExecution
class QualityCheckJob implements Job {

	private static final Logger logger = LogManager.getLogger(QualityCheckJob)

	public static final String RULE_NAME_PARAMETER = "rule_name"

    QualityService qualityService
    ICayenneService cayenneService

	@Inject
    QualityCheckJob(QualityService qualityService, ICayenneService cayenneService) {
		this.qualityService = qualityService
		this.cayenneService = cayenneService
	}

	@Override
	void execute(JobExecutionContext executionContext) throws JobExecutionException {
		ObjectSelect.query(QualityRule)
				.where(QualityRule.ACTIVE.eq(true))
				.select(cayenneService.newContext)
				.each { rule ->
			try {
				qualityService.performRuleCheck(rule.keyCode)
			} catch (Exception e) {
				logger.catching(e)
            }
		}
	}
}
