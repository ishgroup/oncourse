import { ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID } from "../../../js/constants/Config";
import { GetPreferences } from "../../common/GetPreferences.Epic";

describe("Get default voucher liability account preferences epic tests", () => {
  it("should return correct actions", () =>
    GetPreferences([ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID]));
});
