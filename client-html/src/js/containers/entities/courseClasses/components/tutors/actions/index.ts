/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor } from '@api/model';
import { StringKeyAndValueObject } from 'ish-ui';
import { _toRequestType } from '../../../../../../common/actions/ActionUtils';
import { IAction } from '../../../../../../common/actions/IshAction';

export const GET_COURSE_CLASS_TUTORS_WARNINGS = _toRequestType("get/courseClass/tutors/warnings");

export const PUT_COURSE_CLASS_TUTOR = _toRequestType("put/courseClass/tutors");

export const POST_COURSE_CLASS_TUTOR = _toRequestType("post/courseClass/tutors");

export const DELETE_COURSE_CLASS_TUTOR = _toRequestType("delete/courseClass/tutors");

export const SET_COURSE_CLASS_TUTOR_NAMES_WARNINGS = "set/courseClass/tutor/name/warnings";

export const getCourseClassTutorsWarnings = (ids: string): IAction => ({
  type: GET_COURSE_CLASS_TUTORS_WARNINGS,
  payload: ids
});

export const putCourseClassTutor = (tutor: CourseClassTutor) => ({
  type: PUT_COURSE_CLASS_TUTOR,
  payload: { tutor }
});

export const postCourseClassTutor = (tutor: CourseClassTutor) => ({
  type: POST_COURSE_CLASS_TUTOR,
  payload: { tutor }
});

export const deleteCourseClassTutor = (id: number) => ({
  type: DELETE_COURSE_CLASS_TUTOR,
  payload: id
});

export const setCourseClassTutorNamesWarnings = (tutorNamesWarnings: StringKeyAndValueObject) => ({
  type: SET_COURSE_CLASS_TUTOR_NAMES_WARNINGS,
  payload: { tutorNamesWarnings }
});
