import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get sale entities epic tests", () => {
  it("GetSaleEntities should returns correct actions", () =>
    GetEntities("ProductItem", mockedAPI.db.getProductItems()));
});
