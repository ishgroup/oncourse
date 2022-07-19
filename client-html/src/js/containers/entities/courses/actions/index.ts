/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_COURSE = _toRequestType("get/course");
export const GET_COURSE_FULFILLED = FULFILLED(GET_COURSE);

export const UPDATE_COURSE = _toRequestType("put/course");

export const DUPLICATE_COURSE = _toRequestType("post/duplicate");

export const getCourse = (id: string) => ({
  type: GET_COURSE,
  payload: id
});

export const updateCourse = (id: string, course: Course) => ({
  type: UPDATE_COURSE,
  payload: { id, course }
});

export const duplicateCourses = (ids: number[]) => ({
  type: DUPLICATE_COURSE,
  payload: ids
});