import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete message epic tests", () => {
  it("EpicDeleteMessage should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Message"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Message")
  }));
});