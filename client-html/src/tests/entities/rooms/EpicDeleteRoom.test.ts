import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete room epic tests", () => {
  it("EpicDeleteRoom should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Room"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Room")
  }));
});