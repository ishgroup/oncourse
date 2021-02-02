import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get survey entities epic tests", () => {
  it("GetSurveyEntities should returns correct actions", () =>
    GetEntities("Survey", mockedAPI.db.getSurveys()));
});
