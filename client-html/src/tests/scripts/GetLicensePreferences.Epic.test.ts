import { LICENSE_SCRIPTING_KEY } from "../../js/constants/Config";
import { GetUserPreferences } from "../common/GetUserPreferences.Epic";

describe("Get license preferences epic tests", () => {
  it("should return correct actions", () =>
    GetUserPreferences([LICENSE_SCRIPTING_KEY]));
});
