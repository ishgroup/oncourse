import { DEFAULT_TIMEZONE_KEY } from "../../../js/constants/Config";
import { GetPreferences } from "../../common/GetPreferences.Epic";

describe("Get default timezone preferences epic tests", () => {
  it("should return correct actions", () =>
    GetPreferences([DEFAULT_TIMEZONE_KEY]));
});
