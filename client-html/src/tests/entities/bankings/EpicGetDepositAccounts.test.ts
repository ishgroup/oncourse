import { change } from "redux-form";
import { format } from "date-fns";
import { DefaultEpic } from "../../common/Default.Epic";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import {
  GET_DEPOSIT_ACCOUNTS_FULFILLED,
  getDepositAccounts
} from "../../../js/containers/entities/bankings/actions";
import { YYYY_MM_DD_MINUSED } from "../../../js/common/utils/dates/format";
import { EpicGetDepositAccounts } from "../../../js/containers/entities/bankings/epics/EpicGetDepositAccounts";
import { IAction } from "../../../js/common/actions/IshAction";
import { FETCH_FAIL } from "../../../js/common/actions";

describe("Get deposit accounts epic tests", () => {
  it("EpicGetDepositAccounts should returns correct values", () => DefaultEpic({
    action: getDepositAccounts(),
    epic: EpicGetDepositAccounts,
    processData: mockedApi => {
      const accounts = mockedApi.db.getAccounts();
      let actions: IAction<any>[];
      if (accounts.length > 0) {
        const account = accounts[0];
        actions = [
          {
            type: GET_DEPOSIT_ACCOUNTS_FULFILLED,
            payload: { accounts, selectedAccountId: account.id }
          },
          change(LIST_EDIT_VIEW_FORM_NAME, "settlementDate", format(new Date(), YYYY_MM_DD_MINUSED))
        ];
      } else {
        actions = [
          {
            type: GET_DEPOSIT_ACCOUNTS_FULFILLED,
            payload: { accounts, selectedAccountId: null }
          },
          {
            type: FETCH_FAIL,
            payload: { message: "There are no items waiting to be banked." }
          }
        ];
      }
      return actions;
    }
  }));
});
