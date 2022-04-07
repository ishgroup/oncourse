/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import {
  GET_TIMETABLE_SESSIONS_DAYS,
  GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
  setTimetableSearchError
} from "../actions";
import TimetableService from "../services/TimetableService";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getFiltersString } from "../../../common/components/list-view/utils/listFiltersUtils";

const request: EpicUtils.Request<number[], { month: number; year: number }> = {
  type: GET_TIMETABLE_SESSIONS_DAYS,
  hideLoadIndicator: true,
  getData: ({ month, year }, { timetable: { search, filters } }) =>
    TimetableService.getTimetableSessionsDays(month, year, { search, filter: getFiltersString([{ filters }]) } ),
  processData: (selectedMonthSessionDays, s) => [
      {
        type: GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
        payload: { selectedMonthSessionDays }
      },
      ...(s.timetable.searchError ? [setTimetableSearchError(false)] : [])
    ],
  processError: response => {
    if (response && response.status === 400 && response.data.errorMessage.includes("Invalid search expression")) {
      return [
        setTimetableSearchError(true),
        {
          type: GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
          payload: { selectedMonthSessionDays: [] }
        }
      ];
    }
    return FetchErrorHandler(response);
  }
};

export const EpicGetTimetableSessionsDays: Epic<any, any> = EpicUtils.Create(request);
