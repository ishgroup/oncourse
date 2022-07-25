import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get priorLearning epic tests", () => {
  it("EpicGetPriorLearning should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getPriorLearning(1), "PriorLearning"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getPriorLearning(1), "PriorLearning", state)
  }));
});