import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteRoom } from "../../../js/containers/entities/rooms/epics/EpicDeleteRoom";
import { DELETE_ROOM_ITEM_FULFILLED, removeRoom } from "../../../js/containers/entities/rooms/actions";

describe("Delete room epic tests", () => {
  it("EpicDeleteRoom should returns correct values", () => DefaultEpic({
    action: removeRoom("1"),
    epic: EpicDeleteRoom,
    processData: () => [
      {
        type: DELETE_ROOM_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Room deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Room", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
