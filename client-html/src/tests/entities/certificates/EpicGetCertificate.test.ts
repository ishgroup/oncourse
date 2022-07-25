import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get certificate epic tests", () => {
  it("EpicGetCertificate should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getCertificate(1), "Certificate"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getCertificate(1), "Certificate", state)
  }));
});