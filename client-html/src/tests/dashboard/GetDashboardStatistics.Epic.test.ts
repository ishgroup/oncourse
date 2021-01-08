import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_DASHBOARD_STATISTIC_FULFILLED,
  getDashboardStatistic
} from "../../js/containers/dashboard/actions";
import { EpicGetDashboardStatistics } from "../../js/containers/dashboard/epics/EpicGetDashboardStatistics";

describe("Get dashboard statistics epic tests", () => {
  it("GetDashboardStatistics should returns correct values", () => DefaultEpic({
    action: getDashboardStatistic(),
    epic: EpicGetDashboardStatistics,
    processData: mockedApi => [
      {
        type: GET_DASHBOARD_STATISTIC_FULFILLED,
        payload: { statistics: { updating: false, data: mockedApi.db.getStatistic() } }
      }
    ]
  }));
});
