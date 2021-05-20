import { Epic } from "redux-observable";
import { Session } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_TIMETABLE_SESSIONS_BY_IDS, GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED } from "../actions";
import TimetableService from "../services/TimetableService";

const request: EpicUtils.Request<Session[], { ids: number[]; monthIndex?: number; dayIndex?: number }> = {
  type: GET_TIMETABLE_SESSIONS_BY_IDS,
  hideLoadIndicator: true,
  getData: payload => TimetableService.getTimetableSessionsByIds(payload.ids),
  processData: (sessions, s, { monthIndex, dayIndex }) => [
      {
        type: GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED,
        payload: { sessions, monthIndex, dayIndex }
      }
    ]
};

export const EpicGetTimetableSessionsByIds: Epic<any, any> = EpicUtils.Create(request);
