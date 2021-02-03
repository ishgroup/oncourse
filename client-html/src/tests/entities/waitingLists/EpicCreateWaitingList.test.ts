import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateWaitingList } from "../../../js/containers/entities/waitingLists/epics/EpicCreateWaitingList";
import {
  CREATE_WAITING_LIST_ITEM_FULFILLED,
  createWaitingList
} from "../../../js/containers/entities/waitingLists/actions";

describe("Create waitingList epic tests", () => {
  it("EpicCreateWaitingList should returns correct values", () => DefaultEpic({
    action: mockedApi => createWaitingList(mockedApi.db.getWaitingList(1)),
    epic: EpicCreateWaitingList,
    processData: () => [
      {
        type: CREATE_WAITING_LIST_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Waiting List created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "WaitingList" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
