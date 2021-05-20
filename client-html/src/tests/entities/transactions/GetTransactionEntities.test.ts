import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get transaction entities epic tests", () => {
  it("GetTransactionEntities should returns correct actions", () =>
    GetEntities("AccountTransaction", mockedAPI.db.getAccountTransactions()));
});
