import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get site entities epic tests", () => {
  it("GetSiteEntities should returns correct actions", () =>
    GetEntities("Site", mockedAPI.db.getSites()));
});
