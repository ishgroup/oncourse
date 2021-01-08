import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { getAccount } from "../../../js/containers/entities/accounts/actions";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetAccount } from "../../../js/containers/entities/accounts/epics/EpicGetAccount";

describe("Get account epic tests", () => {
  it("EpicGetAccount should returns correct values", () => DefaultEpic({
    action: getAccount("1"),
    epic: EpicGetAccount,
    processData: mockedApi => {
      const account = mockedApi.db.getAccount(1);
      return [
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: account, name: `${account.accountCode} ${account.description}` }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, account)
      ];
    }
  }));
});
