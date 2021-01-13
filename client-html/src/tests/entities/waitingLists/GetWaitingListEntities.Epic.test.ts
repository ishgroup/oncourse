import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get waitingList entities epic tests", () => {
  it("GetWaitingListEntities should returns correct actions", () =>
    GetEntities("WaitingList", mockedAPI.db.getWaitingLists()));
});
