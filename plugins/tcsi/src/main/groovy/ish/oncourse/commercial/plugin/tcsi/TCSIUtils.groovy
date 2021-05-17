/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import ish.common.types.AvetmissStudentDisabilityType
import ish.common.types.AvetmissStudentIndigenousStatus
import ish.common.types.AvetmissStudentLabourStatus
import ish.common.types.AvetmissStudentPriorEducation
import ish.common.types.AvetmissStudentSchoolLevel
import ish.common.types.CourseClassAttendanceType
import ish.common.types.DeliveryMode
import ish.common.types.EnrolmentStatus
import ish.common.types.Gender
import ish.common.types.OutcomeStatus
import ish.common.types.RecognitionOfPriorLearningIndicator
import ish.common.types.StudentCitizenship
import ish.common.types.StudentStatusForUnitOfStudy
import ish.common.types.StudyReason
import ish.math.Money
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate


class TCSIUtils {
    
    static final String DATE_FORMAT='yyyy-MM-dd'
    
    


    static List<Course> getUnitCourses(Course hihgEducation, EntityRelationType highEducationType) {
        ObjectContext context =  hihgEducation.context
        List<EntityRelation> unitRelations = ObjectSelect.query(EntityRelation)
                .where(EntityRelation.RELATION_TYPE.eq(highEducationType))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(hihgEducation.id))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .select(context)

        return unitRelations
                .collect { SelectById.query(Course, it.toRecordId).selectOne(context) }

    }

    static Course getHighEducation(ObjectContext context, EntityRelationType highEducationType, Enrolment enrolment) {
        Course course = enrolment.courseClass.course

        EntityRelation relation = ObjectSelect.query(EntityRelation)
                .where(EntityRelation.RELATION_TYPE.eq(highEducationType))
                .and(EntityRelation.TO_ENTITY_ANGEL_ID.eq(course.id))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .selectFirst(context)
        
        if (relation) {
            return SelectById.query(Course, relation.fromRecordId).selectOne(context)
        } else {
            //check if course is high education itself 
            relation = ObjectSelect.query(EntityRelation)
                    .where(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(course.id))
                    .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                    .and(EntityRelation.RELATION_TYPE.eq(highEducationType)).selectFirst(context)
            if (relation) {
                return course
            }
        }
        return null
    }
}
