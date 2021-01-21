import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get outcome entities epic tests", () => {
  it("GetOutcomeEntities should returns correct actions", () =>
    GetEntities("Outcome", mockedAPI.db.getOutcomes()));
});
