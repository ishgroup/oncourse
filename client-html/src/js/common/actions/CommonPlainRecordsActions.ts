/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CommonPlainSearchEntity } from "../reducers/commonPlainRecordSearchReducer";
import { _toRequestType, FULFILLED } from "./ActionUtils";
import { State } from "../../reducers/state";

export const SET_COMMON_PLAIN_RECORD_SEARCH = "set/common/plain/record/search";
export const GET_COMMON_PLAIN_RECORDS = _toRequestType("get/common/plain/records");
export const GET_COMMON_PLAIN_RECORDS_FULFILLED = FULFILLED(GET_COMMON_PLAIN_RECORDS);

export const GET_COMMON_PLAIN_RECORDS_ACTIONS_FULFILLED = FULFILLED("get/common/plain/records/actions");

export const setCommonPlainSearch = (
  key: string,
  search: string,
  entity?: string,
  actions?: (items: any[], offset?: number, pageSize?: number) => any
) => ({
  type: SET_COMMON_PLAIN_RECORD_SEARCH,
  payload: {
   key, search, entity, actions
  }
});

export const getCommonPlainRecords = (
  key: string,
  offset?: number,
  columns?: string,
  ascending?: boolean,
  sort?: string
) => ({
  type: GET_COMMON_PLAIN_RECORDS,
  payload: {
   key, offset, columns, ascending, sort
  }
});

export const clearCommonPlainSearch = (key: string) => ({
  type: SET_COMMON_PLAIN_RECORD_SEARCH,
  payload: { key, search: "" }
});

export const getCommonPlainRecordFromState = (state: State, key: string): CommonPlainSearchEntity => {
  const getRecord = state.plainSearchRecords[key];
  if (getRecord) return getRecord;
  return state.plainSearchRecords[""];
};
