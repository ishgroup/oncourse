import { Filter, Session } from "@api/model";
import { Epic } from "redux-observable";
import { setActiveFiltersBySearch } from "../../../common/components/list-view/utils/listFiltersUtils";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import CustomFiltersService from "../../../common/services/CustomFiltersService";
import { GET_TIMETABLE_FILTERS, setTimetableFilters } from "../actions";

const request: EpicUtils.Request = {
  type: GET_TIMETABLE_FILTERS,
  hideLoadIndicator: true,
  getData: () => CustomFiltersService.getFilters("Session"),
  processData: (filters: Filter[]) => {

    const params = new URLSearchParams(window.location.search);

    const filtersUrlString = params.get("filter");

    return [setTimetableFilters(setActiveFiltersBySearch(filtersUrlString, [{ filters }])[0].filters)];
  }
};

export const EpicGetTimetableFilters: Epic<any, any> = EpicUtils.Create(request);
