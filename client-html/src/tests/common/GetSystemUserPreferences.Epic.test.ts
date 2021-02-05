import { SYSTEM_USER_ADMINISTRATION_CENTER } from "../../js/constants/Config";
import { GetPreferences } from "./GetPreferences.Epic";

describe("Get system user preferences epic tests", () => {
  it("[SYSTEM_USER_ADMINISTRATION_CENTER] should return correct actions", () => 
    GetPreferences([SYSTEM_USER_ADMINISTRATION_CENTER]));
});
