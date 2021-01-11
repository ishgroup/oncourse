import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get application entities epic tests", () => {
  it("GetApplicationEntities should returns correct actions", () => {
    // Expected response
    const application = mockedAPI.db.getApplications();

    return GetEntities("Application", application);
  });
});
