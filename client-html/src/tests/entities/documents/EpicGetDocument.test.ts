import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get document epic tests", () => {
  it("EpicGetDocument should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getDocument(1), "Document"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getDocument(1), "Document", state)
  }));
});