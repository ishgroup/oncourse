import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateRoom } from "../../../js/containers/entities/rooms/epics/EpicCreateRoom";
import { CREATE_ROOM_ITEM_FULFILLED, createRoom } from "../../../js/containers/entities/rooms/actions";

describe("Create room epic tests", () => {
  it("EpicCreateRoom should returns correct values", () => DefaultEpic({
    action: mockedApi => createRoom(mockedApi.db.getRoom(1)),
    epic: EpicCreateRoom,
    processData: () => [
      {
        type: CREATE_ROOM_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New room created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Room" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
