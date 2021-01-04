import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  getAutomationPdfReport,
  getAutomationPdfReportsList,
  updateAutomationPdfReport
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicUpdatePdfReport } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicUpdatePdfReport";

describe("Update pdf report epic tests", () => {
  it("EpicUpdatePdfReport should returns correct values", () => DefaultEpic({
    action: updateAutomationPdfReport({
      id: 1,
      name: "name 1",
      keyCode: "keyCode 1",
      enabled: false
    }),
    epic: EpicUpdatePdfReport,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "PDF report updated" }
      },
      getAutomationPdfReportsList(),
      getAutomationPdfReport(1)
    ]
  }));
});
