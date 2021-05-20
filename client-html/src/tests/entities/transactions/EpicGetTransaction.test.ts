import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetTransaction } from "../../../js/containers/entities/transactions/epics/EpicGetTransaction";
import { getTransaction } from "../../../js/containers/entities/transactions/actions";

describe("Get transaction epic tests", () => {
  it("EpicGetTransaction should returns correct values", () => DefaultEpic({
    action: getTransaction("1"),
    epic: EpicGetTransaction,
    processData: mockedApi => {
      const transaction = mockedApi.db.getAccountTransaction(1);
      const name: any = transaction.fromAccount;

      return [
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: transaction, name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, transaction)
      ];
    }
  }));
});
