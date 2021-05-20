import { DASHBOARD_THEME_KEY } from "../../js/constants/Config";
import { GetUserPreferences } from "./GetUserPreferences.Epic";

describe("Get theme preferences epic tests", () => {
  it("[DASHBOARD_THEME_KEY] should return correct actions", () =>
    GetUserPreferences([DASHBOARD_THEME_KEY]));
});
