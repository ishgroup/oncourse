import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete application epic tests", () => {
  it("EpicDeleteApplication should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Application"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Application")
  }));
});