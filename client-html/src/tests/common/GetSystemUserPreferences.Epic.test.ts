import { SYSTEM_USER_ADMINISTRATION_CENTER } from "../../js/constants/Config";
import { GetUserPreferences } from "./GetUserPreferences.Epic";

describe("Get system user preferences epic tests", () => {
  it("[SYSTEM_USER_ADMINISTRATION_CENTER] should return correct actions", () => 
    GetUserPreferences([SYSTEM_USER_ADMINISTRATION_CENTER]));
});
