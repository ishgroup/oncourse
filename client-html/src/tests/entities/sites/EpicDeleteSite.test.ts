import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete site epic tests", () => {
  it("EpicDeleteSite should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Site"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Site")
  }));
});