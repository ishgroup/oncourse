import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete contact epic tests", () => {
  it("EpicDeleteContact should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Contact"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Contact")
  }));
});