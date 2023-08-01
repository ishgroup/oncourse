/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentClass } from "@api/model";
import { _toRequestType } from "../../../../../../common/actions/ActionUtils";

export const GET_COURSE_CLASS_ASSESSMENTS = _toRequestType("get/courseClass/assesstments");

export const CREATE_COURSE_CLASS_ASSESSMENT = _toRequestType("post/courseClass/assesstments");

export const UPDATE_COURSE_CLASS_ASSESSMENT = _toRequestType("put/courseClass/assesstments");

export const DELETE_COURSE_CLASS_ASSESSMENT = _toRequestType("delete/courseClass/assesstments");

export const createCourseClassAssessment = (assessment: AssessmentClass) => ({
  type: CREATE_COURSE_CLASS_ASSESSMENT,
  payload: { assessment }
});

export const updateCourseClassAssessment = (assessment: AssessmentClass) => ({
  type: UPDATE_COURSE_CLASS_ASSESSMENT,
  payload: { assessment }
});

export const deleteCourseClassAssessment = (id: number) => ({
  type: DELETE_COURSE_CLASS_ASSESSMENT,
  payload: id
});
