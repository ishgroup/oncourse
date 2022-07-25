import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicGetEntityRecord";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Get invoice epic tests", () => {
  it("EpicGetInvoice should returns correct values", () => DefaultEpic({
    action: mockedApi => getEntityRecord(mockedApi.db.getInvoice(1), "Invoice"),
    epic: EpicGetEntityRecord,
    processData: (mockedApi, state) => getProcessDataActions(mockedApi.db.getInvoice(1), "Invoice", state)
  }));
});