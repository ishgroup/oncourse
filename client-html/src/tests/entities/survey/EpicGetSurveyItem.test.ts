import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get survey epic tests", () => {
  it("EpicGetSurveyItem should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getSurvey(1), "Survey"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getSurvey(1), "Survey", state)
  }));
});