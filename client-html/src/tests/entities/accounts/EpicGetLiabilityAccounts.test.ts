import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_LIABILITY_ACCOUNTS_FULFILLED,
  getLiabilityAccounts
} from "../../../js/containers/entities/accounts/actions";
import { EpicGetLiabilityAccounts } from "../../../js/containers/entities/accounts/epics/EpicGetEnabledAccounts";
import { accountParseResponse } from "./EpicGetIncomeAccounts.test";

describe("Get enabled liability accounts epic tests", () => {
  it("EpicGetLiabilityAccounts should returns correct values", () => DefaultEpic({
    action: getLiabilityAccounts(),
    epic: EpicGetLiabilityAccounts,
    processData: mockedApi => {
      const records = mockedApi.db.getPlainAccounts();
      return [
        {
          type: GET_LIABILITY_ACCOUNTS_FULFILLED,
          payload: { liabilityItems: accountParseResponse(records) }
        }
      ];
    }
  }));
});
