import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID } from "../../../js/constants/Config";
import { GetPreferences } from "../../common/GetPreferences.Epic";

describe("Get default income account preferences epic tests", () => {
  it("should return correct actions", () =>
    GetPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID]));
});
