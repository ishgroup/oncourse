import { initialize } from "redux-form";
import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import { GET_AUTOMATION_PDF_REPORT } from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicGetPdfReport } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicGetPdfReport";
import { PDF_REPORT_FORM_NAME } from "../../../js/containers/automation/containers/pdf-reports/PdfReports";

describe("Get pdf report epic tests", () => {
  it("EpicGetPdfReport should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_AUTOMATION_PDF_REPORT,
        payload: 1
      },
      epic: EpicGetPdfReport,
      processData: () => {
        const report = mockedAPI.db.getReport(1);
        return [
          initialize(PDF_REPORT_FORM_NAME, report)
        ];
      }
    }));
});
