/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  CourseClassDuplicate,
  Session,
  CancelCourseClass,
  CourseClass, SessionWarning, ClassCost
} from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { CourseClassState } from "../reducers";

export const GET_COURSE_CLASS = _toRequestType("get/courseClass");

export const UPDATE_COURSE_CLASS = _toRequestType("put/courseClass");

export const CREATE_COURSE_CLASS = _toRequestType("post/courseClass");

export const DELETE_COURSE_CLASS = _toRequestType("delete/courseClass");

export const DUPLICATE_COURSE_CLASS = _toRequestType("get/courseClass/duplicate");
export const DUPLICATE_COURSE_CLASS_FULFILLED = FULFILLED(DUPLICATE_COURSE_CLASS);

export const GET_DUPLICATE_COURSE_CLASSES_SESSIONS = _toRequestType("get/duplicateCourseClass/sessions");
export const SET_DUPLICATE_COURSE_CLASSES_SESSIONS = "set/duplicateCourseClass/sessions";

export const GET_DUPLICATE_COURSE_CLASSES_BUDGET = _toRequestType("get/duplicateCourseClass/budget");
export const SET_DUPLICATE_COURSE_CLASSES_BUDGET = "set/duplicateCourseClass/budget";

export const CLEAR_DUPLICATE_COURSE_CLASSES_SESSIONS = "clear/duplicateCourseClass/sessions";

export const GET_COURSE_CLASS_TAGS = _toRequestType("get/courseClass/tags");

export const GET_COURSE_CLASS_SESSIONS_WARNINGS = _toRequestType("get/courseClass/sessionWarnings");
export const SET_COURSE_CLASS_SESSIONS_WARNINGS = "set/courseClass/sessionWarnings";

export const GET_COURSE_CLASS_ENROLMENTS = _toRequestType("get/courseClass/enrolments");
export const SET_COURSE_CLASS_ENROLMENTS = "set/courseClass/enrolments";

export const CANCEL_COURSE_CLASS = _toRequestType("cancel/courseClass");
export const CANCEL_COURSE_CLASS_FULFILLED = FULFILLED(CANCEL_COURSE_CLASS);

export const SET_COURSE_CLASS_LATEST_SESSION = "set/courseClass/latestSession";

export const SET_COURSE_CLASS_BUDGET_MODAL_OPENED = "set/courseClass/budget/modal";

export const duplicateCourseClass = (values: CourseClassDuplicate, onComplete?: any) => ({
  type: DUPLICATE_COURSE_CLASS,
  payload: { values, onComplete }
});

export const getDuplicateCourseClassesSessions = (selection: string[]) => ({
  type: GET_DUPLICATE_COURSE_CLASSES_SESSIONS,
  payload: selection
});

export const clearDuplicateCourseClassesSessions = () => ({
  type: CLEAR_DUPLICATE_COURSE_CLASSES_SESSIONS
});

export const setDuplicateCourseClassesSessions = (sessions: Session[], earliest, hasZeroWages?: boolean) => ({
  type: SET_DUPLICATE_COURSE_CLASSES_SESSIONS,
  payload: { sessions, earliest, hasZeroWages }
});

export const getCourseClass = (id: string) => ({
  type: GET_COURSE_CLASS,
  payload: id
});

export const updateCourseClass = (id: number, courseClass: CourseClass) => ({
  type: UPDATE_COURSE_CLASS,
  payload: { id, courseClass }
});

export const deleteCourseClass = (id: number) => ({
  type: DELETE_COURSE_CLASS,
  payload: { id }
});

export const createCourseClass = (courseClass: CourseClass) => ({
  type: CREATE_COURSE_CLASS,
  payload: { courseClass }
});

export const getCourseClassEnrolments = (id: number) => ({
  type: GET_COURSE_CLASS_ENROLMENTS,
  payload: id
});

export const setCourseClassEnrolments = (enrolments: CourseClassState["enrolments"]) => ({
  type: SET_COURSE_CLASS_ENROLMENTS,
  payload: { enrolments }
});

export const cancelCourseClass = (values: CancelCourseClass) => ({
  type: CANCEL_COURSE_CLASS,
  payload: values
});

export const getCourseClassTags = () => ({
  type: GET_COURSE_CLASS_TAGS
});

export const setCourseClassLatestSession = (latestSession: Date) => ({
  type: SET_COURSE_CLASS_LATEST_SESSION,
  payload: { latestSession }
});

export const setCourseClassBudgetModalOpened = (opened: boolean, onCostRate?: number) => ({
  type: SET_COURSE_CLASS_BUDGET_MODAL_OPENED,
  payload: { opened, onCostRate }
});

export const getCourseClassSessionsWarnings = (classId: number, sessions: Session[]) => ({
  type: GET_COURSE_CLASS_SESSIONS_WARNINGS,
  payload: { classId, sessions }
});

export const setCourseClassSessionsWarnings = (sessionWarnings?: SessionWarning[]) => ({
  type: SET_COURSE_CLASS_SESSIONS_WARNINGS,
  payload: { sessionWarnings }
});

export const getDuplicateCourseClassesBudget = (id: number) => ({
  type: GET_DUPLICATE_COURSE_CLASSES_BUDGET,
  payload: id
});

export const setDuplicateCourseClassesBudget = (budget: ClassCost[]) => ({
  type: SET_DUPLICATE_COURSE_CLASSES_BUDGET,
  payload: budget
});

