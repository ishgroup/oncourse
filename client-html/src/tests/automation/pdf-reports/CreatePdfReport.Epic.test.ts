import { DefaultEpic } from "../../common/Default.Epic";
import {
  CREATE_AUTOMATION_PDF_REPORT,
  getAutomationPdfReportsList
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicCreatePdfReport } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicCreatePdfReport";

describe("Create pdf report epic tests", () => {
  it("EpicCreatePdfReport should returns correct values", () =>
    DefaultEpic({
      action: {
        type: CREATE_AUTOMATION_PDF_REPORT,
        payload: {
          report: {
            name: "name 21",
            keyCode: "keyCode 21",
            enabled: true
          }
        }
      },
      epic: EpicCreatePdfReport,
      processData: () => [
        getAutomationPdfReportsList(false, "keyCode 21")
      ]
    }));
});
