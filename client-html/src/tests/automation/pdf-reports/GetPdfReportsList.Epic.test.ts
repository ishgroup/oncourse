import { mockedAPI } from "../../TestEntry";
import { CommonListItem } from "../../../js/model/common/sidebar";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED,
  getAutomationPdfReportsList
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicGetPdfReportsList } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicGetPdfReportsList";

describe("Get pdf reports list epic tests", () => {
  it("EpicGetPdfReportsList should returns correct values", () => DefaultEpic({
    action: getAutomationPdfReportsList(),
    epic: EpicGetPdfReportsList,
    processData: () => {
      const pdfReportsResponse = mockedAPI.db.getReports();

      const pdfReports: CommonListItem[] = pdfReportsResponse.rows.map(r => ({
        id: Number(r.id),
        name: r.values[0],
        keyCode: r.values[1],
        hasIcon: r.values[1].startsWith("ish."),
        grayOut: r.values[2] === "false"
      }));

      return [
        {
          type: GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED,
          payload: { pdfReports }
        }
      ];
    }
  }));
});
