import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete module epic tests", () => {
  it("EpicDeleteModule should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Module"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Module")
  }));
});