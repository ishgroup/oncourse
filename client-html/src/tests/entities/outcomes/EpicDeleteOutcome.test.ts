import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete outcome epic tests", () => {
  it("EpicDeleteOutcome should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Outcome"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Outcome")
  }));
});