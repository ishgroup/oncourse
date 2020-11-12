import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get room entities epic tests", () => {
  it("GetRoomEntities should returns correct actions", () => {
    // Expected response
    const rooms = mockedAPI.db.getRooms();

    return GetEntities("Room", rooms);
  });
});
