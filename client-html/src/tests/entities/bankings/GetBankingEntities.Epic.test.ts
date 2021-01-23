import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get banking entities epic tests", () => {
  it("GetBankingEntities should returns correct actions", () => {
    // Expected response
    const banking = mockedAPI.db.getBankings();

    return GetEntities("Banking", banking);
  });
});
