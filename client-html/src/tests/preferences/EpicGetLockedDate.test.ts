import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED,
  getAccountTransactionLockedDate,
} from "../../js/containers/preferences/actions";
import { EpicGetLockedDate } from "../../js/containers/preferences/epics/EpicGetLockedDate";

describe("Get account transaction locked date epic tests", () => {
  it("EpicGetLockedDate should returns correct values", () => DefaultEpic({
    action: getAccountTransactionLockedDate(),
    epic: EpicGetLockedDate,
    processData: mockedApi => {
      const lockedDate = mockedApi.db.preferencesLockedDate();
      return [
        {
          type: GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED,
          payload: { lockedDate }
        }
      ];
    }
  }));
});
