import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get outcome epic tests", () => {
  it("EpicGetOutcome should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getOutcome(1), "Outcome"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getOutcome(1), "Outcome", state)
  }));
});