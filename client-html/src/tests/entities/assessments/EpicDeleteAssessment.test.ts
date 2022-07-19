import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete assessment epic tests", () => {
  it("EpicDeleteAssessment should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Assessment"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Assessment")
  }));
});