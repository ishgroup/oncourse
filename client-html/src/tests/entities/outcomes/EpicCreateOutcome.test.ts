import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create outcome epic tests", () => {
  it("EpicCreateOutcome should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Outcome", mockedApi.db.getOutcome(1)),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Outcome")
  }));
});
