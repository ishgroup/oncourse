import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get invoice entities epic tests", () => {
  it("GetInvoiceEntities should returns correct actions", () =>
    GetEntities("Invoice", mockedAPI.db.getInvoices()));
});
