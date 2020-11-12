import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get waitingList entities epic tests", () => {
  it("GetWaitingListEntities should returns correct actions", () => {
    // Expected response
    const waitingList = mockedAPI.db.getWaitingLists();

    return GetEntities("WaitingList", waitingList);
  });
});
