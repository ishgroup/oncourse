import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID } from "../../../js/constants/Config";
import { GetUserPreferences } from "../../common/GetUserPreferences.Epic";

describe("Get default income account preferences epic tests", () => {
  it("should return correct actions", () =>
    GetUserPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID]));
});
