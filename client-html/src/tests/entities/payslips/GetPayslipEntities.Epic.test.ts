import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get payslip entities epic tests", () => {
  it("GetPayslipEntities should returns correct actions", () =>
    GetEntities("Payslip", mockedAPI.db.getPayslips()));
});
