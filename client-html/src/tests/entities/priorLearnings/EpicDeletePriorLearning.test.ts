import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete priorLearning epic tests", () => {
  it("EpicDeletePriorLearning should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "PriorLearning"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("PriorLearning")
  }));
});