import { Filter, Session } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import CustomFiltersService from "../../../common/services/CustomFiltersService";
import { GET_TIMETABLE_FILTERS, SET_TIMETABLE_FILTERS } from "../actions";

const request: EpicUtils.Request = {
  type: GET_TIMETABLE_FILTERS,
  hideLoadIndicator: true,
  getData: () => CustomFiltersService.getFilters("Session"),
  processData: (filters: Filter[]) => {
    return [
      {
        type: SET_TIMETABLE_FILTERS,
        payload: { filters: filters.map(f => ({ ...f, active: false })) }
      }
    ];
  }
};

export const EpicGetTimetableFilters: Epic<any, any> = EpicUtils.Create(request);
