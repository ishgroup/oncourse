import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create assessment epic tests", () => {
  it("EpicCreateAssessment should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.createNewAssessment(), "Assessment"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Assessment")
  }));
});


