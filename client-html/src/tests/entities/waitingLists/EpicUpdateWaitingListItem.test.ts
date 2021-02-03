import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateWaitingListItem } from "../../../js/containers/entities/waitingLists/epics/EpicUpdateWaitingListItem";
import {
  GET_WAITING_LIST_ITEM,
  UPDATE_WAITING_LIST_ITEM_FULFILLED,
  updateWaitingList
} from "../../../js/containers/entities/waitingLists/actions";

describe("Update waitingList epic tests", () => {
  it("EpicUpdateWaitingListItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateWaitingList("1", mockedApi.db.getWaitingList(1)),
    epic: EpicUpdateWaitingListItem,
    processData: () => [
      {
        type: UPDATE_WAITING_LIST_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Waiting List was updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "WaitingList", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_WAITING_LIST_ITEM,
        payload: "1"
      }
    ]
  }));
});
