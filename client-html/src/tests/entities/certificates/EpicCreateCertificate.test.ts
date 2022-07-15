import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create certificate epic tests", () => {
  it("EpicCreateCertificate should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord("Certificate", mockedApi.db.createNewCertificate()),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Certificate")
  }));
});
