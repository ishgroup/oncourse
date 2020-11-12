import { mockedAPI } from "../TestEntry";
import { GetEntities } from "../common/GetEntities.Epic";

describe("Get audit entities epic tests", () => {
  it("GetAuditEntities should returns correct actions", () => {
    // Expected response
    const audits = mockedAPI.db.getAudits();

    return GetEntities("Audit", audits);
  });
});
