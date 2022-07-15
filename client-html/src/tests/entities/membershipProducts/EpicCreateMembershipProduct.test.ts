import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create membership product epic tests", () => {
  it("EpicCreateMembershipProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("MembershipProduct", mockedApi.db.getMembershipProduct()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("MembershipProduct")
  }));
});