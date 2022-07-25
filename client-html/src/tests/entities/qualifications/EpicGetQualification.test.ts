import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get qualification epic tests", () => {
  it("EpicGetQualification should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getQualification(1), "Qualification"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getQualification(1), "Qualification", state)
  }));
});