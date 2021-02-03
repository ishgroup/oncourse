import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get voucherProduct entities epic tests", () => {
  it("GetVoucherProductEntities should returns correct actions", () =>
    GetEntities("VoucherProduct", mockedAPI.db.getVoucherProducts()));
});
