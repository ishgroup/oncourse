import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  createAutomationPdfReport,
  getAutomationPdfReportsList
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import {
  PDF_REPORT_FORM_NAME
} from "../../../js/containers/automation/containers/pdf-reports/PdfReports";
import { EpicCreatePdfReport } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicCreatePdfReport";

const report = {
  name: "name 21",
  keyCode: "keyCode 21",
  status: "Enabled"
};

describe("Create pdf report epic tests", () => {
  it("EpicCreatePdfReport should returns correct values", () => DefaultEpic({
    action: createAutomationPdfReport(report as any),
    epic: EpicCreatePdfReport,
    processData: () => [
      initialize(PDF_REPORT_FORM_NAME, report),
      getAutomationPdfReportsList(false, "keyCode 21")
    ]
  }));
});
