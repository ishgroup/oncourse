import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get site entities epic tests", () => {
  it("GetSiteEntities should returns correct actions", () => {
    // Expected response
    const sites = mockedAPI.db.getSites();

    return GetEntities("Site", sites);
  });
});
