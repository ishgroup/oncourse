import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteWaitingList } from "../../../js/containers/entities/waitingLists/epics/EpicDeleteWaitingList";
import {
  DELETE_WAITING_LIST_ITEM_FULFILLED,
  removeWaitingList
} from "../../../js/containers/entities/waitingLists/actions";

describe("Delete waitingList epic tests", () => {
  it("EpicDeleteWaitingList should returns correct values", () => DefaultEpic({
    action: removeWaitingList("1"),
    epic: EpicDeleteWaitingList,
    processData: () => [
      {
        type: DELETE_WAITING_LIST_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Waiting List deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "WaitingList", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
