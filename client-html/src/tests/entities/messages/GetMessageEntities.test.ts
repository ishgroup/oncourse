import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get message entities epic tests", () => {
  it("GetMessageEntities should returns correct actions", () =>
    GetEntities("Message", mockedAPI.db.getMessages()));
});
