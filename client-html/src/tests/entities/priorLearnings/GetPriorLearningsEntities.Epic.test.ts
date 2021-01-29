import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get priorLearnings entities epic tests", () => {
  it("GetPriorLearningsEntities should returns correct actions", () =>
    GetEntities("PriorLearning", mockedAPI.db.getPriorLearnings()));
});
