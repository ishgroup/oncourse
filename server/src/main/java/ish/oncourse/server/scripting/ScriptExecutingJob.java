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

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Script;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;

@DisallowConcurrentExecution
public class ScriptExecutingJob implements Job {

	private static final Logger logger = LogManager.getLogger(ScriptExecutingJob.class);

	public static final String SCRIPT_NAME_PARAMETER = "script_name";

	private GroovyScriptService scriptService;
	private ICayenneService cayenneService;

	@Inject
	public ScriptExecutingJob(GroovyScriptService scriptService, ICayenneService cayenneService) {
		this.scriptService = scriptService;
		this.cayenneService = cayenneService;
	}

	@Override
	public void execute(JobExecutionContext executionContext) throws JobExecutionException {
		ObjectContext context = cayenneService.getNewContext();

		try {
			var script = getScriptByName(
					executionContext.getJobDetail().getJobDataMap().getString(SCRIPT_NAME_PARAMETER), context);

			if(script != null)
				scriptService.runScript(script);
		} catch (Exception e) {
			logger.catching(e);
		}
	}

	private Script getScriptByName(String scriptName, ObjectContext context) {
		return context.selectOne(
				SelectQuery.query(Script.class, Script.NAME.eq(scriptName)));
	}
}
