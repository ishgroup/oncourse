import { Banking } from "@api/model";
import { format } from "date-fns";
import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { getDepositAccounts, initDeposit } from "../../../js/containers/entities/bankings/actions";
import { EpicInitDeposit } from "../../../js/containers/entities/bankings/epics/EpicInitDeposit";
import { YYYY_MM_DD_MINUSED } from "../../../js/common/utils/dates/format";
import { setListEditRecord } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { getAccountTransactionLockedDate } from "../../../js/containers/preferences/actions";

describe("Get deposit init epic tests", () => {
  it("EpicInitDeposit should returns correct values", () => DefaultEpic({
    action: initDeposit(),
    epic: EpicInitDeposit,
    processData: () => {
      const initialValue: Banking = {
        settlementDate: format(new Date(), YYYY_MM_DD_MINUSED)
      };
      return [
        setListEditRecord(initialValue),
        initialize(LIST_EDIT_VIEW_FORM_NAME, initialValue),
        getAccountTransactionLockedDate(),
        getDepositAccounts()
      ];
    }
  }));
});
