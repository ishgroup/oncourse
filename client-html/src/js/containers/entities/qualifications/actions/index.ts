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

export const GET_PLAIN_QUALIFICATION_ITEMS = _toRequestType("get/qualification/plain");
export const GET_PLAIN_QUALIFICATION_ITEMS_FULFILLED = FULFILLED(GET_PLAIN_QUALIFICATION_ITEMS);

export const CLEAR_PLAIN_QUALIFICATION_ITEMS = "clear/qualification/plain";

export const SET_PLAIN_QUALIFICATION_ITEMS_SEARCH = "set/qualification/plain/search";

export const getPlainQualifications = (offset?: number, sortings?: string, ascending?: boolean, pageSize?: number) => ({
  type: GET_PLAIN_QUALIFICATION_ITEMS,
  payload: {
   offset, sortings, ascending, pageSize
  }
});

export const clearPlainQualificationItems = (loading?: boolean) => ({
  type: CLEAR_PLAIN_QUALIFICATION_ITEMS,
  payload: { loading }
});

export const setPlainQualificationSearch = (search: string) => ({
  type: SET_PLAIN_QUALIFICATION_ITEMS_SEARCH,
  payload: { search }
});

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
