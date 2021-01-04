import { DefaultEpic } from "../../common/Default.Epic";
import {
  createAutomationPdfReport,
  getAutomationPdfReportsList
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicCreatePdfReport } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicCreatePdfReport";

describe("Create pdf report epic tests", () => {
  it("EpicCreatePdfReport should returns correct values", () => DefaultEpic({
    action: createAutomationPdfReport({
      name: "name 21",
      keyCode: "keyCode 21",
      enabled: true
    }),
    epic: EpicCreatePdfReport,
    processData: () => [
      getAutomationPdfReportsList(false, "keyCode 21")
    ]
  }));
});
