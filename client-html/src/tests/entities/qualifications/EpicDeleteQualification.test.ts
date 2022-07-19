import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete qualification epic tests", () => {
  it("EpicDeleteQualification should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Qualification"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Qualification")
  }));
});