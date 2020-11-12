import { LICENSE_SCRIPTING_KEY } from "../../js/constants/Config";
import { GetPreferences } from "../common/GetPreferences.Epic";

describe("Get license preferences epic tests", () => {
  it("should return correct actions", () => {
    return GetPreferences([LICENSE_SCRIPTING_KEY]);
  });
});
