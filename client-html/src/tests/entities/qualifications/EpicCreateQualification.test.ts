import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create qualification epic tests", () => {
  it("EpicCreateQualification should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Qualification", mockedApi.db.getQualification(1)),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Qualification")
  }));
});