import { DefaultEpic } from "../../common/Default.Epic";
import {
  getAutomationPdfReportsList,
  removeAutomationPdfReport
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicRemovePdfReport } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicRemovePdfReport";

describe("Remove pdf report epic tests", () => {
  it("EpicRemovePdfReport should returns correct values", () => DefaultEpic({
    action: removeAutomationPdfReport(1),
    epic: EpicRemovePdfReport,
    processData: () => [
      getAutomationPdfReportsList(true)
    ]
  }));
});
