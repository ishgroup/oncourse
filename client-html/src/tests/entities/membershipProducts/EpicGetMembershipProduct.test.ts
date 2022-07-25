import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get membership product epic tests", () => {
  it("EpicGetMembershipProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getMembershipProduct(1), "MembershipProduct"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getMembershipProduct(1), "MembershipProduct", state)
  }));
});