import { DASHBOARD_THEME_KEY } from "../../js/constants/Config";
import { GetPreferences } from "./GetPreferences.Epic";

describe("Get theme preferences epic tests", () => {
  it("[DASHBOARD_THEME_KEY] should return correct actions", () =>
    GetPreferences([DASHBOARD_THEME_KEY]));
});
