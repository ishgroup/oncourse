/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.expression.FieldExpressionFactory;
import com.cronutils.parser.CronParser;
import org.junit.Ignore;
import org.junit.Test;

import static com.cronutils.model.field.expression.FieldExpression.always;
import static ish.oncourse.scheduler.job.IJob.DEFAULT_CRON_DEFINITION;


/**
 * User: akoiro
 * Date: 6/5/18
 */
public class CronutilsTest {
	public static void main(String[] args) {
		Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
				.withYear(always())
				.withMonth(always())
				.withDoM(always())
				.withDoW(FieldExpressionFactory.questionMark())
				.withHour(FieldExpressionFactory.on(5))
				.withMinute(FieldExpressionFactory.on(0))
				.withSecond(FieldExpressionFactory.on(0)).instance();

		System.out.println(cron.asString());
	}


	@Test
	@Ignore
	public void test_to_period() {
//		long[] longs = ScheduleService.toPeriod(new CronParser(DEFAULT_CRON_DEFINITION).parse("0 0 0/12 ? * *"));
		long[] longs = ScheduleService.toPeriod(new CronParser(DEFAULT_CRON_DEFINITION).parse("0 0/2 * ? * *"));
//		longs = ScheduleService.toPeriod(new CronParser(DEFAULT_CRON_DEFINITION)
//				.parse("0/4 * * ? * *"));
		System.out.println(longs);
	}
}
