import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_TIMETABLE_SESSIONS_TAGS, GET_TIMETABLE_SESSIONS_TAGS_FULFILLED } from "../actions";
import TimetableService from "../services/TimetableService";

const request: EpicUtils.Request<
  { [key: string]: string }[],
  { ids: number[]; monthIndex: number; dayIndex: number }
> = {
  type: GET_TIMETABLE_SESSIONS_TAGS,
  hideLoadIndicator: true,
  getData: ({ ids }) => TimetableService.getSessionTags(ids),
  processData: (tags, s, { monthIndex, dayIndex }) => [
      {
        type: GET_TIMETABLE_SESSIONS_TAGS_FULFILLED,
        payload: { tags, monthIndex, dayIndex }
      }
    ]
};

export const EpicGetSessionTags: Epic<any, any> = EpicUtils.Create(request);
