import { DEFAULT_TIMEZONE_KEY } from "../../../js/constants/Config";
import { GetUserPreferences } from "../../common/GetUserPreferences.Epic";

describe("Get default timezone preferences epic tests", () => {
  it("should return correct actions", () =>
    GetUserPreferences([DEFAULT_TIMEZONE_KEY]));
});
