import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import {
  GET_TIMETABLE_FILTERS,
  POST_TIMETABLE_FILTER,
  POST_TIMETABLE_FILTER_FULFILLED,
  SET_TIMETABLE_SAVING_FILTER
} from "../actions";
import { Session } from "@api/model";
import CustomFiltersService from "../../../common/services/CustomFiltersService";
import { FETCH_SUCCESS } from "../../../common/actions";

const request: EpicUtils.Request = {
  type: POST_TIMETABLE_FILTER,
  hideLoadIndicator: true,
  getData: filter => CustomFiltersService.saveCustomFilter(filter, "Session"),
  processData: () => {
    return [
      {
        type: POST_TIMETABLE_FILTER_FULFILLED
      },
      {
        type: GET_TIMETABLE_FILTERS
      },
      {
        type: SET_TIMETABLE_SAVING_FILTER,
        payload: { savingFilter: null }
      },
      {
        type: FETCH_SUCCESS,
        payload: {
          message: "Custom filter successfully created"
        }
      }
    ];
  }
};

export const EpicPostTimetableFilter: Epic<any, any> = EpicUtils.Create(request);
