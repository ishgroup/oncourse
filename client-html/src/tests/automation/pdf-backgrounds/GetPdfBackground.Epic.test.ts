import { initialize } from "redux-form";
import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import { GET_AUTOMATION_PDF_BACKGROUND } from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { PDF_BACKGROUND_FORM_NAME } from "../../../js/containers/automation/containers/pdf-backgrounds/PdfBackgrounds";
import { EpicGetPdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicGetPdfBackground";

describe("Get pdf background epic tests", () => {
  it("EpicGetPdfBackground should returns correct values", () =>
    DefaultEpic({
      action: {
        type: GET_AUTOMATION_PDF_BACKGROUND,
        payload: 1
      },
      epic: EpicGetPdfBackground,
      processData: () => {
        const report = mockedAPI.db.getReportOverlay(1);
        return [
          initialize(PDF_BACKGROUND_FORM_NAME, report)
        ];
      }
    }));
});
