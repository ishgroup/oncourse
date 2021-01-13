import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get assessment entities epic tests", () => {
  it("GetAssessmentEntities should returns correct actions", () =>
    GetEntities("Assessment", mockedAPI.db.getAssessments()));
});
