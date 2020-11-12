import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get account entities epic tests", () => {
  it("GetAccountEntities should returns correct actions", () => {
    // Expected response
    const accounts = mockedAPI.db.getAccountList();

    return GetEntities("Account", accounts);
  });
});
