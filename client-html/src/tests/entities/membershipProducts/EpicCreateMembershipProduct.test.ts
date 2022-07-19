import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create membership product epic tests", () => {
  it("EpicCreateMembershipProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.getMembershipProduct(), "MembershipProduct"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("MembershipProduct")
  }));
});