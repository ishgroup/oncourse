import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_DASHBOARD_CATEGORIES_FULFILLED,
  getDashboardCategories
} from "../../js/containers/dashboard/actions";
import { EpicGetDashboardCategories } from "../../js/containers/dashboard/epics/EpicGetDashboardCategories";
import { getMainRouteUrl } from "../../js/routes/routesMapping";

describe("Get dashboard categories epic tests", () => {
  it("GetDashboardCategories should returns correct values", () => DefaultEpic({
    action: getDashboardCategories(),
    epic: EpicGetDashboardCategories,
    processData: mockedApi => {
      const links = mockedApi.db.getCategories();
      links.categories.forEach(c => {
        if (!c.url) {
          c.url = getMainRouteUrl(c.category);
        }
      });

      return [
        {
          type: GET_DASHBOARD_CATEGORIES_FULFILLED,
          payload: links
        }
      ];
    }
  }));
});
