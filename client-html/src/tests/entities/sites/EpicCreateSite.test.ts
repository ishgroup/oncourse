import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create site epic tests", () => {
  it("EpicCreateSite should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.getSite(1), "Site"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Site")
  }));
});