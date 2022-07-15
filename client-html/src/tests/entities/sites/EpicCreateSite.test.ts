import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create site epic tests", () => {
  it("EpicCreateSite should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Site", mockedApi.db.getSite(1)),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Site")
  }));
});