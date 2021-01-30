import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_ROOM_DELETE_VALIDATION_FULFILLED,
  validateDeleteRoom
} from "../../../js/containers/entities/rooms/actions";
import { EpicValidateDeleteRoom } from "../../../js/containers/entities/rooms/epics/EpicValidateDeleteRoom";

describe("Validate room epic tests", () => {
  it("EpicValidateDeleteRoom should returns correct values", () => DefaultEpic({
    action: validateDeleteRoom("1", () => {}),
    epic: EpicValidateDeleteRoom,
    processData: () => [
      {
        type: GET_ROOM_DELETE_VALIDATION_FULFILLED
      }
    ]
  }));
});
