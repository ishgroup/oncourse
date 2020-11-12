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

import ish.scripting.CronExpressionType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ish.scripting.CronExpressionType.*;

public class CronExpressionGenerator {

	private static final List<String> defaultCronTypes = Stream.of(DAILY_MORNING, DAILY_EVENING, WEEKLY_MONDAY, HOURLY)
			.map(CronExpressionType::getDatabaseValue).collect(Collectors.toList());

	public static String generate(String expression) {
		var cronExpression = expression;

		if (DAILY_MORNING.getDatabaseValue().equals(expression)) {
			cronExpression = getCronExpression("30 %s 5 * * ?"); // every day 5am - 6am
		} else if (DAILY_EVENING.getDatabaseValue().equals(expression)) {
			cronExpression = getCronExpression("30 %s 19 * * ?"); // every day 7pm - 8pm
		} else if (WEEKLY_MONDAY.getDatabaseValue().equals(expression)) {
			cronExpression = getCronExpression("30 %s 6 ? * MON *"); // every Monday 6am - 7am
		} else if (HOURLY.getDatabaseValue().equals(expression)) {
			cronExpression = getCronExpression("30 %s 0/1 * * ?"); // every hour
		}

		return cronExpression;
	}

	public static boolean isDefaultCron(String expression) {
		return defaultCronTypes.contains(expression);
	}

	private static String getCronExpression(String expression) {
		var randomNumber = (int) (Math.random() * 60);
		return String.format(expression, randomNumber);
	}
}
