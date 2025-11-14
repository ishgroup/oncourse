/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost } from '@api/model';
import { _toRequestType } from '../../../../../../common/actions/ActionUtils';

export const GET_COURSE_CLASS_COSTS = _toRequestType("get/courseClass/costs");

export const PUT_COURSE_CLASS_COST = _toRequestType("put/courseClass/cost");

export const POST_COURSE_CLASS_COST = _toRequestType("post/courseClass/cost");

export const DELETE_COURSE_CLASS_COST = _toRequestType("delete/courseClass/cost");

export const putCourseClassCost = (cost: ClassCost) => ({
  type: PUT_COURSE_CLASS_COST,
  payload: { cost }
});

export const postCourseClassCost = (cost: ClassCost) => ({
  type: POST_COURSE_CLASS_COST,
  payload: { cost }
});

export const deleteCourseClassCost = (id: number) => ({
  type: DELETE_COURSE_CLASS_COST,
  payload: id
});

export const getCourseClassCosts = (id: number) => ({
  type: GET_COURSE_CLASS_COSTS,
  payload: id
});
