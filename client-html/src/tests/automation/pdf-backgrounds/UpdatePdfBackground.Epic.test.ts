import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_AUTOMATION_PDF_BACKGROUND,
  getAutomationPdfBackgroundsList,
  UPDATE_AUTOMATION_PDF_BACKGROUND
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicUpdatePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicUpdatePdfBackground";

describe("Update pdf background epic tests", () => {
  it("EpicUpdatePdfBackground should returns correct values", () =>
    DefaultEpic({
      action: {
        type: UPDATE_AUTOMATION_PDF_BACKGROUND,
        payload: {
          id: 1,
          fileName: "name 1",
          overlay: {
            name: "name 1",
            lastModified: new Date().getTime(),
            lastModifiedDate: new Date(),
            size: 5176,
            type: "image/png",
            webkitRelativePath: ""
          }
        }
      },
      epic: EpicUpdatePdfBackground,
      processData: () => [
        getAutomationPdfBackgroundsList(),
        {
          type: GET_AUTOMATION_PDF_BACKGROUND,
          payload: 1
        }
      ]
    }));
});
