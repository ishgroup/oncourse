import { mockedAPI } from "../../TestEntry";
import { GetEntities } from "../../common/GetEntities.Epic";

describe("Get enrolment entities epic tests", () => {
  it("GetEnrolmentEntities should returns correct actions", () =>
    GetEntities("Enrolment", mockedAPI.db.getEnrolments()));
});
