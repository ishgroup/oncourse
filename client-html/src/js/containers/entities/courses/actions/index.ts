/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_PLAIN_COURSES = _toRequestType("get/plain/courses");
export const SET_PLAIN_COURSES = "get/plain/courses";

export const CREATE_COURSE = _toRequestType("post/course");

export const GET_COURSE = _toRequestType("get/course");
export const GET_COURSE_FULFILLED = FULFILLED(GET_COURSE);

export const UPDATE_COURSE = _toRequestType("put/course");

export const DELETE_COURSE = _toRequestType("delete/course");

export const DUPLICATE_COURSE = _toRequestType("post/duplicate");

export const SET_PLAIN_COURSES_SEARCH = "set/plain/courses/search";
export const CLEAR_PLAIN_COURSES_SEARCH = "clear/plain/courses/search";

export const createCourse = (course: Course) => ({
  type: CREATE_COURSE,
  payload: { course }
});

export const getCourse = (id: string) => ({
  type: GET_COURSE,
  payload: id
});

export const getPlainCourses = (offset?: number, columns?: string, ascending?: boolean, pageSize?: number) => ({
  type: GET_PLAIN_COURSES,
  payload: {
    offset, columns, ascending, pageSize
  }
});

export const setPlainCoursesSearch = (search: string) => ({
  type: SET_PLAIN_COURSES_SEARCH,
  payload: { search }
});

export const setPlainCourses = (items: Course[], offset?: number, pageSize?: number, loading?: boolean) => ({
  type: SET_PLAIN_COURSES,
  payload: {
    items, offset, pageSize, loading
  }
});

export const updateCourse = (id: string, course: Course) => ({
  type: UPDATE_COURSE,
  payload: { id, course }
});

export const deleteCourse = (id: string) => ({
  type: DELETE_COURSE,
  payload: id
});

export const duplicateCourses = (ids: number[]) => ({
  type: DUPLICATE_COURSE,
  payload: ids
});
