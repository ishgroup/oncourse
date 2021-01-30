import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get qualification entities epic tests", () => {
  it("GetQualificationEntities should returns correct actions", () =>
    GetEntities("Qualification", mockedAPI.db.getQualifications()));
});
