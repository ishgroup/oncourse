import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { CREATE_BANKING_ITEM_FULFILLED, createBanking } from "../../../js/containers/entities/bankings/actions";
import { EpicCreateBanking } from "../../../js/containers/entities/bankings/epics/EpicCreateBanking";

describe("Create banking epic tests", () => {
  it("EpicCreateBanking should returns correct values", () => DefaultEpic({
    action: mockedApi => createBanking(mockedApi.db.createBankingMock()),
    epic: EpicCreateBanking,
    processData: () => [
      {
        type: CREATE_BANKING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Banking Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Banking", listUpdate: false }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
