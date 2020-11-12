/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../../../common/actions/ActionUtils";
import { TrainingPlan, StudentAttendance, TutorAttendance } from "@api/model";

export const GET_COURSE_CLASS_ATTENDANCE = _toRequestType("get/courseClass/attendance");

export const UPDATE_COURSE_CLASS_STUDENT_ATTENDANCE = _toRequestType("post/courseClass/studentAttendance");

export const UPDATE_COURSE_CLASS_TUTOR_ATTENDANCE = _toRequestType("post/courseClass/tutorAttendance");

export const UPDATE_COURSE_CLASS_TRAINING_PLANS = _toRequestType("post/courseClass/trainingPlans");

export const updateCourseClassStudentAttendance = (id: number, studentAttendance: StudentAttendance[]) => ({
  type: UPDATE_COURSE_CLASS_STUDENT_ATTENDANCE,
  payload: { id, studentAttendance }
});

export const updateCourseClassTutorAttendance = (id: number, tutorAttendance: TutorAttendance[]) => ({
  type: UPDATE_COURSE_CLASS_TUTOR_ATTENDANCE,
  payload: { id, tutorAttendance }
});

export const updateCourseClassTrainingPlans = (id: number, trainingPlans: TrainingPlan[]) => ({
  type: UPDATE_COURSE_CLASS_TRAINING_PLANS,
  payload: { id, trainingPlans }
});
