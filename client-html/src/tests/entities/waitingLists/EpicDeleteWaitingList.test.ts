import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete waitingList epic tests", () => {
  it("EpicDeleteWaitingList should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "WaitingList"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("WaitingList")
  }));
});
