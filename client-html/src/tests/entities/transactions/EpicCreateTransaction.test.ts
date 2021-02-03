import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateTransaction } from "../../../js/containers/entities/transactions/epics/EpicCreateTransaction";
import { createTransaction } from "../../../js/containers/entities/transactions/actions";

describe("Create transaction epic tests", () => {
  it("EpicCreateTransaction should returns correct values", () => DefaultEpic({
    action: mockedApi => createTransaction(mockedApi.db.getAccountTransaction(1)),
    epic: EpicCreateTransaction,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Transaction Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "AccountTransaction", listUpdate: true }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, {})
    ]
  }));
});
