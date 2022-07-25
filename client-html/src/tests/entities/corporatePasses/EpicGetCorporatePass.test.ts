import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get corporate pass epic tests", () => {
  it("EpicGetCorporatePass should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getCorporatePass(1), "CorporatePass"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getCorporatePass(1), "CorporatePass", state)
  }));
});