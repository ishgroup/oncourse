/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler.job;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public interface IJob extends Runnable {
	CronDefinition DEFAULT_CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

	Cron getCron();
}
