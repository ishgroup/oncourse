import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get room entities epic tests", () => {
  it("GetRoomEntities should returns correct actions", () =>
    GetEntities("Room", mockedAPI.db.getRooms()));
});
