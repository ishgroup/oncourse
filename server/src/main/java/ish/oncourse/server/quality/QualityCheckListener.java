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

package ish.oncourse.server.quality;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.QualityRule;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostRemove;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class QualityCheckListener {

	private QualityService qualityService;
	private ICayenneService cayenneService;

	public QualityCheckListener(QualityService qualityService, ICayenneService cayenneService) {
		this.qualityService = qualityService;
		this.cayenneService = cayenneService;
	}

	@PostPersist
	public void postPersist(Object entity) {
		triggerEntityRuleCheck(entity);
	}

	@PostUpdate
	public void postUpdate(Object entity) {
		triggerEntityRuleCheck(entity);
	}

	@PostRemove
	public void postRemove(Object entity) {
		triggerEntityRuleCheck(entity);
	}

	private void triggerEntityRuleCheck(Object entity) {
		ObjectContext context = cayenneService.getNewContext();
		var rules = ObjectSelect.query(QualityRule.class)
				.where(QualityRule.ACTIVE.eq(true).andExp(QualityRule.ENTITY.eq(entity.getClass().getSimpleName())))
				.select(context);

		for (var rule : rules) {
			qualityService.performRuleCheck(rule);
		}
	}
}
