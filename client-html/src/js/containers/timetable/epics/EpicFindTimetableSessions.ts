/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { SearchRequest, Session } from "@api/model";
import { parseISO } from "date-fns";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getFiltersString } from "../../../common/components/list-view/utils/listFiltersUtils";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { TimetableMonth } from "../../../model/timetable";
import {
  FIND_TIMETABLE_SESSIONS,
  findTimetableSessionsFulfilled,
  setTimetableSearchError
} from "../actions";
import TimetableService from "../services/TimetableService";
import { getMonthsWithinYear } from "../utils";

const appendMonths = (updatedMonths: TimetableMonth[], stateMonths: TimetableMonth[]) => {
  const updated = [];
  
  for (let i = 0; i < 12; i++) {
    stateMonths.forEach(sm => {
      if (sm.month.getMonth() === i) updated[i] = sm; 
    });
    updatedMonths.forEach(um => {
      if (um.month.getMonth() === i) updated[i] = um;
    });
  }

  return updated.filter(u => u);
};

const request: EpicUtils.Request<Session[], { request: SearchRequest, reset: boolean }> = {
  type: FIND_TIMETABLE_SESSIONS,
  getData: ({ request }, { timetable: { search, filters } }) => {
    request.search = search;
    request.filter = getFiltersString([{ filters }]);
    return TimetableService.findTimetableSessions(request);
  },
  processData: (sessions, s, { request: { from }, reset }) => {

    const updatedMonths = getMonthsWithinYear(sessions, parseISO(from));

    const months = reset ? updatedMonths : appendMonths(updatedMonths as any, s.timetable.months);

    return [
      findTimetableSessionsFulfilled(months),
      ...(s.timetable.searchError ? [setTimetableSearchError(false)] : [])
    ];
  },
  processError: response => {
    if (response && response.status === 400 && response.data.errorMessage.includes("Invalid search expression")) {
      return [
        setTimetableSearchError(true),
        findTimetableSessionsFulfilled([])
      ];
    }
    return FetchErrorHandler(response);
  }
};

export const EpicFindTimetableSessions: Epic<any, any> = EpicUtils.Create(request);
