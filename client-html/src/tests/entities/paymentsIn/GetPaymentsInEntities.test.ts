import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get paymentsIn entities epic tests", () => {
  it("GetPaymentsInEntities should returns correct actions", () =>
    GetEntities("PaymentIn", mockedAPI.db.getPaymentsIn()));
});
