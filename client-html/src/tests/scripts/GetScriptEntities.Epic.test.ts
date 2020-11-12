import { mockedAPI } from "../TestEntry";
import { GetEntities } from "../common/GetEntities.Epic";

describe("Get script entities epic tests", () => {
  it("GetScriptEntities should returns correct actions", () => {
    // Expected response
    const scripts = mockedAPI.db.getScripts();

    return GetEntities("Script", scripts);
  });
});
