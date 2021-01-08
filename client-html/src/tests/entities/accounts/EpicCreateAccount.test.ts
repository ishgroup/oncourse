import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateAccount } from "../../../js/containers/entities/accounts/epics/EpicCreateAccount";
import { createAccount } from "../../../js/containers/entities/accounts/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";

describe("Create account epic tests", () => {
  it("EpicCreateAccount should returns correct values", () => DefaultEpic({
    action: mockedApi => createAccount(mockedApi.db.createNewAccount()),
    epic: EpicCreateAccount,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Account Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Account", listUpdate: true }
      },
      clearListNestedEditRecord(0),
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
