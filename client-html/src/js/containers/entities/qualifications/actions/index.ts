/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Qualification } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_QUALIFICATION_ITEM = _toRequestType("get/qualification");
export const GET_QUALIFICATION_ITEM_FULFILLED = FULFILLED(GET_QUALIFICATION_ITEM);

export const DELETE_QUALIFICATION_ITEM = _toRequestType("delete/qualification");
export const DELETE_QUALIFICATION_ITEM_FULFILLED = FULFILLED(DELETE_QUALIFICATION_ITEM);

export const UPDATE_QUALIFICATION_ITEM = _toRequestType("put/qualification");
export const UPDATE_QUALIFICATION_ITEM_FULFILLED = FULFILLED(UPDATE_QUALIFICATION_ITEM);

export const CREATE_QUALIFICATION_ITEM = _toRequestType("post/qualification");
export const CREATE_QUALIFICATION_ITEM_FULFILLED = FULFILLED(CREATE_QUALIFICATION_ITEM);

export const getQualification = (id: string) => ({
  type: GET_QUALIFICATION_ITEM,
  payload: id
});

export const removeQualification = (id: string) => ({
  type: DELETE_QUALIFICATION_ITEM,
  payload: id
});

export const updateQualification = (id: string, qualification: Qualification) => ({
  type: UPDATE_QUALIFICATION_ITEM,
  payload: { id, qualification }
});

export const createQualification = (qualification: Qualification) => ({
  type: CREATE_QUALIFICATION_ITEM,
  payload: { qualification }
});
