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

package ish.oncourse.server.duplicate;

import com.google.inject.Inject;
import ish.duplicate.CourseDuplicationRequest;
import ish.duplicate.DuplicationResult;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.api.dao.EntityRelationDao;
import ish.oncourse.server.cayenne.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class DuplicateCourseService {

    private static final Logger logger = LogManager.getLogger();

    @Inject
    private ICayenneService cayenneService;

    private DataContext context;

    public DuplicationResult duplicateCourses(CourseDuplicationRequest request) {
        var result = new DuplicationResult();
        context = cayenneService.getNewContext();

        for (var id : request.getIds()) {
            var oldCourse = ObjectSelect.query(Course.class)
                    .where(Course.ID.eq(id))
                    .selectOne(context);

            var newCourse = context.newObject(Course.class);

            newCourse.setWebDescription(oldCourse.getWebDescription());
            newCourse.setName(oldCourse.getName());
            newCourse.setIsShownOnWeb(false);
            newCourse.setIsTraineeship(oldCourse.getIsTraineeship());
            newCourse.setIsSufficientForQualification(oldCourse.getIsSufficientForQualification());
            newCourse.setCurrentlyOffered(oldCourse.getCurrentlyOffered());
            newCourse.setEnrolmentType(oldCourse.getEnrolmentType());
            newCourse.setFieldOfEducation(oldCourse.getFieldOfEducation());
            newCourse.setIsVET(oldCourse.getIsVET());
            newCourse.setQualification(oldCourse.getQualification());
            newCourse.setAllowWaitingLists(oldCourse.getAllowWaitingLists());
            oldCourse.getModules().forEach(newCourse::addToModules);
            newCourse.setReportableHours(oldCourse.getReportableHours());
            newCourse.setFieldConfigurationSchema(oldCourse.getFieldConfigurationSchema());
            newCourse.setFeeHelpClass(oldCourse.getFeeHelpClass());
            newCourse.setFullTimeLoad(oldCourse.getFullTimeLoad());

            for (var courseAttachmentRelation : oldCourse.getAttachmentRelations()) {
                var relation = context.newObject(CourseAttachmentRelation.class);
                relation.setAttachedCourse(newCourse);
                relation.setDocument(courseAttachmentRelation.getDocument());
                relation.setDocumentVersion(courseAttachmentRelation.getDocumentVersion());
                relation.setSpecialType(courseAttachmentRelation.getSpecialType());
            }

            oldCourse.getTags().forEach(newCourse::addTag);

            newCourse.setFieldConfigurationSchema(oldCourse.getFieldConfigurationSchema());

            var newCourseCode = DuplicateCourseCode.valueOf(oldCourse).duplicateCode();
            newCourse.setCode(newCourseCode);

            try {
                context.commitChanges();
                duplicateEntityRelations(oldCourse, newCourse);
            } catch (ValidationException ve) {
                logger.error(ve);
                context.rollbackChanges();
                var vf = ve.getValidationResult().getFailures().get(0);
                result.setFailure(vf);
                result.setFailed(true);
                return result;
            } catch (Exception e) {
                logger.error("An exception (not a validation error) was thrown when trying to duplicate a courses.", e);
                context.rollbackChanges();
                result.setFailed(true);
                return result;
            }
        }
        return result;
    }

    private void duplicateEntityRelations(Course oldCourse, Course newCourse) {

        for (var entityRelation : EntityRelationDao.getRelatedFrom(context, Course.class.getSimpleName(), oldCourse.getId())) {
            var relation = context.newObject(EntityRelation.class);
            relation.setFromEntityAngelId(entityRelation.getFromEntityAngelId());
            relation.setFromEntityIdentifier(entityRelation.getFromEntityIdentifier());
            relation.setToEntityAngelId(newCourse.getId());
            relation.setToEntityIdentifier(Course.class.getSimpleName());
            relation.setRelationType(entityRelation.getRelationType());
        }

        for (var entityRelation : EntityRelationDao.getRelatedTo(context, Course.class.getSimpleName(), oldCourse.getId())) {
            var relation = context.newObject(EntityRelation.class);
            relation.setFromEntityAngelId(newCourse.getId());
            relation.setFromEntityIdentifier(Course.class.getSimpleName());
            relation.setToEntityAngelId(entityRelation.getToEntityAngelId());
            relation.setToEntityIdentifier(entityRelation.getToEntityIdentifier());
            relation.setRelationType(entityRelation.getRelationType());
        }

        context.commitChanges();
    }

    public static class DuplicateCourseCode {

        private Course oldCourse;
        private ObjectContext context;
        private Pattern endsWithNumberPattern;

        private DuplicateCourseCode() {
        }

        public static DuplicateCourseCode valueOf(Course oldCourse) {
            var duplicateCourseCode = new DuplicateCourseCode();
            duplicateCourseCode.oldCourse = oldCourse;
            duplicateCourseCode.context = oldCourse.getContext();

            var pattern = "((.+)|(^))([a-zA-Z])(\\d+)$";
            duplicateCourseCode.endsWithNumberPattern = Pattern.compile(pattern);

            return duplicateCourseCode;
        }

        public String duplicateCode() {
            var oldCourseCode = oldCourse.getCode();
            var newCourseCodeEnd = "1";

            var endsWithNumberMatcher = endsWithNumberPattern.matcher(oldCourseCode);
            if (endsWithNumberMatcher.find()) {
                var lastNumberString = endsWithNumberMatcher.group(5);
                if (!StringUtils.isNumeric(lastNumberString)) {
                    throw new IllegalArgumentException();
                }
                oldCourseCode = oldCourseCode.replaceAll(lastNumberString+"$", "");
                var maxEndNumber = Math.max(Integer.parseInt(lastNumberString), getMaxEndNumberForCourseCode(oldCourseCode));
                newCourseCodeEnd = Integer.toString(maxEndNumber + 1);

            } else {
                if (getMaxEndNumberForCourseCode(oldCourseCode) != -1) {
                    newCourseCodeEnd = Integer.toString(getMaxEndNumberForCourseCode(oldCourseCode) + 1);
                }
            }

            if (oldCourseCode.length() + newCourseCodeEnd.length() < Course.COURSE_CODE_MAX_LENGTH) {
                return oldCourseCode + newCourseCodeEnd;
            } else {
                return oldCourseCode.substring(0,Course.COURSE_CODE_MAX_LENGTH - newCourseCodeEnd.length()) + newCourseCodeEnd;
            }
        }

        private int getMaxEndNumberForCourseCode (String courseCode) {
            var courseList = ObjectSelect.query(Course.class)
                    .where(Course.CODE.like(courseCode + "%"))
                    .select(context);
            var maxNumber = -1;
            for (var course : courseList) {
                var endsWithDigitsMather = endsWithNumberPattern.matcher(course.getCode());
                if (endsWithDigitsMather.matches()) {
                    if (maxNumber < Integer.parseInt(endsWithDigitsMather.group(5))) {
                        maxNumber = Integer.parseInt(endsWithDigitsMather.group(5));
                    }
                }
            }
            return maxNumber;
        }
    }
}
