import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get assessment epic tests", () => {
  it("EpicGetAssessment should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getAssessment(1), "Assessment"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getAssessment(1), "Assessment", state)
  }));
});