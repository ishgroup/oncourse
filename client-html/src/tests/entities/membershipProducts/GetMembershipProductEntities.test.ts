import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get membership product entities epic tests", () => {
  it("GetMembershipProductEntities should returns correct actions", () =>
    GetEntities("MembershipProduct", mockedAPI.db.getMembershipProducts()));
});
