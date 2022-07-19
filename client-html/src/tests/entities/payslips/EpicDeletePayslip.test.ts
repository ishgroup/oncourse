import { DefaultEpic } from "../../common/Default.Epic";
import { EpicDeleteEntityRecord, getProcessDataActions } from "../../../js/containers/entities/common/epics/EpicDeleteEntityRecord";
import { deleteEntityRecord } from "../../../js/containers/entities/common/actions";

describe("Delete payslip epic tests", () => {
  it("EpicDeletePayslip should returns correct values", () => DefaultEpic({
    action: () => deleteEntityRecord(1, "Payslip"),
    epic: EpicDeleteEntityRecord,
    processData: () => getProcessDataActions("Payslip")
  }));
});