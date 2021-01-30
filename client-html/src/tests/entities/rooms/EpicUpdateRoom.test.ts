import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateRoom } from "../../../js/containers/entities/rooms/epics/EpicUpdateRoom";
import { GET_ROOM_ITEM, UPDATE_ROOM_ITEM_FULFILLED, updateRoom } from "../../../js/containers/entities/rooms/actions";

describe("Update room epic tests", () => {
  it("EpicUpdateRoom should returns correct values", () => DefaultEpic({
    action: mockedApi => updateRoom("1", mockedApi.db.getRoom(1)),
    epic: EpicUpdateRoom,
    processData: () => [
      {
        type: UPDATE_ROOM_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Room updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Room", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_ROOM_ITEM,
        payload: "1"
      }
    ]
  }));
});
