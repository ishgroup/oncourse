import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED,
  getAutomationPdfReportsList
} from "../../../js/containers/automation/containers/pdf-reports/actions";
import { EpicGetPdfReportsList } from "../../../js/containers/automation/containers/pdf-reports/epics/EpicGetPdfReportsList";
import { CatalogItemType } from "../../../js/model/common/Catalog";
import { mapListToCatalogItem } from "../../../js/common/utils/Catalog";

describe("Get pdf reports list epic tests", () => {
  it("EpicGetPdfReportsList should returns correct values", () => DefaultEpic({
      action: getAutomationPdfReportsList(),
      epic: EpicGetPdfReportsList,
      processData: () => {
        const pdfReportsResponse = mockedAPI.db.getReports();
        const pdfReports: CatalogItemType[] = pdfReportsResponse.rows.map(mapListToCatalogItem);

        pdfReports.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

        return [
          {
            type: GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED,
            payload: { pdfReports }
          }
        ];
      }
    }));
});
