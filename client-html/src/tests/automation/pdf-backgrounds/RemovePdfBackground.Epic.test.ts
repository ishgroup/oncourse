import { DefaultEpic } from "../../common/Default.Epic";
import {
  getAutomationPdfBackgroundsList,
  REMOVE_AUTOMATION_PDF_BACKGROUND
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicRemovePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicRemovePdfBackground";

describe("Remove pdf background epic tests", () => {
  it("EpicRemovePdfBackground should returns correct values", () =>
    DefaultEpic({
      action: {
        type: REMOVE_AUTOMATION_PDF_BACKGROUND,
        payload: 1
      },
      epic: EpicRemovePdfBackground,
      processData: () => [
        getAutomationPdfBackgroundsList(true)
      ]
    }));
});
