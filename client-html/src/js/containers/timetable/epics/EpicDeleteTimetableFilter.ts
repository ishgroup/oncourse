/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import {
  clearTimetableMonths,
  DELETE_TIMETABLE_FILTER,
  DELETE_TIMETABLE_FILTER_FULFILLED,
  findTimetableSessions,
  GET_TIMETABLE_FILTERS,
  getTimetableSessionsDays,
  setTimetableSearch
} from "../actions";
import { Session } from "@api/model";
import CustomFiltersService from "../../../common/services/CustomFiltersService";
import { FETCH_SUCCESS } from "../../../common/actions";
import { addMonths, endOfMonth, startOfMonth } from "date-fns";
import { getFiltersString } from "../../../common/components/list-view/utils/listFiltersUtils";

const getActionsForUpdate = (filters, id, currentMonth) => {
  const search = getFiltersString([{ filters: filters.filter(f => f.id !== id) }]);

  const startMonth = startOfMonth(currentMonth);

  const endMonth = endOfMonth(addMonths(startMonth, 1));

  return [
    setTimetableSearch(search),
    clearTimetableMonths(),
    findTimetableSessions({ from: startMonth.toISOString(), to: endMonth.toISOString(), search }),
    getTimetableSessionsDays(currentMonth.getMonth(), currentMonth.getFullYear())
  ];
};

const request: EpicUtils.Request<any,  { id: number; currentMonth: Date }> = {
  type: DELETE_TIMETABLE_FILTER,
  hideLoadIndicator: true,
  getData: ({ id }) => CustomFiltersService.deleteCustomFilter("Session", id),
  processData: (v, { timetable: { filters } }, { id, currentMonth }) => {
    const wasActive = filters.find(f => f.id === id).active;

    return [
      {
        type: DELETE_TIMETABLE_FILTER_FULFILLED
      },
      {
        type: GET_TIMETABLE_FILTERS
      },
      {
        type: FETCH_SUCCESS,
        payload: {
          message: "Custom filter deleted"
        }
      },
      ...(wasActive ? getActionsForUpdate(filters, id, currentMonth) : [])
    ];
  }
};

export const EpicDeleteTimetableFilter: Epic<any, any> = EpicUtils.Create(request);
