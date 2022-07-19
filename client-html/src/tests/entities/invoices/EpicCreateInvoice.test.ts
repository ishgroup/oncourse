import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create invoice epic tests", () => {
  it("EpicCreateInvoice should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord({ ...mockedApi.db.getInvoice(1), id: "" }, "AbstractInvoice"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("AbstractInvoice")
  }));
});
