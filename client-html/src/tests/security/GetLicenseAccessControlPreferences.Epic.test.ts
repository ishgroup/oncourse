import { LICENSE_ACCESS_CONTROL_KEY } from "../../js/constants/Config";
import { GetPreferences } from "../common/GetPreferences.Epic";

describe("Get license access control preferences epic tests", () => {
  it("should return correct actions", () => {
    return GetPreferences([LICENSE_ACCESS_CONTROL_KEY]);
  });
});
