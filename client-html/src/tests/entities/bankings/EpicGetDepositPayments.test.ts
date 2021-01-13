import { change } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import {
  GET_DEPOSIT_PAYMENTS_FULFILLED,
  getDepositPayments
} from "../../../js/containers/entities/bankings/actions";
import { IAction } from "../../../js/common/actions/IshAction";
import { FETCH_FAIL } from "../../../js/common/actions";
import { EpicGetDepositPayments } from "../../../js/containers/entities/bankings/epics/EpicGetDepositPayments";

describe("Get deposit payments epic tests", () => {
  it("EpicGetDepositPayments should returns correct values", () => DefaultEpic({
    action: getDepositPayments(1, 200),
    epic: EpicGetDepositPayments,
    processData: mockedApi => {
      const response = mockedApi.db.getDepositPayment();

      const payments = response.map(v => {
        v.selected = true;
        return v;
      });

      let actions: IAction<any>[];

      if (payments.length > 0) {
        actions = [
          {
            type: GET_DEPOSIT_PAYMENTS_FULFILLED,
            payload: { payments }
          },
          change(LIST_EDIT_VIEW_FORM_NAME, "payments", payments)
        ];
      } else {
        actions = [
          {
            type: GET_DEPOSIT_PAYMENTS_FULFILLED,
            payload: { payments }
          },
          change(LIST_EDIT_VIEW_FORM_NAME, "payments", payments),
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
