/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "./ActionUtils";

export const SET_COMMON_PLAIN_RECORD_SEARCH = "set/common/plain/record/search";
export const CLEAR_COMMON_PLAIN_RECORDS = "clear/common/plain/records";
export const GET_COMMON_PLAIN_RECORDS = _toRequestType("get/common/plain/records");
export const GET_COMMON_PLAIN_RECORDS_FULFILLED = FULFILLED(GET_COMMON_PLAIN_RECORDS);

export const setCommonPlainSearch = (
  key: string,
  search: string
) => ({
  type: SET_COMMON_PLAIN_RECORD_SEARCH,
  payload: {
   key, search
  }
});

export const getCommonPlainRecords = (
  key: string,
  offset?: number,
  columns?: string,
  ascending?: boolean,
  sort?: string,
  pageSize?: number
) => ({
  type: GET_COMMON_PLAIN_RECORDS,
  payload: {
   key, offset, columns, ascending, sort, pageSize
  }
});

export const clearCommonPlainRecords = (key: string, loading?: boolean) => ({
  type: CLEAR_COMMON_PLAIN_RECORDS,
  payload: { key, loading }
});

