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

package ish.util;

import ish.messaging.ICourse;
import ish.messaging.ICourseClass;
import ish.oncourse.server.cayenne.CourseModule;
import ish.oncourse.server.cayenne.Module;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseUtil {

    private static Logger logger = LogManager.getLogger();

    private CourseUtil() {
    }

    public static void addModule(ICourse course, Module module, Class<CourseModule> courseModuleClass) {
        // this is a many to many relation, it was not solving the duplication problems well in cayenne some time ago. The code below is to ensuse that
        String nationalCode = module.getNationalCode();
        if (nationalCode != null && !nationalCode.isEmpty()) {
            Expression anExpression = ExpressionFactory.matchExp(Module.NATIONAL_CODE_KEY, nationalCode);
            List<Module> currentModules = anExpression.filterObjects(new ArrayList<>(course.getModules()));
            logger.debug("current modules {}", currentModules.size());
            if (currentModules.size() == 0) {
                Module localModule = course.getContext().localObject(module);

                CourseModule courseModule = course.getContext().newObject(courseModuleClass);
                courseModule.setCourse(course);
                courseModule.setModule(localModule);

                // propagate module changes to all sessions of classes linked to course
                for (ICourseClass courseClass : course.getCourseClasses()) {
                    courseClass.addModuleToAllSessions(localModule);
                }

                // touch the modified on date to force postUpdate and eventually replication
                if (!course.isModifiedRecord()) {
                    course.setModifiedOn(new Date());
                }
            }
        }
    }
}
