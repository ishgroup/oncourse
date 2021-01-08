import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { removeAccount } from "../../../js/containers/entities/accounts/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteAccount } from "../../../js/containers/entities/accounts/epics/EpicDeleteAccount";

describe("Delete account epic tests", () => {
  it("EpicDeleteAccount should returns correct values", () => DefaultEpic({
    action: removeAccount("1"),
    epic: EpicDeleteAccount,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Account record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Account", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
