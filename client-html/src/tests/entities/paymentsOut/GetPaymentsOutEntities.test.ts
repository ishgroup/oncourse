import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get paymentOut entities epic tests", () => {
  it("GetPaymentsOutEntities should returns correct actions", () =>
    GetEntities("PaymentOut", mockedAPI.db.getPaymentsOut()));
});
