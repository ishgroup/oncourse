import { Epic } from "redux-observable";
import { SearchRequest, Session } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { FIND_TIMETABLE_SESSIONS, FIND_TIMETABLE_SESSIONS_FULFILLED, setTimetableSearchError } from "../actions";
import TimetableService from "../services/TimetableService";
import { getMonthsWithinYear } from "../utils";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<Session[], { request: SearchRequest }> = {
  type: FIND_TIMETABLE_SESSIONS,
  getData: ({ request }) => TimetableService.findTimetableSessions(request),
  processData: (sessions, s, { request: { from } }) => {
    const months = getMonthsWithinYear(sessions, new Date(from));

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
