import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get assessment entities epic tests", () => {
  it("GetAssessmentEntities should returns correct actions", () => {
    // Expected response
    const assessment = mockedAPI.db.getAssessments();

    return GetEntities("Assessment", assessment);
  });
});
