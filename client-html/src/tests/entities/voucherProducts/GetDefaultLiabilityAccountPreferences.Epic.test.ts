import { ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID } from "../../../js/constants/Config";
import { GetUserPreferences } from "../../common/GetUserPreferences.Epic";

describe("Get default voucher liability account preferences epic tests", () => {
  it("should return correct actions", () =>
    GetUserPreferences([ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID]));
});
