/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { isSameDay } from "date-fns";
import { IAction } from "../../../common/actions/IshAction";
import { TimetableState } from "../../../model/timetable";
import {
  CLEAR_TIMETABLE_MONTHS,
  FIND_TIMETABLE_SESSIONS,
  FIND_TIMETABLE_SESSIONS_FULFILLED,
  GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED,
  GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
  GET_TIMETABLE_SESSIONS_TAGS_FULFILLED,
  SET_TIMETABLE_FILTERS,
  SET_TIMETABLE_MONTHS,
  SET_TIMETABLE_SAVING_FILTER,
  SET_TIMETABLE_SEARCH,
  SET_TIMETABLE_SEARCH_ERROR
} from "../actions";

const TimetableInitialState: TimetableState = {
  months: [],
  selectedMonthSessionDays: [],
  filters: [],
  filtersLoading: true,
  sessionsLoading: false,
  search: "",
  searchError: false,
  savingFilter: null
};

export const timetableReducer = (
  state: TimetableState = TimetableInitialState,
  action: IAction<any>
): TimetableState => {
  switch (action.type) {
    case SET_TIMETABLE_MONTHS: {
      const { months, loadMore } = action.payload;
      
      return {
        ...state,
        months: loadMore ? state.months.concat(months) : months,
      };
    }

    case FIND_TIMETABLE_SESSIONS: {
      return {
        ...state,
        sessionsLoading: true
      };
    }
    
    case FIND_TIMETABLE_SESSIONS_FULFILLED: {
      const { months } = action.payload;
      const startIndex = months.length ? state.months.findIndex(m => isSameDay(m.month, months[0].month)) : -1;
      const updated = [...state.months];
      updated.splice(startIndex, months.length, ...months);

      return {
        ...state,
        months: updated,
        sessionsLoading: false
      };
    }

    case GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED: {
      const { sessions, monthIndex, dayIndex } = action.payload;

      return {
        ...state,
        months: state.months.map((m, i) => {
          if (i === monthIndex) {
            return {
              ...m,
              days: m.days.map((d, dIn) => {
                if (dIn === dayIndex) {
                  return {
                    ...d,
                    updated: true,
                    sessions
                  };
                }
                return d;
              })
            };
          }
          return m;
        })
      };
    }

    case GET_TIMETABLE_SESSIONS_TAGS_FULFILLED: {
      const { tags, monthIndex, dayIndex } = action.payload;

      return {
        ...state,
        months: state.months.map((m, i) => {
          if (i === monthIndex) {
            return {
              ...m,
              days: m.days.map((d, dIn) => {
                if (dIn === dayIndex) {
                  return {
                    ...d,
                    tagsUpdated: true,
                    sessions: d.sessions.map((s, sIn) => ({
                      ...s,
                      tags: tags[sIn]
                    }))
                  };
                }
                return d;
              })
            };
          }
          return m;
        })
      };
    }

    case SET_TIMETABLE_SEARCH:
    case SET_TIMETABLE_SEARCH_ERROR:
    case SET_TIMETABLE_SAVING_FILTER:
    case GET_TIMETABLE_SESSIONS_DAYS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case SET_TIMETABLE_FILTERS: {
      return {
        ...state,
        filtersLoading: false,
        filters: action.payload.filters
      };
    }

    case CLEAR_TIMETABLE_MONTHS: {
      return {
        ...state,
        months: []
      };
    }

    default:
      return state;
  }
};
