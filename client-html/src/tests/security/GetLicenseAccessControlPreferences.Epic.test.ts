import { LICENSE_ACCESS_CONTROL_KEY } from "../../js/constants/Config";
import { GetUserPreferences } from "../common/GetUserPreferences.Epic";

describe("Get license access control preferences epic tests", () => {
  it("should return correct actions", () =>
    GetUserPreferences([LICENSE_ACCESS_CONTROL_KEY]));
});
