/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { SearchRequest, Session } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { FIND_TIMETABLE_SESSIONS, FIND_TIMETABLE_SESSIONS_FULFILLED, setTimetableSearchError } from "../actions";
import TimetableService from "../services/TimetableService";
import { getMonthsWithinYear } from "../utils";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getFiltersString } from "../../../common/components/list-view/utils/listFiltersUtils";

const request: EpicUtils.Request<Session[], { request: SearchRequest }> = {
  type: FIND_TIMETABLE_SESSIONS,
  getData: ({ request }, { timetable: { search, filters } }) => {
    request.search = search;
    request.filter = getFiltersString([{ filters }]);
    return TimetableService.findTimetableSessions(request);
  },
  processData: (sessions, s, { request: { from } }) => {
    const months = sessions.length ? getMonthsWithinYear(sessions, new Date(from)) : [];

    return [
      {
        type: FIND_TIMETABLE_SESSIONS_FULFILLED,
        payload: { months }
      },
      ...(s.timetable.searchError ? [setTimetableSearchError(false)] : [])
    ];
  },
  processError: response => {
    if (response && response.status === 400 && response.data.errorMessage.includes("Invalid search expression")) {
      return [
        setTimetableSearchError(true),
        {
          type: FIND_TIMETABLE_SESSIONS_FULFILLED,
          payload: { months: [] }
        }
      ];
    }
    return FetchErrorHandler(response);
  }
};

export const EpicFindTimetableSessions: Epic<any, any> = EpicUtils.Create(request);
