import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import {
  GET_TIMETABLE_SESSIONS_DAYS,
  GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
  setTimetableSearchError
} from "../actions";
import TimetableService from "../services/TimetableService";
import { State } from "../../../reducers/state";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<number[], State, { month: number; year: number; search: string }> = {
  type: GET_TIMETABLE_SESSIONS_DAYS,
  hideLoadIndicator: true,
  getData: ({ month, year }, { timetable: { search } }) =>
    TimetableService.getTimetableSessionsDays(month, year, search),
  processData: (selectedMonthSessionDays, s) => {
    return [
      {
        type: GET_TIMETABLE_SESSIONS_DAYS_FULFILLED,
        payload: { selectedMonthSessionDays }
      },
      ...(s.timetable.searchError ? [setTimetableSearchError(false)] : [])
    ];
  },
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
