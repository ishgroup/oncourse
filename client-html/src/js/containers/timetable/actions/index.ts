/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Filter, SearchRequest } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import { CoreFilter, SavingFilterState } from "../../../model/common/ListView";
import { TimetableMonth } from "../../../model/timetable";

export const FIND_TIMETABLE_SESSIONS = _toRequestType("find/timetable/session");
export const FIND_TIMETABLE_SESSIONS_FULFILLED = FULFILLED(FIND_TIMETABLE_SESSIONS);

export const GET_TIMETABLE_SESSIONS_DAYS = _toRequestType("get/timetable/session/days");
export const GET_TIMETABLE_SESSIONS_DAYS_FULFILLED = FULFILLED(GET_TIMETABLE_SESSIONS_DAYS);

export const GET_TIMETABLE_SESSIONS_TAGS = _toRequestType("get/timetable/session/tags");
export const GET_TIMETABLE_SESSIONS_TAGS_FULFILLED = FULFILLED(GET_TIMETABLE_SESSIONS_TAGS);

export const GET_TIMETABLE_FILTERS = _toRequestType("get/timetable/filters");
export const SET_TIMETABLE_FILTERS = "set/timetable/filters";

export const POST_TIMETABLE_FILTER = _toRequestType("post/timetable/filter");
export const POST_TIMETABLE_FILTER_FULFILLED = FULFILLED(POST_TIMETABLE_FILTER);

export const DELETE_TIMETABLE_FILTER = _toRequestType("delete/timetable/filter");
export const DELETE_TIMETABLE_FILTER_FULFILLED = FULFILLED(DELETE_TIMETABLE_FILTER);

export const GET_TIMETABLE_SESSIONS_BY_IDS = _toRequestType("get/timetable/session");
export const GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED = FULFILLED(GET_TIMETABLE_SESSIONS_BY_IDS);

export const SET_TIMETABLE_MONTHS = "set/timetable/months";
export const CLEAR_TIMETABLE_MONTHS = "clear/timetable/months";

export const SET_TIMETABLE_SEARCH = "set/timetable/search";
export const SET_TIMETABLE_SEARCH_ERROR = "set/timetable/search/error";
export const SET_TIMETABLE_SAVING_FILTER = "set/timetable/savingFilter";

export const setTimetableFilters = (filters?: CoreFilter[]) => ({
  type: SET_TIMETABLE_FILTERS,
  payload: { filters }
});

export const setTimetableMonths = (months?: TimetableMonth[], loadMore?: boolean) => ({
  type: SET_TIMETABLE_MONTHS,
  payload: { months, loadMore }
});

export const setTimetableSavingFilter = (savingFilter?: SavingFilterState) => ({
  type: SET_TIMETABLE_SAVING_FILTER,
  payload: { savingFilter }
});

export const saveTimetableFilter = (filter: Filter) => ({
  type: POST_TIMETABLE_FILTER,
  payload: filter
});

export const deleteTimetableFilter = (id: number, currentMonth: Date) => ({
  type: DELETE_TIMETABLE_FILTER,
  payload: { id, currentMonth }
});

export const getTimetableFilters = () => ({
  type: GET_TIMETABLE_FILTERS
});

export const clearTimetableMonths = () => ({
  type: CLEAR_TIMETABLE_MONTHS
});

export const setTimetableSearchError = (searchError: boolean) => ({
  type: SET_TIMETABLE_SEARCH_ERROR,
  payload: { searchError }
});

export const setTimetableSearch = (search: string) => ({
  type: SET_TIMETABLE_SEARCH,
  payload: { search }
});

export const findTimetableSessions = (request: SearchRequest) => ({
  type: FIND_TIMETABLE_SESSIONS,
  payload: { request }
});

export const getTimetableSessionsDays = (month: number, year: number) => ({
  type: GET_TIMETABLE_SESSIONS_DAYS,
  payload: { month, year }
});

export const getTimetableSessionsByIds = (ids: number[], monthIndex?: number, dayIndex?: number) => ({
  type: GET_TIMETABLE_SESSIONS_BY_IDS,
  payload: { ids, monthIndex, dayIndex }
});

export const getTimetableSessionsTags = (ids: number[], monthIndex: number, dayIndex: number) => ({
  type: GET_TIMETABLE_SESSIONS_TAGS,
  payload: { ids, monthIndex, dayIndex }
});
