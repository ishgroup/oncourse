import { DefaultEpic } from "../common/Default.Epic";
import { EpicGetDashboardSearch } from "../../js/containers/dashboard/epics/EpicGetDashboardSearch";
import { GET_DASHBOARD_SEARCH_FULFILLED, getDashboardSearch } from "../../js/containers/dashboard/actions";

describe("Get dashboard search epic tests", () => {
  it("GetDashboardSearch should returns correct values", () => DefaultEpic({
    action: getDashboardSearch("test"),
    epic: EpicGetDashboardSearch,
    processData: mockedApi => [
      {
        type: GET_DASHBOARD_SEARCH_FULFILLED,
        payload: {
          searchResults: {
            updating: false,
            results: mockedApi.db.getDashboardSearchResult()
          }
        }
      }
    ]
  }));
});
