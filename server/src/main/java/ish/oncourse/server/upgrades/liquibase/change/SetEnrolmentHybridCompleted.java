/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.upgrades.liquibase.change;

import ish.common.types.AttendanceType;
import ish.common.types.CourseClassType;
import ish.common.types.EnrolmentStatus;
import ish.liquibase.IshTaskChange;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Document;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class SetEnrolmentHybridCompleted extends IshTaskChange {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        logger.warn("Running upgrade SetEnrolmentHybridCompleted...");

        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewNonReplicatingContext();

        int n = 0;
        List<Enrolment> enrolments;
        do {
            enrolments = ObjectSelect.query(Enrolment.class)
                    .where(Enrolment.COURSE_CLASS.dot(CourseClass.TYPE).eq(CourseClassType.HYBRID)
                            .andExp(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS)))
                    .offset(n++)
                    .limit(500)
                    .prefetch(Enrolment.COURSE_CLASS.joint())
                    .prefetch(Enrolment.COURSE_CLASS.dot(CourseClass.SESSIONS).joint())
                    .select(context);

            enrolments.forEach(Enrolment::updateHybridCompleted);
            context.commitChanges();
        } while (!enrolments.isEmpty());
    }
}
