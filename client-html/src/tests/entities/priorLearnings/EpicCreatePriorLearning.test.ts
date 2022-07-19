import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create priorLearning epic tests", () => {
  it("EpicCreatePriorLearning should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.getPriorLearning(1), "PriorLearning"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("PriorLearning")
  }));
});
