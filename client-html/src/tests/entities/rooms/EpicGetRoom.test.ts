import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetRoom } from "../../../js/containers/entities/rooms/epics/EpicGetRoom";
import { GET_ROOM_ITEM_FULFILLED, getRoom } from "../../../js/containers/entities/rooms/actions";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get room epic tests", () => {
  it("EpicGetRoom should returns correct values", () => DefaultEpic({
    action: getRoom("1"),
    epic: EpicGetRoom,
    processData: mockedApi => {
      const room = mockedApi.db.getRoom(1);
      return [
        {
          type: GET_ROOM_ITEM_FULFILLED,
          payload: { room }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: room, name: room.name }
        },
        getNoteItems("Room", "1" as any, LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, room)
      ];
    }
  }));
});
