import { DefaultEpic } from "../common/Default.Epic";
import {
  SET_DASHBOARD_FAVORITES_FULFILLED,
  setDashboardFavorites
} from "../../js/containers/dashboard/actions";
import { EpicSetDashboardFavorites } from "../../js/containers/dashboard/epics/EpicSetDashboardFavorites";

describe("Get dashboard favorites epic tests", () => {
  it("SetDashboardFavorites should returns correct values", () => DefaultEpic({
    action: setDashboardFavorites([]),
    epic: EpicSetDashboardFavorites,
    processData: () => [
      {
        type: SET_DASHBOARD_FAVORITES_FULFILLED
      }
    ]
  }));
});
