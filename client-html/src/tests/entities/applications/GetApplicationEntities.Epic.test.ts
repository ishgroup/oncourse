import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get application entities epic tests", () => {
  it("GetApplicationEntities should returns correct actions", () =>
    GetEntities("Application", mockedAPI.db.getApplications()));
});
