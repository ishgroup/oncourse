import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete certificate epic tests", () => {
  it("EpicDeleteCertificate should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Certificate"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Certificate")
  }));
});