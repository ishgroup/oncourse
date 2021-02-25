/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Assessment } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_ASSESSMENT_ITEM = _toRequestType("get/assessment");
export const GET_ASSESSMENT_ITEM_FULFILLED = FULFILLED(GET_ASSESSMENT_ITEM);

export const DELETE_ASSESSMENT_ITEM = _toRequestType("delete/assessment");
export const DELETE_ASSESSMENT_ITEM_FULFILLED = FULFILLED(DELETE_ASSESSMENT_ITEM);

export const UPDATE_ASSESSMENT_ITEM = _toRequestType("put/assessment");
export const UPDATE_ASSESSMENT_ITEM_FULFILLED = FULFILLED(UPDATE_ASSESSMENT_ITEM);

export const CREATE_ASSESSMENT_ITEM = _toRequestType("post/assessment");
export const CREATE_ASSESSMENT_ITEM_FULFILLED = FULFILLED(CREATE_ASSESSMENT_ITEM);

export const getAssessment = (id: string) => ({
  type: GET_ASSESSMENT_ITEM,
  payload: id
});

export const removeAssessment = (id: string) => ({
  type: DELETE_ASSESSMENT_ITEM,
  payload: id
});

export const updateAssessment = (id: string, assessment: Assessment) => ({
  type: UPDATE_ASSESSMENT_ITEM,
  payload: { id, assessment }
});

export const createAssessment = (assessment: Assessment) => ({
  type: CREATE_ASSESSMENT_ITEM,
  payload: { assessment }
});
