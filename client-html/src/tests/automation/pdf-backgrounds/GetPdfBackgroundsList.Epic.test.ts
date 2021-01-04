import { mockedAPI } from "../../TestEntry";
import { CommonListItem } from "../../../js/model/common/sidebar";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED,
  getAutomationPdfBackgroundsList
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicGetPdfBackgroundsList } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicGetPdfBackgroundsList";

describe("Get pdf backgrounds list epic tests", () => {
  it("EpicGetPdfBackgroundsList should returns correct values", () => DefaultEpic({
    action: getAutomationPdfBackgroundsList(),
    epic: EpicGetPdfBackgroundsList,
    processData: () => {
      const pdfBackgroundsResponse = mockedAPI.db.getReportOverlays();

      const pdfBackgrounds: CommonListItem[] = pdfBackgroundsResponse.rows.map(r => ({
        id: Number(r.id),
        name: r.values[0],
        isPortrait: r.values[1] === "true",
        hasIcon: true
      }));

      return [
        {
          type: GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED,
          payload: { pdfBackgrounds }
        }
      ];
    }
  }));
});
