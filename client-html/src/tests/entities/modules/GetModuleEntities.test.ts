import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get module entities epic tests", () => {
  it("GetModuleEntities should returns correct actions", () =>
    GetEntities("Module", mockedAPI.db.getModules()));
});
