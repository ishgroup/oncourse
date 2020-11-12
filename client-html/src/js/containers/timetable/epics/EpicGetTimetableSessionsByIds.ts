import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED, GET_TIMETABLE_SESSIONS_BY_IDS } from "../actions";
import { Session } from "@api/model";
import TimetableService from "../services/TimetableService";

const request: EpicUtils.Request<Session[], any, { ids: number[]; monthIndex?: number; dayIndex?: number }> = {
  type: GET_TIMETABLE_SESSIONS_BY_IDS,
  hideLoadIndicator: true,
  getData: payload => TimetableService.getTimetableSessionsByIds(payload.ids),
  processData: (sessions, s, { monthIndex, dayIndex }) => {
    return [
      {
        type: GET_TIMETABLE_SESSIONS_BY_IDS_FULFILLED,
        payload: { sessions, monthIndex, dayIndex }
      }
    ];
  }
};

export const EpicGetTimetableSessionsByIds: Epic<any, any> = EpicUtils.Create(request);
