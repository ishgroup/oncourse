import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCreateEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicCreateEntityRecord";
import { createEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Create payslip epic tests", () => {
  it("EpicCreatePayslip should returns correct values", () => DefaultEpic({
    action: mockedApi => createEntityRecord(mockedApi.db.getPayslip(1), "Payslip"),
    epic: EpicCreateEntityRecord,
    processData: () => getProcessDataActions("Payslip")
  }));
});
